package com.xingyeda.ad;


import android.app.Application;
import android.content.Context;

import android.os.Environment;



import com.lansosdk.videoeditor.LanSoEditor;
import com.liulishuo.filedownloader.FileDownloader;
import com.xingyeda.ad.util.CrashHandler;
import com.xingyeda.ad.util.MyLog;
import com.xingyeda.ad.util.Util;
import com.zz9158.app.common.utils.LoggerHelper;
import com.zz9158.app.common.utils.ToolUtils;

import java.io.File;

public class BaseApplication extends Application {

    //public static final String HOST = "192.168.10.114";
    //public static String www = "http://192.168.10.114:8080/xydServer/servlet/";

    public static final String HOST = "120.25.245.234";
    public static String www = "http://120.25.245.234:8080/xydServer/servlet/";
    public static String andoridId;

    /**
     * 软件自动重启时间间隔
     */
    public static final long AUTO_RESTART_APP_TIME = 40 * 60 * 60 * 1000;

    /**
     * 自动请求广告接口列表间隔时间
     */
    public static final long AUTO_REQUEST_ADLIST_TIME = 5 * 60 * 1000;
    /**
     * 是否需要旋转视频
     */
    public static final boolean RotateVideo = true;
    /**
     * 主界面日志调试接口
     */
    public static final boolean OpenLogView = true;
    public static int VERSIONCODE = 6;

    public static String HOME = "";
    public static String DOWNLOAD_ROOT_PATH = "";

    public static String VERSION_NAME = "20190614";


    @Override
    public void onCreate() {
        super.onCreate();

        //        andoridId = "c9a44d3e90f2b0f4";//大广告机
//        andoridId = "472481f1f2f9f8ac";//电视机
        andoridId = Util.getAndroidId(this);
//      测试mac：  49022511e7f2b209，4d564e1762d530d8，da52c5ebae234301   d0afdfc31e535514
//        andoridId = "49022511e7f2b209";
        andoridId = "4d564e1762d530d8";
//        andoridId = "da52c5ebae234301";
//        andoridId = "d0afdfc31e535514";
        ToolUtils.init(this);
        LoggerHelper.init();
        MyLog.getInstance(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        Context cont = this.getApplicationContext();
        BaseApplication.HOME = cont.getExternalFilesDir("/").getAbsolutePath();
        FileDownloader.setup(this);
        LanSoEditor.initSDK(getApplicationContext(),null);

        initDownloadRootPath(this);
        AliVcMediaPlayer.init(getApplicationContext());
    }

    private void initDownloadRootPath(Context context) {
        String rootPath;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            rootPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            rootPath = context.getFilesDir().getAbsolutePath();
        }
        DOWNLOAD_ROOT_PATH = rootPath + File.separator + "XYD_AD" + File.separator + "download";
        File rootFilePath = new File(DOWNLOAD_ROOT_PATH);
        if (!rootFilePath.exists()) {
            rootFilePath.mkdirs();
        }
    }
}
