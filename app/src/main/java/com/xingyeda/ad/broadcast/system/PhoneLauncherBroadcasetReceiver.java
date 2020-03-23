package com.xingyeda.ad.broadcast.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xingyeda.ad.module.start.StartActivity;
import com.xingyeda.ad.util.MyLog;


/**
 * 系统启动，自动运行
 * @author tangyongx
 * @date 7/12/2018
 */
public class PhoneLauncherBroadcasetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MyLog.i("PhoneLauncherBroadcasetReceiver");
        if(!StartActivity.isStarted){
            Intent it=new Intent(context,StartActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(it);
        }
    }
}
