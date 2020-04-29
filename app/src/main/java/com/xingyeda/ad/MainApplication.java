package com.xingyeda.ad;


import android.app.Application;

import com.lansosdk.videoeditor.LanSoEditor;
import com.liulishuo.filedownloader.FileDownloader;
import com.xingyeda.ad.module.ad.data.DownloadManager;
import com.xingyeda.ad.util.CrashHandler;
import com.xingyeda.ad.util.MyLog;
import com.xingyeda.ad.util.httputil.TokenMananger;
import com.zz9158.app.common.utils.ApplicationUtil;
import com.zz9158.app.common.utils.LoggerHelper;
import com.zz9158.app.common.utils.ToolUtils;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG){
//            DeviceUUIDManager.setUUID("6c21a218fb77");
//            DeviceUUIDManager.setUUID("73e4a4fda9a9f73c");
//            DeviceUUIDManager.setUUID("c44cede2765a169b");
//            DeviceUUIDManager.setUUID("10d07a031686");
        }
        if(ApplicationUtil.isMainProcess(this)) {
            TokenMananger.getInstance().init(this,"admin","1");
            DownloadManager.getInstance().setContext(this);
            ToolUtils.init(this);
            LoggerHelper.init();
            MyLog.getInstance(this);
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
            FileDownloader.setup(this);
            LanSoEditor.initSDK(getApplicationContext(),null);
        }
    }

}
