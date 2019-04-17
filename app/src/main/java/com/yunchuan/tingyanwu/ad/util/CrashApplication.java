package com.yunchuan.tingyanwu.ad.util;

import android.app.Application;
import android.content.Context;

public class CrashApplication extends Application {

    public static final String HOST = "120.25.245.234";

    //    public static final String HOST = "47.104.162.204";
//    public static final String HOST = "192.168.10.200";
    public static final int SOCKET_PORT = 5888;

    public static String www = "http://120.25.245.234:8080/xydServer/servlet/";

//    public static String www = "http://47.104.162.204:8080/xydServer/servlet/";
//    public static String www = "http://192.168.10.200:8080/xydServer/servlet/";


    //        public static String www = "http://192.168.8.252:3000/data/";
    public static String versionCode = "5";
    public static String home = "";
    public static String videoHome = "";


    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        Context cont = this.getApplicationContext();
        CrashApplication.home = cont.getExternalFilesDir("/").getAbsolutePath();

        CrashApplication.videoHome = CrashApplication.home + "";


    }
}
