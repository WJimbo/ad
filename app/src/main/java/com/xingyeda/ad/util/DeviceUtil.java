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
