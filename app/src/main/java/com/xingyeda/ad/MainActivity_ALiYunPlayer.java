package com.xingyeda.ad;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.altang.app.common.utils.GsonUtil;
import com.altang.app.common.utils.LoggerHelper;
import com.gavinrowe.lgw.library.SimpleTimerTask;
import com.gavinrowe.lgw.library.SimpleTimerTaskHandler;
import com.xingyeda.ad.logdebug.LogDebugItem;
import com.xingyeda.ad.logdebug.LogDebugUtil;
import com.xingyeda.ad.service.socket.CommandMessageData;
import com.xingyeda.ad.service.socket.CommandReceiveService;
import com.xingyeda.ad.util.RotateTransformation;
import com.xingyeda.ad.util.Util;
import com.xingyeda.ad.vo.AdItem;
import com.xingyeda.ad.vo.AdListResponseData;
import com.xingyeda.ad.vo.MsgInfo;
import com.xingyeda.ad.vo.Version;
import com.xingyeda.ad.vo.VersionInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import presenter.AdPresenter;
import top.wuhaojie.installerlibrary.AutoInstaller;
import view.IAdView;


public class MainActivity_ALiYunPlayer extends BaseActivity {
//    VideoView videoView;
    /**
     * 视频播放
     */
    /**
     * 图片播放
     */
    @BindView(R.id.ad_pic)
    public ImageView pic;

    /**
     * alert 提示
     */
    @BindView(R.id.tips)
    public TextView mTips;

    @BindView(R.id.tv_LogDebug)
    TextView tvLogDebug;
    @BindView(R.id.iv_Defualt)
    ImageView ivDefualt;
    @BindView(R.id.tv_CountSecond)
    TextView tvCountSecond;
    @BindView(R.id.videoViewRootLayout)
    FrameLayout videoViewRootLayout;
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;


    private CountDownTimer countDownTimer;

    private AdPresenter mAdInfoPresenter = null;

    private Unbinder mUnbinder = null;


    private AutoInstaller installer;


    private Handler mHandler;

