package com.xingyeda.ad.broadcast.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xingyeda.ad.MainApplication;
import com.xingyeda.ad.module.start.StartActivity;
import com.xingyeda.ad.util.MyLog;


/**
 * 软件安装卸载广播
 * @author tangyongx
 * @date 7/12/2018
 */
public class PackageEventBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction()) || Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())){
            String packageName = intent.getDataString();
            MyLog.i("PackageEventBroadcastReceiver-->" + StartActivity.isStarted);
            if(packageName != null && packageName.endsWith(context.getPackageName())){
//                DeviceUtil.reboot(context);

                if(!StartActivity.isStarted){
                    Intent it=new Intent(context,StartActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                }

            }
        }
    }
}
