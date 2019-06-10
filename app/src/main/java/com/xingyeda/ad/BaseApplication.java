package com.xingyeda.ad;

import android.app.Application;
import android.content.Context;

import com.lansosdk.videoeditor.LanSoEditor;
import com.liulishuo.filedownloader.FileDownloader;
import com.xingyeda.ad.util.CrashHandler;
import com.xingyeda.ad.util.LoggerHelper;

public class BaseApplication extends Application {

//    public static final String HOST = "192.168.10.200";
//    public static String www = "http://192.168.10.200:8080/xydServer/servlet/";

    public static final String HOST = "120.25.245.234";
    public static String www = "http://120.25.245.234:8080/xydServer/servlet/";

    /**
     * 是否需要旋转视频
     */
    public static final boolean RotateVideo = true;

    public static int VERSIONCODE = 6;

    public static String HOME = "";

    public static String VERSION_NAME = "20190529";


    @Override
    public void onCreate() {
        super.onCreate();
        LoggerHelper.init();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        Context cont = this.getApplicationContext();
        BaseApplication.HOME = cont.getExternalFilesDir("/").getAbsolutePath();
        FileDownloader.setup(this);
        LanSoEditor.initSDK(getApplicationContext(),null);

    }
}