    private Runnable toNextAdRunnable = new Runnable() {
        @Override
        public void run() {
            playNextAd();
        }
    };
    private AliyunVodPlayer mPlayer;
    //  目前只有全屏
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_aliyunplayer);
        mHandler = new Handler();
        ButterKnife.bind(this);
        surfaceView.setZOrderMediaOverlay(true);

        SurfaceHolder holder = surfaceView.getHolder();
        //增加surfaceView的监听
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                mPlayer.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                                       int height) {
                mPlayer.surfaceChanged();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            }
        });

        mPlayer = new AliyunVodPlayer(this);
        mPlayer.setOnCompletionListener(new IAliyunVodPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                playNextAd();
            }
        });
        mPlayer.setOnErrorListener(new IAliyunVodPlayer.OnErrorListener() {
            @Override
            public void onError(int i, int i1, String s) {
                playNextAd();
            }
        });
        mPlayer.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                pic.setVisibility(View.INVISIBLE);
            }
        });
        mPlayer.setDisplay(surfaceView.getHolder());
        initialization();

        ivDefualt.setImageResource(BaseApplication.RotateVideo ? R.mipmap.bg_defualt_landscape : R.mipmap.bg_defualt_portrait);
        ivDefualt.setVisibility(View.VISIBLE);
        mHandler.postDelayed(toNextAdRunnable, 2 * 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity_ALiYunPlayer.this, MainActivity_ALiYunPlayer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity_ALiYunPlayer.this.startActivity(intent);
                Process.killProcess(Process.myPid());
            }
        }, BaseApplication.AUTO_RESTART_APP_TIME);
    }

    private StringBuffer logStringBuffer = new StringBuffer();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogEvent(LogDebugItem logDebugItem) {
        if (BaseApplication.OpenLogView) {
            logStringBuffer.insert(0, logDebugItem.getMessage() + "\n");
            tvLogDebug.setText(logStringBuffer.toString());
            if(logStringBuffer.length() > 5000){
                logStringBuffer = new StringBuffer();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(CommandMessageData messageData) {
        String command = messageData.getCommond();
        LogDebugUtil.appendLog("接收到服务器心跳命令:" + command);
        //更新数据，增加发送廣告
        if (command.equals("A543")) {
            requestList();
        }
        if (command.equals("A531")) {
            ADListManager.getInstance(getApplicationContext()).setNeedUpdateList();
        }
        //重启
        if (command.equals("A544")) {
        }

        //软件更新
        if (command.equals("A545")) {
            checkVersion();
        }

        //通告更新
        if (command.equals("A547")) {
        }

        if (command.equals("A666")) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        }


        //另一个方向的竖屏
        if (command.equals("A548")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }
        //另一个方向的横屏
        if (command.equals("A551")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }

        //横屏
        if (command.equals("A549")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }

        //竖屏
        if (command.equals("A550")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (!ijkVideoView.onBackPressed()) {
//            super.onBackPressed();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVideo();
//        ijkVideoView.pause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        unregisterReceiver(innerReceiver);
        mUnbinder.unbind();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        ijkVideoView.pause();
        mPlayer.resume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.pause();
    }


    private void requestList() {
        ADListManager.getInstance(getApplicationContext()).setNeedUpdateList();
        mAdInfoPresenter.getAnnouncement(BaseApplication.www + "getEquipmentAnnouncement?etype=2&mac=" + BaseApplication.andoridId);
    }


    private void checkVersion() {
        mAdInfoPresenter.getVersion(BaseApplication.www + "getServerVersionByAD");
    }

    private void register() {
        mAdInfoPresenter = new AdPresenter(this);
        mAdInfoPresenter.onCreate();
        mAdInfoPresenter.attachView(new IAdView() {
            @Override
            public void onError(String result) {
            }

            @Override
            public void onSuccessRegister(ResponseBody result) {
            }


            @Override
            public void onSuccessAnnouncement(MsgInfo result) {
            }

            @Override
            public void onSuccessVersion(VersionInfo result) {
                Version ver = GsonUtil.gson.fromJson(result.getObj(), Version.class);
                if (ver.getVersionNumber() > BaseApplication.VERSIONCODE) {
                    installer.installFromUrl(ver.getUrl());
                }
            }
        });
        //注册机器
        mAdInfoPresenter.register(BaseApplication.www + "insertEqByMac/C?mac=" + BaseApplication.andoridId + "&eq_Version=version" + BaseApplication.VERSIONCODE);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(MainActivity_ALiYunPlayer.this, "返回键无效", Toast.LENGTH_SHORT).show();
            return true;//return true;拦截事件传递,从而屏蔽back键。
        }
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            Toast.makeText(getApplicationContext(), "HOME 键已被禁用...", Toast.LENGTH_SHORT).show();
            return true;//同理
        }
        return super.onKeyDown(keyCode, event);
    }

    //创建广播
    InnerReceiver innerReceiver = new InnerReceiver();

    private void receiverHome() {
        //动态注册广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //启动广播
        registerReceiver(innerReceiver, intentFilter);
    }


    private void initialization() {
        ADListManager.getInstance(this).setOnDataChangeCallBackListener(new ADListManager.OnDataChangeCallBackListener() {
            @Override
            public void dataChanged(AdListResponseData adListResponseData) {
                if (adListResponseData != null && adListResponseData.getObj() != null) {
                    List<AdItem> adItems = new ArrayList<>();
                    adItems.addAll(adListResponseData.getObj());
                    for (AdItem adItem : adItems) {
                        if (!adItem.isFileExsits(BaseApplication.DOWNLOAD_ROOT_PATH)) {
                            DownloadManager.DownloadItem downloadItem = new DownloadManager.DownloadItem();
                            downloadItem.rotateVideo = BaseApplication.RotateVideo;
                            downloadItem.url = adItem.getFileUrl();
                            downloadItem.fileType = adItem.getFiletype();
                            downloadItem.savePath = adItem.locationFile(BaseApplication.DOWNLOAD_ROOT_PATH);
                            DownloadManager.getInstance().downloadWithUrl(downloadItem);
                        }
                    }
                }
            }
        });

        mUnbinder = ButterKnife.bind(this);
        //订阅组件注册
        EventBus.getDefault().register(this);
        //socket
        CommandReceiveService.startService(this);

        //注册键盘
        receiverHome();

        installer = new AutoInstaller.Builder(this)
                .setMode(AutoInstaller.MODE.AUTO_ONLY)
                .build();

        installer.setOnStateChangedListener(new AutoInstaller.OnStateChangedListener() {
            @Override
            public void onStart() {
                // 当后台安装线程开始时回调
            }

            @Override
            public void onComplete() {
                // 当请求安装完成时回调
            }

            @Override
            public void onNeed2OpenService() {
                // 当需要用户手动打开 `辅助功能服务` 时回调
                // 可以在这里提示用户打开辅助功能
                Toast.makeText(MainActivity_ALiYunPlayer.this, "请打开辅助功能服务", Toast.LENGTH_SHORT).show();
            }
        });
        tvLogDebug.setVisibility(BaseApplication.OpenLogView ? View.VISIBLE : View.GONE);

        pic.setVisibility(View.VISIBLE);

        mTips.setText("mac:" + BaseApplication.andoridId + " version:" + BaseApplication.VERSION_NAME + "\nstartTime:" + new Date().toString());

        //初始化视频播放器数据
        //ijkVideoView.setRotation(-90f);
        //pic.setRotation(-90f);
//        MyIjkPlayer mediaPlayer = new MyIjkPlayer(this);

        register();

        checkVersion();
        requestList();
    }

    private void stopVideo() {
        mPlayer.reset();
        mPlayer.stop();
    }


    private void playLocalVideo(File file) {
        String path = "file://" + file.getPath();
        LoggerHelper.i("playLocalVideo:" + path);
        stopVideo();
        mPlayer.setCirclePlay(false);
        mPlayer.setAutoPlay(true);

        AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        alsb.setSource(path);
        AliyunLocalSource localSource = alsb.build();


        mPlayer.prepareAsync(localSource);
        surfaceView.setVisibility(View.VISIBLE);
    }

    private int currentShowAdIndex = -1;

    private synchronized void playNextAd() {
        long delayTime = 0;
        mHandler.removeCallbacks(toNextAdRunnable);
        AdItem adItem = getNextADItem();
        if (adItem == null) {
            ivDefualt.setVisibility(View.VISIBLE);
            pic.setVisibility(View.INVISIBLE);
            surfaceView.setVisibility(View.INVISIBLE);
            stopVideo();
            delayTime = 10000;
            LogDebugUtil.appendLog("暂无可播放的广告");
        } else {
            ivDefualt.setVisibility(View.INVISIBLE);
            if ("2".equals(adItem.getFiletype())) {
//                pic.setVisibility(View.VISIBLE);
                playLocalVideo(new File(BaseApplication.DOWNLOAD_ROOT_PATH, adItem.getLocationFileName()));
            } else {
                surfaceView.setVisibility(View.INVISIBLE);
                pic.setVisibility(View.VISIBLE);
                stopVideo();
                Util.loadImage(mContext, adItem.locationFile(BaseApplication.DOWNLOAD_ROOT_PATH), pic, new RotateTransformation(getApplicationContext(), BaseApplication.RotateVideo ? 270f : 0f));
            }
            delayTime = adItem.getDuration() * 1000;
        }

        mHandler.postDelayed(toNextAdRunnable, delayTime);
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer(delayTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (tvCountSecond != null) {
                    tvCountSecond.setText("剩余" + millisUntilFinished / 1000 + "秒");
                }
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    private synchronized AdItem getNextADItem() {
        AdItem adItem = null;
        List<AdItem> tempAdItemList = new ArrayList<>();
        AdListResponseData adListResponseData = ADListManager.getInstance(getApplicationContext()).getAdListResponseData();

        if (adListResponseData != null && adListResponseData.getObj() != null) {
            tempAdItemList.addAll(adListResponseData.getObj());
        }
        int tempShowIndex = -1;
        for (int index = 0; index < tempAdItemList.size(); index++) {
            AdItem tempAdItem = tempAdItemList.get(index);
            if (index > currentShowAdIndex) {
                if (tempAdItem.isFileExsits(BaseApplication.DOWNLOAD_ROOT_PATH)) {
                    adItem = tempAdItem;
                    tempShowIndex = index;
                    break;
                }
            } else {
                if (adItem == null) {
                    if (tempAdItem.isFileExsits(BaseApplication.DOWNLOAD_ROOT_PATH)) {
                        adItem = tempAdItem;
                        tempShowIndex = index;
                    }
                }
            }
        }
        currentShowAdIndex = tempShowIndex;
        return adItem;
    }

}

