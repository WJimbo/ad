package com.xingyeda.ad;


import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.support.multidex.MultiDex;

import com.lansosdk.videoeditor.LanSoEditor;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.leakcanary.LeakCanary;
import com.xingyeda.ad.config.DeviceUUIDManager;
import com.xingyeda.ad.module.ad.data.DownloadManager;
import com.xingyeda.ad.module.main.OneADMainActivity;
import com.xingyeda.ad.module.start.StartActivity;
import com.xingyeda.ad.util.CrashHandler;
import com.xingyeda.ad.util.MyLog;
import com.xingyeda.ad.util.httputil.TokenMananger;
import com.zz9158.app.common.utils.ApplicationUtil;
import com.zz9158.app.common.utils.LoggerHelper;
import com.zz9158.app.common.utils.ToolUtils;

public class MainApplication extends Application {
    private boolean isStarted = false;
    private static boolean isStaticStartedFlag = false;
    private static final String SP_KEY_LAST_APPLICATION_LOCATION = "SP_KEY_LAST_APPLICATION_LOCATION";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG){
//            DeviceUUIDManager.setUUID("6ce8c64270c0");
//            DeviceUUIDManager.setUUID("6c21a218fb77");
//            DeviceUUIDManager.setUUID("73e4a4fda9a9f73c");
//            DeviceUUIDManager.setUUID("c44cede2765a169b");
//            DeviceUUIDManager.setUUID("bb4abd3a345c8d0b");
//            DeviceUUIDManager.setUUID("7cc8d66b13fc0063");
//            DeviceUUIDManager.setUUID("dda29da822020797");
//            DeviceUUIDManager.setUUID("10d07aaa9ab4");
        }
        if (LeakCanary.isInAnalyzerProcess(this)) {
            //此过程专用于LeakCanary进行堆分析。在此过程中不应初始化应用程序。
            return;
        }
        LeakCanary.install(this);
        if(ApplicationUtil.isMainProcess(this)) {
            LoggerHelper.init();
            ToolUtils.init(this);
            MyLog.getInstance(this);
            MyLog.delBefore7LogFiles();
            MyLog.i(new StringBuilder("MainApplication onCreate ->")
                    .append(this.toString())
                    .append(" SystemClockTime:")
                    .append(SystemClock.elapsedRealtime())
                    .append("  isStarted:")
                    .append(isStarted)
                    .append(" staticStartedFlag:")
                    .append(isStaticStartedFlag)
                    .append(" isAppAlive:")
                    .append(ApplicationUtil.isAppAlive(this))
                    .append(" isActivityForeground:")
                    .append(ApplicationUtil.isActivityForeground(this, OneADMainActivity.class.getName()))
                    .toString());
            String lastApplicationInstanceLocation = ToolUtils.sp().getShareString(SP_KEY_LAST_APPLICATION_LOCATION);//上一次启动Application的内存地址
            String currentApplicationInstanceLocation = this.toString();
            if(isStarted || isStaticStartedFlag
                    || currentApplicationInstanceLocation.equals(lastApplicationInstanceLocation)){
                MyLog.i("Application异常被重启");
                ToolUtils.sp().saveShareString(SP_KEY_LAST_APPLICATION_LOCATION,"");
                ApplicationUtil.restartApp(this, StartActivity.class);
                return;
            }
            ToolUtils.sp().saveShareString(SP_KEY_LAST_APPLICATION_LOCATION,currentApplicationInstanceLocation);

            TokenMananger.getInstance().init(this, DeviceUUIDManager.generateUUID(this),"1");
            DownloadManager.getInstance().setContext(this);


            if(!BuildConfig.DEBUG){
                CrashHandler crashHandler = CrashHandler.getInstance();
                crashHandler.init(getApplicationContext());
            }

            FileDownloader.setup(this);
            LanSoEditor.initSDK(getApplicationContext(),null);
            isStarted = true;
            isStaticStartedFlag = true;
        }
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        MyLog.i("onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        MyLog.i("onTrimMemory ->" + level);
        if(level == TRIM_MEMORY_RUNNING_MODERATE
                || level == TRIM_MEMORY_RUNNING_LOW
                || level == TRIM_MEMORY_RUNNING_CRITICAL) {//内存不足(后台进程不足5个)，并且该进程优先级比较高，需要清理内存
//            CustomMainBoardUtil.reboot(getApplicationContext(),"内存不足重启");
//            Glide.get(getApplicationContext()).clearMemory();
        }
    }
}
