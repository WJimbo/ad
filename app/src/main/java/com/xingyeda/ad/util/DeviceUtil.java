package com.xingyeda.ad.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.ToolUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class DeviceUtil {

//   String timeoff="yyyy-MM-dd HH:mm:ss";
//String timeon="yyyy-MM-dd HH:mm:ss";
//Intent intent=new Intent();
//intent.putExtra("timeoff", timeoff);
//intent.putExtra("timeon", timeon);
//// enable=true时 定时开关机生效  为false时 取消定时开关机
//intent.putExtra("enable", true);
//intent.setAction("com.jld.power.action");
    //sendBroadcast(intent);
    //ad3288板子定时开关机方法
    public static void timingSwitchForAD3288(Context context,String timeoff,String timeon){

        Intent intent=new Intent();
        if(ToolUtils.string().isEmpty(timeoff) || ToolUtils.string().isEmpty(timeon)){
            intent.putExtra("timeoff", "");
            intent.putExtra("timeon", "");
            intent.putExtra("enable", false);
        }else{
            String currentDateStr = ToolUtils.time().date2String(new Date(),"yyyy-MM-dd");
            if(timeoff.contains("0000-00-00")){
                timeoff = timeoff.replace("0000-00-00",currentDateStr);

                long time = ToolUtils.time().string2Date(timeoff,"yyyy-MM-dd HH:mm:ss").getTime();
                if(System.currentTimeMillis() > time){
                    time = time + 24 * 60 * 60 * 1000;
                }
                timeoff = ToolUtils.time().date2String(new Date(time),"yyyy-MM-dd HH:mm:ss");
            }
            if(timeon.contains("0000-00-00")){
                timeon = timeon.replace("0000-00-00",currentDateStr);
                long time = ToolUtils.time().string2Date(timeon,"yyyy-MM-dd HH:mm:ss").getTime();
                if(System.currentTimeMillis() > time){
                    time = time + 24 * 60 * 60 * 1000;
                }
                timeon = ToolUtils.time().date2String(new Date(time),"yyyy-MM-dd HH:mm:ss");
            }

            intent.putExtra("timeoff", timeoff);
            intent.putExtra("timeon", timeon);
// enable=true时 定时开关机生效  为false时 取消定时开关机
            intent.putExtra("enable", true);
        }

        intent.setAction("com.jld.power.action");
        context.sendBroadcast(intent);
    }


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
    public static void timingSwitchForADTV(Context context,String timeoff,String timeon){
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
     * 设置tv广告机系统时间
     * @param context
     * @param time  yyyy-MM-dd HH:mm:ss
     */
    public static void setSystemTimeForADTV(Context context,String time) throws Exception{
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
     * @param apkPath
     */
    public static void installSilentApkForADTV(Context context,String apkPath){
        Intent intent = new Intent("com.histar.installsilent");
        intent.putExtra("filePath", apkPath);//(path为安装包绝对路径)；
        context.sendBroadcast(intent);
    }

    /**
     * 重启机器
     * @param context
     */
    public static void rebootForADTV(Context context){
        Intent intent = new Intent("com.soniq.cybercast.reboot");
        context.sendBroadcast(intent);
    }

    /**
     * 关机
     * @param context
     */
    public static void powerDownForADTV(Context context){
        Intent intent = new Intent("com.soniq.cybercast.powerdown");
        context.sendBroadcast(intent);
    }



    public static void setMusicVolume(Context context,String volume){
        int musicVolumePercent = 0;
        if(ToolUtils.string().isEmpty(volume)){
            return;
        }
        try {
            musicVolumePercent = Integer.parseInt(volume);
        }catch (Exception ex){
            MyLog.i("音量设置异常:" + volume);
            return;
        }
        if(musicVolumePercent < 0 || musicVolumePercent > 100){
            MyLog.i("音量设置出错，超出音量范围:" + volume);
            return;
        }
        AudioManager am=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int maxMusicVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int musicVolume = (int)(maxMusicVolume *(musicVolumePercent / 100.0));
        am.setStreamVolume(AudioManager.STREAM_MUSIC,musicVolume , AudioManager.FLAG_PLAY_SOUND|AudioManager.FLAG_SHOW_UI);
        ToastUtils.showToastLong(context,"音量设置成功,当前音量：" + musicVolume + "  系统支持的最大音量为：" + maxMusicVolume);
//        MyLog.i("音量设置成功,当前音量：" + musicVolume + "  系统支持的最大音量为：" + maxMusicVolume);
    }

    /**
     * @param context
     * 首先，需要明确屏幕亮度有两种调节模式：
     *
     * Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC：值为1，自动调节亮度。
     * Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL：值为0，手动模式。
     * 如果需要实现亮度调节，首先需要设置屏幕亮度调节模式为手动模式。
     * ————————————————
     * 版权声明：本文为CSDN博主「低调小一」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/wzy_1988/article/details/49472611
     */
    public static void setScreenManualMode(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取屏幕亮度值
     *
     * 这里需要了解：
     *
     * 屏幕最大亮度为255。
     * 屏幕最低亮度为0。
     * 屏幕亮度值范围必须位于：0～255。
     * @param context
     * @return
     */
    public static int getSystemScreenBrightness(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        int defVal = 125;
        return Settings.System.getInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, defVal);
    }

    /**
     * 当屏幕亮度模式为0即手动调节时，可以通过如下代码设置屏幕亮度：
     * @param context
     * @param value 范围必须位于：0～100。
     */
    public static void setSystemScreenBrightness(Context context,String value) {
        if(ToolUtils.string().isEmpty(value)){
            return;
        }
        int screenBrightness = 0;
        try {
            screenBrightness = (int)(Integer.parseInt(value) / 100f * 255);
        }catch (Exception ex){
            MyLog.i("设置屏幕亮度数据不对:" + value);
            return;
        }


        setScreenManualMode(context);
        ContentResolver contentResolver = context.getContentResolver();
        if(screenBrightness < 0){
            screenBrightness = 125;
        }else if(screenBrightness > 255){
            screenBrightness = 255;
        }
        ToastUtils.showToast(context,"屏幕亮度调节:" + screenBrightness + " 最大值:" + 255);
        Settings.System.putInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, screenBrightness);
    }

    /**
     * 设置当前窗口亮度
     * 很多视频应用，在touch事件处理屏幕亮度时，并不是修改的系统亮度值，
     * 而是修改当前应用所在窗口的亮度。具体做法就是修改LayoutParams中的screenBrightness属性
     * @param activity
     * @param brightness 范围必须位于：0～255。
     */
    public static void setWindowBrightness(Activity activity ,int brightness) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);
    }


    public static void takeScreenForADTV(Context context){
        Intent intent = new Intent("com.histar.takescreen");
        context.sendBroadcast(intent);
    }





    public static void reboot(Context context) {
        MyLog.e("准备重启");
        ToastUtils.showToast(context,"准备重启");
        Process systemProcess;
        DataOutputStream systemProcessDataOutputStream = null;
        try {
            systemProcess = Runtime.getRuntime().exec("su");
            systemProcessDataOutputStream = new
                    DataOutputStream(systemProcess.getOutputStream());
            systemProcessDataOutputStream.writeBytes("reboot \n");
            ToastUtils.showToast(context,"重启成功");
            MyLog.e("重启成功");
        } catch (Exception e) {
            ToastUtils.showToast(context,"重启失败");
            MyLog.e("重启失败:" + e.getMessage());
        } finally {
            try {
                if (systemProcessDataOutputStream != null) {
                    systemProcessDataOutputStream.close();
                }
            } catch (IOException e) {
                Log.e("TAG", e.getMessage(), e);
            }
        }
    }

    //翻译并执行相应的adb命令
    public static boolean exusecmd(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            Log.e("updateFile", "======000==writeSuccess======");
            process.waitFor();
        } catch (Exception e) {
            Log.e("updateFile", "======111=writeError======" + e.toString());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**移动文件
     * RootCmd.moveFileToSystem(file.getAbsolutePath(),"/system");
     * @param filePath
     * @param sysFilePath
     * @return
     */
    public static boolean moveFileToSystem(String filePath, String sysFilePath) {
        if(setSystemDirToWrite()){

            exusecmd("dd if=" + filePath +  " of=" + sysFilePath + new File(filePath).getName());
            return exusecmd("mv " + filePath + " " + sysFilePath);
//            return exusecmd("cp " + filePath + " " + sysFilePath);
        }
        return false;
    }
    public static boolean deleteFileInSystem(String filePath){
        if(setSystemDirToWrite()){
            return exusecmd("rm -f " + filePath);
        }
        return false;
    }

    public static boolean setSystemDirToWrite(){
        if(exusecmd("mount -o rw,remount /system")){
            return exusecmd("chmod 777 /system");
        }
        return false;
    }
}
