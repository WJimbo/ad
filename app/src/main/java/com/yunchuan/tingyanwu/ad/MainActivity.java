package com.yunchuan.tingyanwu.ad;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yunchuan.tingyanwu.ad.service.socket.CommandReceiveService;
import com.yunchuan.tingyanwu.ad.util.CrashApplication;
import com.yunchuan.tingyanwu.ad.util.FileHelper;
import com.yunchuan.tingyanwu.ad.util.FullScreenVideoView;
import com.yunchuan.tingyanwu.ad.util.Helper;
import com.yunchuan.tingyanwu.ad.util.MainBusiness;
import com.yunchuan.tingyanwu.ad.util.Md5;
import com.yunchuan.tingyanwu.ad.util.SharedPreUtil;
import com.yunchuan.tingyanwu.ad.vo.Ad;
import com.yunchuan.tingyanwu.ad.vo.AdInfo;
import com.yunchuan.tingyanwu.ad.vo.Msg;
import com.yunchuan.tingyanwu.ad.vo.MsgInfo;
import com.yunchuan.tingyanwu.ad.vo.UploadResult;
import com.yunchuan.tingyanwu.ad.vo.Version;
import com.yunchuan.tingyanwu.ad.vo.VersionInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import presenter.AdPresenter;
import presenter.DownloadPresenter;
import rx.functions.Action1;
import view.IAdView;
import view.IDownloadView;
import view.IView;


public class MainActivity extends BaseActivity {
    @BindView(R.id.marquee)
    public TextView marquee;

    @BindView(R.id.v1)
    public FullScreenVideoView mV1;

    @Nullable
    @BindView(R.id.v2)
    public FullScreenVideoView mV2;

    @Nullable
    @BindView(R.id.v3)
    public FullScreenVideoView mV3;

    @BindView(R.id.p1)
    public ImageView mP1;

    @Nullable
    @BindView(R.id.p2)
    public ImageView mP2;

    @Nullable
    @BindView(R.id.p3)
    public ImageView mP3;


    @BindView(R.id.tips)
    public TextView mTips;


    @BindView(R.id.marqueeLayout)
    public LinearLayout mMarqueeLayout;

    private AdPresenter mAdInfoPresenter = null;
    private DownloadPresenter mDownloadPresenter = null;

    private boolean reload = false;  //是否在重启中，不能载入图片

    private String state = "2";

    private String mac = "000";
    private String macAn = "000";


    private Thread thread1 = null;
    private Thread thread2 = null;
    private Thread thread3 = null;
    private Thread thread4 = null;

    private Unbinder mUnbinder = null;

    private Set<String> downloadings = new HashSet<String>();


    MediaPlayer mp = new MediaPlayer();
    private boolean playable = true;  //是否播放背景音乐


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(String command) {
        Log.e("eventbus", command);
Toast.makeText(mContext,"命令"+command,Toast.LENGTH_LONG).show();

        //A543  刷新广告   A549 删除广告    A544 重启   A545  固件更新软件  A546  停止背景音乐  A547 通知增加   A548 通知删除  A550 修改广告（时间）A551 设备分屏改变
        if (command.equals("A543") || command.equals("A549") || command.equals("A550") || command.equals("A551")) {
            reload = true;
            playable = true;

            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                thread1.interrupt();
                thread2.interrupt();
                thread3.interrupt();
                thread4.interrupt();


            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);

        }

