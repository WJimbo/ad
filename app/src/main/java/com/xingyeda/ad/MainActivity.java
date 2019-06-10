package com.xingyeda.ad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.gavinrowe.lgw.library.SimpleTimerTask;
import com.gavinrowe.lgw.library.SimpleTimerTaskHandler;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.mazouri.tools.Tools;
import com.xingyeda.ad.service.socket.CommandMessageData;
import com.xingyeda.ad.service.socket.CommandReceiveService;
import com.xingyeda.ad.util.GsonUtil;
import com.xingyeda.ad.util.LoggerHelper;
import com.xingyeda.ad.util.RotateTransformation;
import com.xingyeda.ad.util.Util;
import com.xingyeda.ad.vo.Ad;
import com.xingyeda.ad.vo.AdInfo;
import com.xingyeda.ad.vo.MsgInfo;
import com.xingyeda.ad.vo.Version;
import com.xingyeda.ad.vo.VersionInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    private VideoEditor videoEditor;
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

    @BindView(R.id.version)
    public TextView mVersion;

    @BindView(R.id.mac)
    public TextView mMac;


    /**
     * 通告
     */
    @BindView(R.id.marqueeLayout)
    public LinearLayout mMarqueeLayout;


    SimpleTimerTaskHandler timeHandler = SimpleTimerTaskHandler.getInstance();

    private AdPresenter mAdInfoPresenter = null;

    private Unbinder mUnbinder = null;

    private String andoridId;

    private AutoInstaller installer;


    private void remove(Integer id) {
        Iterator<Integer> iterator = DATAS.keySet().iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            if (key.equals(id)) {
                iterator.remove();
            }
        }
    }

    private void delete(String id) {
        AdEntity ad = dbUtil.get(id);
        remove(Integer.valueOf(id));
        if (ad == null) return;
        String path = ad.getLocalUrl();
        new File(path).delete();
        dbUtil.delete(ad);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(CommandMessageData messageData) {
        String command = messageData.getCommond();
        //更新数据，增加发送廣告
        if (command.equals("A543")) {
            data();
        }
        if (command.equals("A531")) {
            delete(messageData.getContent());
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


    private void data() {
        buildData();
        mAdInfoPresenter.getAdInfo(BaseApplication.www + "GetAdversitingByMac/R?mac=" + andoridId);
        mAdInfoPresenter.getAnnouncement(BaseApplication.www + "getEquipmentAnnouncement?etype=2&mac=" + andoridId);
    }


    private void checkVersion() {
        mAdInfoPresenter.getVersion(BaseApplication.www + "getServerVersionByAD");
    }

    private void register() {
        mAdInfoPresenter = new AdPresenter(this);
        mAdInfoPresenter.onCreate();
        mAdInfoPresenter.attachView(mAdView);
        //注册机器
        mAdInfoPresenter.register(BaseApplication.www + "insertEqByMac/C?mac=" + andoridId + "&eq_Version=version" + BaseApplication.VERSIONCODE);
    }

    private DBUtil dbUtil;


    private void buildData() {
        DATAS.clear();
        List<AdEntity> localData = dbUtil.list();
        for (AdEntity entity : localData) {
            if (new File(entity.getLocalUrl()).exists()) {
                DATAS.put(entity.getId(), Util.entityToPo(entity));
            }
        }

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

    private void receiverHome() {

        //创建广播
        InnerReceiver innerReceiver = new InnerReceiver();
        //动态注册广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //启动广播
        registerReceiver(innerReceiver, intentFilter);
    }


    private void initialization() {

        mUnbinder = ButterKnife.bind(this);
        //订阅组件注册
        EventBus.getDefault().register(this);
        //socket
        CommandReceiveService.startService(this);

        //注册键盘
        receiverHome();


        dbUtil = DBUtil.getInstance(this);
        dbUtil.initialization();


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

//        andoridId = "c9a44d3e90f2b0f4";//大广告机
        andoridId = "472481f1f2f9f8ac";//电视机
        //andoridId = Util.getAndroidId(this);

        LoggerHelper.i("andoridId : " + andoridId);

        //显示默认图片
//        ijkVideoView.setVisibility(View.GONE);
        videoView.setZOrderMediaOverlay(true);
//        videoView.setZOrderOnTop(true);
        videoView.setVisibility(View.GONE);
        pic.setVisibility(View.VISIBLE);


        mVersion.setText("version:" + BaseApplication.VERSION_NAME);
//        mVersion.setRotation(270);
        mMac.setText("mac:" + andoridId);
        mTips.setText("时间..");

        //初始化视频播放器数据
        //ijkVideoView.setRotation(-90f);
        //pic.setRotation(-90f);
        MyIjkPlayer mediaPlayer = new MyIjkPlayer(this);
//        mediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
//                if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
//                    //这里返回了视频旋转的角度，根据角度旋转视频到正确的画面
//                    if (ijkVideoView != null) {
//                        ijkVideoView.setRotation(extra);
//                    }
//                }
//                return false;
//            }
//        });
//        ijkVideoView.setCustomMediaPlayer(mediaPlayer);
//
//        ijkVideoView.setVideoController(null); //
//        ijkVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
//            @Override
//            public void onPlayerStateChanged(int playerState) {
//                switch (playerState) {
//                    case IjkVideoView.PLAYER_NORMAL://小屏
//                        break;
//                    case IjkVideoView.PLAYER_FULL_SCREEN://全屏
//                        break;
//                }
//            }
//
//            @Override
//            public void onPlayStateChanged(int playState) {
//                switch (playState) {
//                    case IjkVideoView.STATE_IDLE:
//                        break;
//                    case IjkVideoView.STATE_PREPARING:
//                        break;
//                    case IjkVideoView.STATE_PREPARED:
//                        break;
//                    case IjkVideoView.STATE_PLAYING:
//                        break;
//                    case IjkVideoView.STATE_PAUSED:
//                        break;
//                    case IjkVideoView.STATE_BUFFERING:
//                        break;
//                    case IjkVideoView.STATE_BUFFERED:
//                        break;
//                    case IjkVideoView.STATE_PLAYBACK_COMPLETED:
//                        ijkVideoView.start();
//                        break;
//                    case IjkVideoView.STATE_ERROR:
//                        break;
//                }
//            }
//        });

        register();

        checkVersion();

        //开始请求数据
        //容错，怕偶尔收不到服务器推送，采用轮询的方式获取数据。
        SimpleTimerTask loopTask = new SimpleTimerTask(60 * 60 * 1000) {
            @Override
            public void run() {
                data();
            }
        };
        timeHandler.sendTask(1, loopTask);
    }

    private void playLocalVideo(String url) {
        String path = "file://" + url;
//        ijkVideoView.setUrl(path);
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
//        ijkVideoView.stopPlayback();
//        //ijkVideoView.setLooping(true);
//        ijkVideoView.setPlayOnMobileNetwork(true);
//        ijkVideoView.start();
    }


    //  目前只有全屏
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        initialization();
    }

    private void play() {
        Util.defaultImage(MainActivity.this, pic);
        Thread playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        for (Map.Entry<Integer, Ad> entry : DATAS.entrySet()) {
                            Ad info = entry.getValue();
                            Message msg = Message.obtain();
                            Bundle b = new Bundle();
                            b.putString("type", info.getFiletype());
                            b.putString("url", info.getFileUrl());
                            b.putString("md5", info.getMD5());
                            msg.setData(b);
                            msg.what = 1;
                            handler.sendMessage(msg);
                            Thread.sleep(info.getDuration() * 1000);
                        }
                        Thread.sleep(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        playThread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void continueDownLoad(BaseDownloadTask task) {
        while (task.getSmallFileSoFarBytes() != task.getSmallFileTotalBytes()) {
            int percent = (int) ((double) task.getSmallFileSoFarBytes() / (double) task.getSmallFileTotalBytes() * 100);
        }
    }

    private void download(final Ad ad) {
        Toast.makeText(getApplicationContext(), "正在下载视频", Toast.LENGTH_LONG).show();
        String vName = UUID.randomUUID().toString();
        final String path = getExternalFilesDir("/").getAbsolutePath() + "/video/" + vName + ".mp4";
        FileDownloader.getImpl().create(ad.getFileUrl())
                .setPath(path)
                //.setForceReDownload(true)
                .setAutoRetryTimes(5)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LoggerHelper.i("开始下载文件 : " + ad.getFileUrl());
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Log.i("download", "资源地址链接成功");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Toast.makeText(getApplicationContext(), "下载进度(" + soFarBytes + "/" + totalBytes + ")", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    //下载成功
                    @Override
                    protected void completed(BaseDownloadTask task) {

                        if (videoEditor == null) {
                            videoEditor = new VideoEditor();
                        }
                        LoggerHelper.i("下载完成 : " + ad.getFileUrl());
                        ad.setFileUrl(path);
                        if (videoEditor == null) {
                            videoEditor = new VideoEditor();
                            videoEditor.setOnProgessListener(new onVideoEditorProgressListener() {
                                @Override
                                public void onProgress(VideoEditor v, int percent) {
                                    if (mProgressDialog != null) {
                                        mProgressDialog.setMessage("正在处理中..." + String.valueOf(percent) + "%");
                                    }
                                }
                            });
                        }
                        Toast.makeText(getApplicationContext(), "正在旋转视频", Toast.LENGTH_LONG).show();
                        new SubAsyncTask(ad, path).execute();
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Toast.makeText(getApplicationContext(), "下载出错", Toast.LENGTH_LONG).show();
                        Log.i("download", "下载出错了:" + e.getLocalizedMessage());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        continueDownLoad(task);
                    }
                }).start();
    }

    private ProgressDialog mProgressDialog;

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("正在处理中...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void calcelProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    /**
     * 异步执行
     */
    public class SubAsyncTask extends AsyncTask<Object, Object, Boolean> {
        private Ad ad;
        private String path;

        public SubAsyncTask(Ad ad, String path) {
            this.ad = ad;
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected synchronized Boolean doInBackground(Object... params) {
            //修改视频元数据
            String dstVideo = null;
            dstVideo = videoEditor.executeSetVideoMetaAngle(path, 270);
            if (dstVideo == null) {
                //旋转视频
                dstVideo = videoEditor.executeVideoRotate90Clockwise(path);
//                            dstVideo = videoEditor.executeVideoRotate90CounterClockwise(path);
            }
            if (dstVideo != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "旋转视频成功", Toast.LENGTH_LONG).show();
                    }
                });

                Tools.file().deleteFile(path);
                Tools.file().moveFile(dstVideo, path);
                ad.setFileUrl(path);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "旋转视频失败", Toast.LENGTH_LONG).show();
                    }
                });

            }


            DATAS.put(ad.getId(), ad);

            Util.sortMapByKey(DATAS);


            AdEntity entity = adInfoToEntity(ad);
            entity.setState("0");
            if (dbUtil.get(String.valueOf(entity.getId())) == null) {
                dbUtil.save(entity);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            calcelProgressDialog();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (pic == null) return;
            Bundle data = msg.getData();
            String type = (String) data.get("type");
            String url = (String) data.get("url");
            //String md5 = (String) data.get("md5");

            if (url == null || "".equals(url)) return;

            //LoggerHelper.i("---file md5:" + md5 + "---- 正在播放文件 : " + url);

            if ("0".equals(type)) {
                pic.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                videoView.stopPlayback();
//                ijkVideoView.setVisibility(View.GONE);
//                ijkVideoView.stopPlayback();
                Util.loadImage(mContext, url, pic, new RotateTransformation(getApplicationContext(), 270f));
            } else {
                pic.setVisibility(View.GONE);
//                ijkVideoView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.VISIBLE);
                playLocalVideo(url);
            }

        }
    };

    @Override
    protected void onStop() {
        super.onStop();

    }

    private ConcurrentHashMap<Integer, Ad> DATAS = new ConcurrentHashMap<Integer, Ad>();

    private AdEntity adInfoToEntity(Ad ad) {
        AdEntity entity = new AdEntity();
        entity.setId(ad.getId());
        entity.setMD5(ad.getMD5());
        entity.setLocalUrl(ad.getFileUrl());
        entity.setDuration(ad.getDuration());
        entity.setState("1");
        return entity;
    }

    /**
     * 获取网络数据后，处理函数
     */
    private IAdView mAdView = new IAdView() {
        @Override
        public void onSuccess(AdInfo adInfo) {
            List<Ad> list = adInfo.getObj();
//            Ad videoAD = new Ad();
//            videoAD.setFiletype("2");
//            videoAD.setFileUrl("http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4");
//            videoAD.setId(1333);
//            videoAD.setDuration(30);
//            list.add(videoAD);
            for (Ad ad : list) {
                if (!"2".equals(ad.getFiletype())) {
                    DATAS.put(ad.getId(), ad);//优先将图片放入播放列表
                } else {
                    //视频是否已经下载完毕，若下载完毕该map会保存,若没有则开始下载
                    if (!DATAS.keySet().contains(ad.getId())) {
                        download(ad);//从网络下上下视频
                    }
                }
            }
            Util.sortMapByKey(DATAS);
            play();
            //LoggerHelper.i("接口返回返回数据大小 : " + DATAS.size());
        }


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
    };

}

