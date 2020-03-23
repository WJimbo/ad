package com.xingyeda.ad.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.xingyeda.ad.MainApplication;
import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.UIUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public static void timingSwitch(Context context,String timeoff,String timeon){

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
        am.setStreamVolume(AudioManager.STREAM_MUSIC,musicVolume , AudioManager.FLAG_PLAY_SOUND);
        ToastUtils.showToastLong(context,"音量设置成功,当前音量：" + musicVolume + "  系统支持的最大音量为：" + maxMusicVolume);
//        MyLog.i("音量设置成功,当前音量：" + musicVolume + "  系统支持的最大音量为：" + maxMusicVolume);
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