        if (command.equals("A546")) {
            playable = false;
            try {
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.reset();
                }
            } catch (Exception e) {
            }
        }

        //重启
        if (command.equals("A544")) {
//            try {
//                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                powerManager.reboot("");
//            } catch (Exception e) {
//                Log.e("main",e.toString());
//            }


            try {
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream out = new DataOutputStream(
                        process.getOutputStream());
                out.writeBytes("reboot \n");
                out.writeBytes("exit\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        //软件更新
        if (command.equals("A545")) {
            try {
                String url = CrashApplication.www + "getServerVersionByAD";
                mAdInfoPresenter.getVersion(url);
            } catch (Exception e) {
            }
        }

        //通知更新
        if (command.equals("A547") || command.equals("A548")) {
            mAdInfoPresenter.getAnnouncement(CrashApplication.www + "getEquipmentAnnouncement?etype=2&mac=" + macAn);

        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        reload = true;

        mUnbinder.unbind();
        Log.e(getTag(), "destroy");

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(getTag(), "resume");

        if (mV1 != null && mV1.getVisibility() == View.VISIBLE) {
            Log.e(getTag(), "v1 resume");
            mV1.start();

        }

        if (mV2 != null && mV2.getVisibility() == View.VISIBLE) {
            mV2.start();
            Log.e(getTag(), " v2 resume");

        }

        if (mV3 != null && mV3.getVisibility() == View.VISIBLE) {
            mV3.start();

            Log.e(getTag(), "v3 resume");

        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(getTag(), "pause");

        if (mV1 != null && mV1.isPlaying()) {
            mV1.pause();
        }

        if (mV2 != null && mV2.isPlaying()) {
            mV2.pause();
        }
        if (mV3 != null && mV3.isPlaying()) {
            mV3.pause();
        }



    }

    //  state 1 三分屏   2  全屏   3 三分之二屏
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = SharedPreUtil.getString(mContext, "state");

        if (state == null) {
            Log.e("main", "state is null");
            state = "2";

        }

        if (state.equals("1"))
            setContentView(R.layout.activity_main1);
        else if (state.equals("2"))
            setContentView(R.layout.activity_main2);
        else
            setContentView(R.layout.activity_main3);


        mAdInfoPresenter = new AdPresenter(this);
        mAdInfoPresenter.onCreate();
        mAdInfoPresenter.attachView(mAdView);

        mDownloadPresenter = new DownloadPresenter(this);
        mDownloadPresenter.onCreate();
        mDownloadPresenter.attachView(mDownloadView);


        mUnbinder = ButterKnife.bind(this);


        mac = MainBusiness.getMacAddress(mContext);
//        mac = "bce68fb77f69";
//        mac = "c894bbb2c0cb";
        macAn = mac;

//        String   macAn = "842096e011db";


        mTips.setText("v:" + CrashApplication.versionCode + "  mac:" + mac);


        SharedPreUtil.put(this, "Mac", mac);
        mAdInfoPresenter.register(CrashApplication.www + "insertEqByMac/C?mac=" + mac + "&eq_Version=version" + CrashApplication.versionCode);

        mAdInfoPresenter.getAdInfo(CrashApplication.www + "GetAdversitingByMac/R?mac=" + mac);


        mAdInfoPresenter.getAnnouncement(CrashApplication.www + "getEquipmentAnnouncement?etype=2&mac=" + macAn);

        EventBus.getDefault().register(this);

        initMp();

        rx.Observable.interval(0, 3600, java.util.concurrent.TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                String url = CrashApplication.www + "getServerVersionByAD";
                Log.e("s", url);
                mAdInfoPresenter.getVersion(url);
            }
        });

        CommandReceiveService.startService(this);
    }

    public void initMp() {


        MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

                Log.e(getTag(),i+"__"+i1);

//                mediaPlayer.prepareAsync();
//                mediaPlayer.start();
                return true;
            }
        };


        MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
           }};




        if (mV1 != null) {
            mV1.setOnErrorListener(errorListener);
            mV1.setOnPreparedListener(preparedListener);
        }
        if (mV2 != null) {
            mV2.setOnErrorListener(errorListener);
            mV2.setOnPreparedListener(preparedListener);
        }
        if (mV3 != null) {
            mV3.setOnErrorListener(errorListener);
            mV3.setOnPreparedListener(preparedListener);
        }

//        if (mV2 != null)
//            mV2.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                @Override
//                public boolean onError(MediaPlayer mp, int what, int extra) {
//                    mV2.stopPlayback();
//                    return true;
//                }
//            });


