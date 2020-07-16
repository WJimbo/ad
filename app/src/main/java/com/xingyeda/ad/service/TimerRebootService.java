package com.xingyeda.ad.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xingyeda.ad.util.CustomMainBoardUtil;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.ToolUtils;

import java.util.Timer;
import java.util.TimerTask;

public class TimerRebootService extends Service {
    public static void startService(Context context){
        Intent intent = new Intent(context,TimerRebootService.class);
        context.startService(intent);
    }
    private Timer mTimer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cancelTime();
        mTimer = new Timer();
        String scheduleDateTimeStr = ToolUtils.time().getNowTimeString("yyyy-MM-dd") + " 23:59:55";
        MyLog.i("定时重启服务已启动 系统重启时间:" + scheduleDateTimeStr);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                MyLog.i("定时服务->准备重启机器");
                CustomMainBoardUtil.reboot(getApplicationContext());
            }
        }, ToolUtils.time().string2Date(scheduleDateTimeStr,"yyyy-MM-dd HH:mm:ss"));
    }
    private void cancelTime(){
        if(mTimer != null){
            try {
                mTimer.cancel();
                mTimer = null;
            }catch (Exception ex){

            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTime();
        ToastUtils.showToast(this,"定时重启服务已销毁");
    }
}
