package com.xingyeda.ad.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xingyeda.ad.util.CustomMainBoardUtil;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.LoggerHelper;


/**
 * 屏保启动服务，通过喂狗方式进行
 */
public class SystemRunningMonitorService extends Service {
    private static final String KEY_EXTRA_FEEDDOG = "KEY_EXTRA_FEEDDOG";

    public static void startService(Context context){
        Intent intent = new Intent(context,SystemRunningMonitorService.class);
        context.startService(intent);
    }

    public static void feedDog(Context context){
        Intent intent = new Intent(context,SystemRunningMonitorService.class);
        intent.putExtra(KEY_EXTRA_FEEDDOG,1);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.hasExtra(KEY_EXTRA_FEEDDOG)){
            lastFeedDogTime = System.currentTimeMillis();
            LoggerHelper.i("系统运行监控程序喂狗成功");
        }
        return START_STICKY;
    }
    private long lastFeedDogTime;
    private boolean isThreadRun;
    private Thread feedDogThread;
    private static final long ScreenSaverIntervalMill = 2 * 60 * 1000;
    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.i("系统运行监听服务启动");
        lastFeedDogTime = System.currentTimeMillis();
        isThreadRun = true;
        feedDogThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThreadRun){
                    try {
                        //超过指定时间未收到喂狗指令，则可能程序出现了问题，执行系统重启操作
                        if(System.currentTimeMillis() - lastFeedDogTime >= ScreenSaverIntervalMill){
                            lastFeedDogTime = System.currentTimeMillis();
                            MyLog.i("喂狗服务检测到一定时间内未有吃的，准备重启机器了");
                            CustomMainBoardUtil.reboot(getApplicationContext());
                        }else{
                            Thread.sleep(10 * 1000);
                        }

                    }catch (Exception ex){

                    }
                }
            }
        });
        feedDogThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("系统运行监听服务销毁");
        try {
            isThreadRun = false;
            feedDogThread.interrupt();
            feedDogThread = null;
        }catch (Exception ex){

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
