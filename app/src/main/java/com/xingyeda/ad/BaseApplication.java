package com.xingyeda.ad;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.altang.app.common.utils.LoggerHelper;
import com.lansosdk.videoeditor.LanSoEditor;
import com.liulishuo.filedownloader.FileDownloader;
import com.xingyeda.ad.util.CrashHandler;

import java.io.File;

public class BaseApplication extends Application {

    //public static final String HOST = "192.168.10.114";
    //public static String www = "http://192.168.10.114:8080/xydServer/servlet/";

    public static final String HOST = "120.25.245.234";
    public static String www = "http://120.25.245.234:8080/xydServer/servlet/";
    public static String andoridId;
    /**
     * 是否需要旋转视频
     */
    public static final boolean RotateVideo = true;

    public static int VERSIONCODE = 6;

    public static String HOME = "";
    public static String VEDIO_DOWNLOAD_ROOT_PATH = "";

    public static String VERSION_NAME = "20190614";


    @Override
    public void onCreate() {
        super.onCreate();

        //        andoridId = "c9a44d3e90f2b0f4";//大广告机
//        andoridId = "472481f1f2f9f8ac";//电视机
        //andoridId = Util.getAndroidId(this);
//      测试mac：  49022511e7f2b209，4d564e1762d530d8，da52c5ebae234301
//        andoridId = "49022511e7f2b209";
//        andoridId = "4d564e1762d530d8";
        andoridId = "da52c5ebae234301";


        LoggerHelper.init();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        Context cont = this.getApplicationContext();
        BaseApplication.HOME = cont.getExternalFilesDir("/").getAbsolutePath();
        FileDownloader.setup(this);
        LanSoEditor.initSDK(getApplicationContext(),null);

        initDownloadRootPath(this);

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
        VEDIO_DOWNLOAD_ROOT_PATH = rootPath + File.separator + "XYD_AD" + File.separator + "vedio";
        File rootFilePath = new File(VEDIO_DOWNLOAD_ROOT_PATH);
        if (!rootFilePath.exists()) {
            rootFilePath.mkdirs();
        }
    }
}
