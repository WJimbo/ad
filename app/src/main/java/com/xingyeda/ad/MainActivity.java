package com.xingyeda.ad;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.altang.app.common.utils.GsonUtil;
import com.altang.app.common.utils.LoggerHelper;
import com.altang.app.common.utils.ToolUtils;
import com.altang.app.common.utils.UIUtils;
import com.gavinrowe.lgw.library.SimpleTimerTask;
import com.gavinrowe.lgw.library.SimpleTimerTaskHandler;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import presenter.AdPresenter;
import top.wuhaojie.installerlibrary.AutoInstaller;
import view.IAdView;


public class MainActivity extends BaseActivity {
    @BindView(R.id.videoView)
    VideoView videoView;

    /**
     * 视频播放
     */
//    @BindView(R.id.ijkVideoView)
//    public IjkVideoView ijkVideoView;
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


    /**
     * 通告
     */
    @BindView(R.id.marqueeLayout)
    public LinearLayout mMarqueeLayout;


    SimpleTimerTaskHandler timeHandler = SimpleTimerTaskHandler.getInstance();

    private AdPresenter mAdInfoPresenter = null;

    private Unbinder mUnbinder = null;



    private AutoInstaller installer;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(CommandMessageData messageData) {
        String command = messageData.getCommond();
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
        videoView.pause();
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
        videoView.pause();
    }


    @Override
    protected void onPause() {
        super.onPause();
//        ijkVideoView.pause();
        videoView.pause();
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
            Toast.makeText(MainActivity.this, "返回键无效", Toast.LENGTH_SHORT).show();
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
                if(adListResponseData != null && adListResponseData.getObj() != null){
                    List<AdItem> adItems = new ArrayList<>();
                    adItems.addAll(adListResponseData.getObj());
                    for(AdItem adItem : adItems){
                        if("2".equals(adItem.getFiletype()) && !ToolUtils.file().isFileExists(new File(BaseApplication.VEDIO_DOWNLOAD_ROOT_PATH,adItem.getLocationFileName()))){
                            DownloadManager.DownloadItem downloadItem = new DownloadManager.DownloadItem();
                            downloadItem.rotateVideo = BaseApplication.RotateVideo;
                            downloadItem.url = adItem.getFileUrl();
                            downloadItem.savePath = new File(BaseApplication.VEDIO_DOWNLOAD_ROOT_PATH, adItem.getLocationFileName());
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
                Toast.makeText(MainActivity.this, "请打开辅助功能服务", Toast.LENGTH_SHORT).show();
            }
        });



        //显示默认图片
//        ijkVideoView.setVisibility(View.GONE);
        videoView.setZOrderMediaOverlay(true);
//        videoView.setZOrderOnTop(true);
        videoView.setVisibility(View.GONE);
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //屏蔽视频无法播放错误弹出框
                LoggerHelper.i("视频无法播放");
                videoView.stopPlayback();
                return true;
            }
        });
        pic.setVisibility(View.VISIBLE);


        mTips.setText("mac:" + BaseApplication.andoridId + " version:" + BaseApplication.VERSION_NAME);

        //初始化视频播放器数据
        //ijkVideoView.setRotation(-90f);
        //pic.setRotation(-90f);
//        MyIjkPlayer mediaPlayer = new MyIjkPlayer(this);

        register();

        checkVersion();
        requestList();
        //开始请求数据
        //容错，怕偶尔收不到服务器推送，采用轮询的方式获取数据。
        SimpleTimerTask loopTask = new SimpleTimerTask(60 * 60 * 1000) {
            @Override
            public void run() {
                requestList();
            }
        };
        timeHandler.sendTask(1, loopTask);
    }

    private void playLocalVideo(String url) {
        String path = "file://" + url;
//        ijkVideoView.setUrl(path);
        LoggerHelper.i("playLocalVideo:" + path);
        videoView.stopPlayback();
        videoView.setVideoPath(path);
        videoView.start();
    }


    private void playRemoteVideo(String url) {
        Log.i("remote", url);
//        ijkVideoView.setUrl(url);
        videoView.stopPlayback();
        videoView.setVideoURI(Uri.parse(url));
        videoView.start();

    }

    private void playVideo() {
        videoView.stopPlayback();
        videoView.start();
    }


    //  目前只有全屏
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        initialization();
        Util.defaultImage(MainActivity.this, pic, new RotateTransformation(this,270));
        startPlayThread();
    }
    private int currentShowAdIndex = 0;
    private void startPlayThread() {
        Thread playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        try {
                            List<AdItem> tempAdItemList = new ArrayList<>();
                            AdListResponseData adListResponseData = ADListManager.getInstance(getApplicationContext()).getAdListResponseData();

                            if(adListResponseData != null && adListResponseData.getObj() != null){
                                tempAdItemList.addAll(adListResponseData.getObj());
                            }
                            if(currentShowAdIndex < 0 || currentShowAdIndex >= tempAdItemList.size()){
                                currentShowAdIndex = 0;
                            }
                            int index = currentShowAdIndex;
                            AdItem showAdItem = null;
                            for(; index < tempAdItemList.size();index++){
                                final AdItem adItem = tempAdItemList.get(index);
                                final String fileType = adItem.getFiletype();
                                if("2".equals(adItem.getFiletype())){
                                    File file = new File(BaseApplication.VEDIO_DOWNLOAD_ROOT_PATH,adItem.getLocationFileName());
                                    if(!file.exists()){
                                        continue;
                                    }
                                }
                                showAdItem = adItem;
                                UIUtils.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(pic != null){
                                            if ("0".equals(fileType)) {
                                                pic.setVisibility(View.VISIBLE);
                                                videoView.setVisibility(View.GONE);
                                                videoView.stopPlayback();
                                                Util.loadImage(mContext, adItem.getFileUrl(), pic, new RotateTransformation(getApplicationContext(), 270f));
                                            } else {
                                                pic.setVisibility(View.GONE);
                                                videoView.setVisibility(View.VISIBLE);
                                                playLocalVideo(new File(BaseApplication.VEDIO_DOWNLOAD_ROOT_PATH,adItem.getLocationFileName()).getPath());
                                            }
                                        }

                                    }
                                });

                                break;
                            }
                            currentShowAdIndex = index;
                            if(showAdItem != null){
                                Thread.sleep(showAdItem.getDuration() * 1000);
                            }else{
                                Thread.sleep(1);
                            }
                            currentShowAdIndex++;
                        }catch (Exception ex){

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        playThread.start();
    }
}

