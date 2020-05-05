package com.xingyeda.ad.util;

import android.content.Context;
import android.content.Intent;

import com.zz9158.app.common.utils.ToolUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * 定制化主板的功能
 */
public class CustomMainBoardUtil {

    /**
     * 定时开关机
     * @param context
     * @param powerOn
     * @param powerOff
     */
    public static void powerOffAndOn(Context context,String powerOff,String powerOn){
        MyLog.i("设置定时关机：" + ToolUtils.string().nullStrToEmpty(powerOff) + "  定时开机:" + powerOn);
        String datePatten = "yyyy-MM-dd HH:mm:ss";

        //开机时间，int数组，如int[] timeonArray = {2014, 10, 1, 8, 30};//下次开机具体日期时间，即在2014.10.1 8:30开机
        int[] timeonArray = new int[]{1970, 1, 1, 0, 0};
        //关机时间，int数组，如int[] timeoffArray = {2014, 9, 1, 8, 30};//下次关机具体日期时间，即在2014.9.1 8:30关机
        int[] timeoffArray = new int[]{1970, 1, 1, 0, 0};
        //使能开关机功能，true开启，false关闭
        boolean enable = true;

        if(ToolUtils.string().isEmpty(powerOff) || ToolUtils.string().isEmpty(powerOn)){
            enable = false;
        }else{
            enable = true;
            String currentDateStr = ToolUtils.time().date2String(new Date(),"yyyy-MM-dd");
            if(powerOff.contains("0000-00-00")){
                powerOff = powerOff.replace("0000-00-00",currentDateStr);

                long time = ToolUtils.time().string2Date(powerOff,datePatten).getTime();
                if(System.currentTimeMillis() > time){
                    time = time + 24 * 60 * 60 * 1000;
                }
                powerOff = ToolUtils.time().date2String(new Date(time),datePatten);

            }
            if(powerOn.contains("0000-00-00")){
                powerOn = powerOn.replace("0000-00-00",currentDateStr);
                long time = ToolUtils.time().string2Date(powerOn,datePatten).getTime();
                if(System.currentTimeMillis() > time){
                    time = time + 24 * 60 * 60 * 1000;
                }
                powerOn = ToolUtils.time().date2String(new Date(time),datePatten);
            }

            Calendar calendar = Calendar.getInstance();
            if(!ToolUtils.string().isEmpty(powerOff)){

                calendar.setTimeInMillis(ToolUtils.time().string2Millis(powerOff,datePatten));
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                timeoffArray = new int[]{year,month,day,hour,minute};
            }

            if(!ToolUtils.string().isEmpty(powerOn)){
                calendar.setTimeInMillis(ToolUtils.time().string2Millis(powerOn,datePatten));
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                timeonArray = new int[]{year,month,day,hour,minute};
            }

        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.setpoweronoff");
        intent.putExtra("timeon", timeonArray);
        intent.putExtra("timeoff", timeoffArray);
        intent.putExtra("enable", enable);
        context.sendBroadcast(intent);
    }

    /**
     * 系统时间
     *
     * @param context
     * @param time  yyyy-MM-dd HH:mm:ss
     */
    public static void setSystemTime(Context context,String time) throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ToolUtils.time().string2Date(time,"yyyy-MM-dd HH:mm:ss"));

        Intent intent = new Intent();
        intent.setAction("com.android.lango.setsystemtime");
        //时间戳，单位ms, String类型
        intent.putExtra("time", String.valueOf(calendar.getTimeInMillis()));
        context.sendBroadcast(intent);
    }

    /**
     * 电视广告机静默安装
     * @param context
     * @param apkPath  apk绝对路径
     */
    public static void installSilentApk(Context context,String apkPath){
        Intent intent = new Intent();
        intent.setAction("com.android.lango.installapp");
        intent.putExtra("apppath", apkPath);
        context.sendBroadcast(intent);
    }

    /**
     * 关闭第三方应用
     *
     * @param context
     * @param packageName com.xxxx.demo
     */
    public static void killapp(Context context,String packageName){
        Intent intent = new Intent();
        intent.setAction("com.android.lango.killapp");
        intent.putExtra("packagename", packageName);
        context.sendBroadcast(intent);
    }



    /**
     * 重启机器
     * @param context
     */
    public static void reboot(Context context){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.reboot");
        context.sendBroadcast(intent);
    }

    /**
     * 关机
     * @param context
     */
    public static void powerDown(Context context){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.shutdown");
        context.sendBroadcast(intent);
    }

    /**
     * 截屏
     * 截图保存路径：外置存储根目录的screenshot.png
     * （既：Environment.getExternalStorageDirectory().getAbsolutePath()+"/screenshot.png"
     * ）
     * @param context
     */
    public static void takeScreen(Context context){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.screencap");
        context.sendBroadcast(intent);
    }

    /**
     * 休眠
     * @param context
     */
    public static void gotoSleep(Context context){
        Intent intent = new Intent();  
        intent.setAction("android.intent.action.gotosleep");  
        context.sendBroadcast(intent);
    }

    /**
     * 唤醒休眠
     * @param context
     */
    public static void exitSleep(Context context){
        Intent intent = new Intent();  
        intent.setAction("android.intent.action.exitsleep");  
        context.sendBroadcast(intent);
    }

    /**
     * 旋转屏幕
     * @param context 
     * @param rotation  0，90，180，270
     */
    public static void rotation(Context context,int rotation){
        Intent intent = new Intent();
        switch (rotation){
            case 90:
                intent.setAction("android.intent.rotation_90");
                break;
            case 180:
                intent.setAction("android.intent.rotation_180");
                break;
            case 270:
                intent.setAction("android.intent.rotation_270");
                break;
            default:
                intent.setAction("android.intent.rotation_0");
                break;
        }
        context.sendBroadcast(intent);
    }

}
