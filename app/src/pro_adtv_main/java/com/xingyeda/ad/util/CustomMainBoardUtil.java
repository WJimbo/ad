package com.xingyeda.ad.util;

import android.content.Context;
import android.content.Intent;

import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.ToolUtils;

import java.util.Calendar;
import java.util.Date;


/**
 * 定制化主板的功能
 */
public class CustomMainBoardUtil {

    /**
     * 定时开关机
     * 说明：其中属性hour为小时（0-23），
     * minute为分钟（0-59），
     * mAttribute为事件属性，
     * 值为1表明为开机，2为关机。
     * daysOfWeek为一周中哪几天需要开关机的属性。值为0x7f，二进制为1111111，表明每天都要执行此事件。
     * 如果我们需要周一到周五才执行此事件，二进制为 001 1111，即将daysOfWeek属性设为0x1f即可。
     * 再如我们需要周六和周日才执行此事件，
     * 二进制为110 0000，即将daysOfWeek属性设为0x60即可。所以，以上代码的意思是增加一个每天20点30分关机的事件。
     * @param context
     * @param timeoff HH:mm
     * @param timeon HH:mm
     */
    public static void powerOffAndOn(Context context,String timeoff,String timeon){
        MyLog.i("设置定时关机：" + ToolUtils.string().nullStrToEmpty(timeoff) + "  定时开机:" + timeon);
        try {
            deleteAllTimingSwitchForADTV(context);

            Calendar calendar = Calendar.getInstance();
            if (!ToolUtils.string().isEmpty(timeoff)) {
                String offTimes[] = new String[]{timeoff};
                if(timeoff.contains(";")){
                    offTimes = timeoff.split(";");
                }
                for(String time : offTimes){
                    if(!ToolUtils.string().isEmpty(time)){
                        calendar.setTime(ToolUtils.time().string2Date(time,"HH:mm"));
                        sendDevicePowerBroadcast(context,calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),2);
                    }
                }
            }
            if (!ToolUtils.string().isEmpty(timeon)) {
                String onTimes[] = new String[]{timeon};
                if(timeon.contains(";")){
                    onTimes = timeon.split(";");
                }
                for(String time : onTimes){
                    if(!ToolUtils.string().isEmpty(time)) {
                        calendar.setTime(ToolUtils.time().string2Date(time, "HH:mm"));
                        sendDevicePowerBroadcast(context, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 1);
                    }
                }
            }
            ToastUtils.showToast(context,"设置定时开关机成功");
        }catch (Exception ex){
            ToastUtils.showToast(context,"设置定时开关机 Exception：" + ex.getMessage());
            MyLog.i("设置定时开关机 Exception：" + ToolUtils.string().nullStrToEmpty(timeoff) + "  定时开机:" + timeon);
        }

    }

    private static void sendDevicePowerBroadcast(Context context,int hour,int minute,int attribute){
        Intent intent = new Intent("com.soniq.cybercast.time");
        intent.putExtra("hour",hour);
        intent.putExtra("minute", minute);
        intent.putExtra("mAttribute", attribute);//值为1表明为开机，2为关机。
        intent.putExtra("daysOfWeek", 0x7f);
        context.sendBroadcast(intent);
    }
    /**
     * 删除定时开关机
     * @param context
     */
    public static void deleteAllTimingSwitchForADTV(Context context){
//        删除定时开关机：
//
//        以下代码表示删除周一到周五早上8点30分开机的事件。
//        这里hour同样一定要设置，
//        minute如果没设置则默认为0，
//        如果没设置mAttribute和daysOfWeek则表明所有跟hour和minute属性相等的事件都将删除。
//        Intent intent = new Intent("com.soniq.cybercast.time_delete");
//        intent.putExtra("hour", 8);
//        intent.putExtra("minute", 30);
//        intent.putExtra("mAttribute", 1);
//        intent.putExtra("daysOfWeek", 0x1f);
//        sendBroadcast(intent);

//        例如以下代码将删除所有在早上8点钟开关机的事件：
//        Intent intent = new Intent("com.soniq.cybercast.time_delete");
//        intent.putExtra("hour", 8);
//        sendBroadcast(intent);
//
//        再如以下代码将删除所有在晚上8点30分的关机事件：
//        Intent intent = new Intent("com.soniq.cybercast.time_delete");
//        intent.putExtra("hour", 20);
//        intent.putExtra("minute", 30);
//        intent.putExtra("mAttribute", 2);
//        sendBroadcast(intent);

//        另外，如果需要删除所有的开关机事件可参考以下代码, delete_all为true时：如mAttribute为1则删除所有开机事件、mAttribute为2则删除所有关机事件，mAttribute没有设置时则删除所有开关机事件：
        Intent intent = new Intent("com.soniq.cybercast.time_delete");
        intent.putExtra("delete_all", true);
//        intent.putExtra("mAttribute", 1);//如mAttribute为1则删除所有开机事件、mAttribute为2则删除所有关机事件，mAttribute没有设置时则删除所有开关机事件
        context.sendBroadcast(intent);
    }

    /**
     * 系统时间
     *
     * @param context
     * @param time  yyyy-MM-dd HH:mm:ss
     */
    public static void setSystemTime(Context context,String time) throws Exception{
        Date date = ToolUtils.time().string2Date(time,"yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        Intent mIntent = new Intent("com.histar.set.system.time");
        int[] mTime = {year,month,day,hour,minute,second};//(年，月，日，时，分，秒)
        mIntent.putExtra("currentTime",mTime);
        context.sendBroadcast(mIntent);
    }

    /**
     * 电视广告机静默安装
     * @param context
     * @param apkPath  apk绝对路径
     */
    public static void installSilentApk(Context context,String apkPath){

    }

    /**
     * 关闭第三方应用
     *
     * @param context
     * @param packageName com.xxxx.demo
     */
    public static void killapp(Context context,String packageName){

    }



    /**
     * 重启机器
     * @param context
     */
    public static void reboot(Context context){
        Intent intent = new Intent("com.soniq.cybercast.reboot");
        context.sendBroadcast(intent);
        DeviceUtil.reboot(context);
    }

    /**
     * 关机
     * @param context
     */
    public static void powerDown(Context context){
        Intent intent = new Intent("com.soniq.cybercast.powerdown");
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
        Intent intent = new Intent("com.histar.takescreen");
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