//        if (mV3 != null)
//            mV3.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
//
//                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//                        @Override
//                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
//                                mV3.setBackgroundColor(Color.TRANSPARENT);
//                            return true;
//                        }
//                    });
//                }
//            });
//
    }

    private IView mDownloadView = new IDownloadView() {
        @Override
        public void onUpload(UploadResult result) {

        }


        //长级文件下载完
        @Override
        public void onDownload(String filename) {
            Log.e("download complete", filename);
            filename = CrashApplication.home + "/" + filename;
            installApk(new File(filename), mContext);
        }


        //视频文件下载完
        @Override
        public void onDownloadVideo(String url, String filename) {
//            try {
//                Thread.sleep(10 * 1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            try {
                Log.e("download video complete", filename);
                filename = CrashApplication.home + "/" + filename;
                File f = new File(filename);
                File d = new File(filename + ".mp4");
                f.renameTo(d);
                f = null;
                d = null;

            } catch (Exception e) {
                Log.e(getTag(), e.toString());
            }


        }


        @Override
        public void onStart() {

        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onUpdate(long progress, long total) {

        }

        @Override
        public void onError(String result) {

        }

        @Override
        public void onVideoError(String downloadUrl) {
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            downloadings.remove(Md5.MD5(downloadUrl));
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        Log.e(getTag(), "on start");

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            String type = (String) b.get("type");
            String url = (String) b.get("url");


            String videoName = "";
            //检查是否是视频，视频需要下载到本地再播放
            if (type.equals("2")) {
                String name = Md5.MD5(url);
                videoName = name + ".mp4";


                File f = new File(CrashApplication.home + "/" + videoName);
                if (!f.exists() && !downloadings.contains(videoName)) {

                    FileHelper h = new FileHelper();
                    h.checkVideoSpace();

                    downloadings.add(videoName);
                    Log.e(getTag(), url + "_name:" + name);
                    mDownloadPresenter.downloadVideo(url, CrashApplication.home, name);
                    return;
                }

                if (!f.exists() ) {
                    return;
                }


                videoName = CrashApplication.home + "/" + videoName;

                Log.e(getTag(), url + "_name check download:" + videoName);

            }


            if (reload)
                return;

            if (msg.what == 1 || msg.what == 4) {
                if (type.equals("0")) {
                    mP1.setVisibility(View.VISIBLE);
                    mV1.setVisibility(View.GONE);
                    Helper.loadImage(mContext, url, mP1);

                }

                if (type.equals("2")) {
                    mV1.stopPlayback();
                    mV1.setVideoPath(videoName);
                    mP1.setVisibility(View.GONE);
                    mV1.setVisibility(View.VISIBLE);
                    mV1.start();


                }

            } else if (msg.what == 2 || msg.what == 6) {


                if (type.equals("0")) {
                    mP2.setVisibility(View.VISIBLE);
                    mV2.setVisibility(View.GONE);
                    Helper.loadImage(mContext, url, mP2);

                }

                if (type.equals("2")) {
                    mV2.stopPlayback();
//                    mV2.setVideoURI(Uri.parse((String) b.get("url")));
//                    String proxyUrl = proxy.getProxyUrl((String) b.get("url"));
//                    mV2.setVideoPath(proxyUrl);
                    mP2.setVisibility(View.GONE);
                    mV2.setVisibility(View.VISIBLE);
                    mV2.setVideoPath(videoName);
                    Log.e(getTag(), url + "_name check play:" + videoName);
                    mV2.start();

                }


            } else if (msg.what == 3) {
                if (type.equals("0")) {
                    mV3.setVisibility(View.GONE);
                    mP3.setVisibility(View.VISIBLE);
                    Helper.loadImage(mContext, url, mP3);
                }

                if (type.equals("2")) {
                    mV3.stopPlayback();
//                    mV3.setVideoURI(Uri.parse((String) b.get("url")));
//                    String proxyUrl = proxy.getProxyUrl((String) b.get("url"));
                    mV3.setVideoPath(videoName);

                    mP3.setVisibility(View.GONE);
                    mV3.setVisibility(View.VISIBLE);
                    mV3.start();
                }

            } else if (msg.what == 7) {//播放背景
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                    }

                    mp.setDataSource(mContext, Uri.parse((String) b.get("url")));
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    Log.e("mp", e.toString());
                    e.printStackTrace();
                }

            }


        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(getTag(), "on stop");

    }

    private IAdView mAdView = new IAdView() {
        @Override
        public void onSuccess(AdInfo adInfo) {

            if (!adInfo.getState().equals(state)) {

                reload = true;
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                SharedPreUtil.put(mContext, "state", adInfo.getState());
                finish();

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else {

                final List<Ad> a1 = new ArrayList<Ad>();
                final List<Ad> a2 = new ArrayList<Ad>();
                final List<Ad> a3 = new ArrayList<Ad>();
                final List<Ad> musics = new ArrayList<Ad>();

                for (Ad p : adInfo.getObj()
                        ) {
//                    测试用的本地视频
//                    if (p.getFiletype().equals("2")) {
//                        p.setFileUrl("http://h.ping2000.com/b.mp4");
//                        p.setDuration(15);
//                    }




                    if (p.getFiletype().equals("1")) {
                        musics.add(p);
                    } else {

                        if (p.getLocation().equals("1") || p.getLocation().equals("5"))
                            a1.add(p);
                        if (p.getLocation().equals("2"))
                            a2.add(p);
                        if (p.getLocation().equals("3"))
                            a3.add(p);

                        if (p.getLocation().equals("4"))
                            a1.add(p);

                        if (p.getLocation().equals("6"))
                            a2.add(p);

                    }


                }

                if (state.equals("2")) {
                    a3.clear();
                    a2.clear();
                }

                if (state.equals("3")) {
                    a3.clear();
                }


                if (!a1.isEmpty()) {
                    thread1 = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            while (true)
                                for (Ad p : a1
                                        ) {

                                    Message msg = Message.obtain();
                                    Bundle b = new Bundle();
                                    b.putString("type", p.getFiletype());
                                    b.putString("url", p.getFileUrl());
                                    msg.setData(b);
                                    msg.what = 1;
                                    handler.sendMessage(msg);

                                    try {
                                        Thread.sleep(p.getDuration() * 1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }


                                    Log.e("a1", "a1");

                                }
                        }
                    });
                    thread1.start();
                }


                if (!a2.isEmpty()) {
                    thread2 = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            while (true)
                                for (Ad p : a2
                                        ) {

                                    Message msg = Message.obtain();
                                    Bundle b = new Bundle();
                                    b.putString("type", p.getFiletype());
                                    b.putString("url", p.getFileUrl());
                                    msg.setData(b);
                                    msg.what = 2;
                                    handler.sendMessage(msg);

                                    try {
                                        Thread.sleep(p.getDuration() * 1000);
                                    } catch (InterruptedException e) {
                                        Log.e("v", e.toString());
                                        e.printStackTrace();
                                    }

                                    Log.e("a2", "a2");

                                }
                        }
                    });
                    thread2.start();
                }

                if (!a3.isEmpty()) {
                    thread3 = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            while (true)
                                for (Ad p : a3
                                        ) {

                                    Message msg = Message.obtain();
                                    Bundle b = new Bundle();
                                    b.putString("type", p.getFiletype());
                                    b.putString("url", p.getFileUrl());
                                    msg.setData(b);
                                    msg.what = 3;
                                    handler.sendMessage(msg);

                                    try {
                                        Thread.sleep(p.getDuration() * 1000);
                                    } catch (InterruptedException e) {
                                        Log.e("v", e.toString());
                                        e.printStackTrace();
                                    }

                                    Log.e("a3", "a3");

                                }
                        }
                    });
                    thread3.start();
                }

                if (!musics.isEmpty()) {
                    thread4 = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            while (true)
                                for (Ad p : musics
                                        ) {

                                    if (playable) {
                                        Message msg = Message.obtain();
                                        Bundle b = new Bundle();
                                        b.putString("type", p.getFiletype());
                                        b.putString("url", p.getFileUrl());
                                        msg.setData(b);
                                        msg.what = 7;
                                        handler.sendMessage(msg);
                                        Log.e("a4", "a4");

                                        try {
                                            Thread.sleep(p.getDuration() * 1000);
                                        } catch (InterruptedException e) {
                                            Log.e("v", e.toString());
                                            e.printStackTrace();
                                        }

                                        try {
                                            if (mp.isPlaying()) {
                                                mp.stop();
                                                mp.reset();
                                            }
                                        } catch (Exception e) {

                                        }


                                    }


                                }
                        }
                    });

                    thread4.start();
                }

            }

            marquee.requestFocus();
        }


        @Override
        public void onError(String result) {
            Log.e("adInfo", result);
            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccessRegister(ResponseBody result) {
            Log.e("register", result.toString());
        }


        @Override
        public void onSuccessAnnouncement(MsgInfo result) {
            Log.e("announcement", "");
            String s = "";
            for (Msg p : result.getObj()
                    ) {
                s += p.getTitle() + ":   " + p.getContent() + "              ";
            }
            if (s.equals(""))
                mMarqueeLayout.setVisibility(View.GONE);
            else
                mMarqueeLayout.setVisibility(View.VISIBLE);

            //marquee.setText(s + "                                                                                                                                                     ");
            marquee.setText(s);
            marquee.requestFocus();

        }

        @Override
        public void onSuccessVersion(VersionInfo result) {
            Log.d("main", result.getObj());
            Gson gson = new Gson();
            Version v = gson.fromJson(result.getObj(), Version.class);
            Log.e("main", v.getVersionNumber());

            if (v.getVersionNumber().compareTo(CrashApplication.versionCode) > 0) {
                if (!v.getFiles().isEmpty()) {
                    String path = v.getServerURL() + v.getFiles().get(0).getFileName();
                    mDownloadPresenter.download2(path, CrashApplication.home, "ad.apk");

                }


            }
        }
    };


    public void onCommandClick(View v) {
        EventBus.getDefault().post("A548");
    }


    /*
       安装App
        */
    public void installApk(File file, Context context) {

        try {
            Uri uri = Uri.fromFile(file);
            Intent localIntent = new Intent(Intent.ACTION_VIEW);
            localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            context.startActivity(localIntent);

            android.os.Process.killProcess(android.os.Process.myPid());
//            Intent intent=new Intent();
//            intent.setAction("android.intent.action.PACKAGE_REPLACED");
//            //发送广播
//            sendBroadcast(intent);


        } catch (Exception e) {
            Log.e("main", e.toString());
        }
    }
}
