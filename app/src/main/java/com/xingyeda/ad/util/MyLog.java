package com.xingyeda.ad.util;

import android.content.Context;
import android.os.Environment;

import com.xingyeda.ad.config.DeviceUUIDManager;
import com.zz9158.app.common.utils.LoggerHelper;
import com.zz9158.app.common.utils.ToolUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by LDL on 2017/10/10.
 */

public class MyLog {
    private static MyLog INSTANCE = null;
    private static final String TAG = "LowerMachine";
    private static Boolean MYLOG_SWITCH=true; // 日志文件总开关
    private static Boolean MYLOG_WRITE_TO_FILE=true;// 日志写入文件开关
    private static char MYLOG_TYPE='v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
    private static String MYLOG_PATH_SDCARD_DIR;// 日志文件在sdcard中的路径
    //    private static String MYLOG_PATH_SDCARD_DIR=LogcatHelper.getPATH_LOGCAT();// 日志文件在sdcard中的路径
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 7;// sd卡中日志文件的最多保存天数
    private static String MYLOGFILEName = ".txt";// 本类输出的日志文件名称
    private static SimpleDateFormat myLogSdfSimpleDateFormate = new SimpleDateFormat("MM-dd HH:mm:ss");// 日志的输出格式
    private static SimpleDateFormat logfileSimpleDateFormate = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式

    public static void w(Object msg) { // 警告信息
        log(TAG, msg.toString(), 'w');
    }

    public static void e( Object msg) { // 错误信息
        log(TAG, msg.toString(), 'e');
    }

    public static void d(Object msg) {// 调试信息
        log(TAG, msg.toString(), 'd');
    }

    public static void i( Object msg) {//
        log(TAG, msg.toString(), 'i');
    }

    public static void v( Object msg) {
        log(TAG, msg.toString(), 'v');
    }

    public static void w( String text) {
        log(TAG, text, 'w');
    }

    public static void e( String text) {
        log(TAG, text, 'e');
    }

    public static void d( String text) {
        log(TAG, text, 'd');
    }

    public static void i(String text) {
        log(TAG, text, 'i');
    }

    public static void v(String text) {
        log(TAG, text, 'v');
    }

    private static String versionInfo = "";
    /**
     *
     * 初始化目录
     *
     * */
    public void init(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            MYLOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "XYD_AD" + File.separator + "log";
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            MYLOG_PATH_SDCARD_DIR = context.getFilesDir().getAbsolutePath()
                    + File.separator + "XYD_AD"+ File.separator + "log";
        }
        File file = new File(MYLOG_PATH_SDCARD_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        versionInfo = ToolUtils.appTool().getVersionNameFromPackage(context) + "_" + ToolUtils.appTool().getAppVersionCode(context);
    }

    public static MyLog getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MyLog(context.getApplicationContext());
        }
        return INSTANCE;
    }

    private MyLog(Context context) {
        init(context);
    }
    public static String getPATH_LOGCAT() {
        return MYLOG_PATH_SDCARD_DIR;
    }

    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag
     * @param msg
     * @param level
     * @return void
     * @since v 1.0
     */
    private static void log(String tag, String msg, char level) {
        if (MYLOG_SWITCH) {
            if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) { // 输出错误信息
                LoggerHelper.e(msg);
            } else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                LoggerHelper.w( msg);
            } else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                LoggerHelper.d( msg);
            } else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                LoggerHelper.i(msg);
            } else {
                LoggerHelper.v(msg);
            }
            if (MYLOG_WRITE_TO_FILE)
                writeLogtoFile(String.valueOf(level), tag, msg);
        }
    }
    /**
     * 打开日志文件并写入日志
     *
     * @return
     * **/
    private static final Object lockObject = new Object();
    private static void writeLogtoFile(String mylogtype, String tag, String text) {// 新建或打开日志文件
        synchronized (lockObject){
            try{
                try {
                    Date nowtime = new Date();
                    String needWriteMessage = myLogSdfSimpleDateFormate.format(nowtime) + " " + versionInfo + " " + mylogtype  + "-->" + text;
                    File file = getCurrentLogFile();
                    ToolUtils.file().writeFileFromString(file,needWriteMessage + "\n",true);
                }catch (Error error){
                    LoggerHelper.e(error,"writeLogtoFile error");
                }

            } catch (Exception ex){
                LoggerHelper.e(ex,"writeLogtoFile Exception");
            }
        }
    }

    public static File getCurrentLogFile(){
        Date nowtime = new Date();
        String needWriteFiel = logfileSimpleDateFormate.format(nowtime);
        File file = new File(MYLOG_PATH_SDCARD_DIR, needWriteFiel + MYLOGFILEName);
        return file;
    }

    public static File getLogRootPath(){
        File file = new File(MYLOG_PATH_SDCARD_DIR);
        return file;
    }

    /**
     * 删除制定的日志文件
     * */
    public static void delFile() {// 删除日志文件
        String needDelFiel = logfileSimpleDateFormate.format(getDateBefore());
        File file = new File(MYLOG_PATH_SDCARD_DIR, needDelFiel + MYLOGFILEName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除超过七天的所有日志
     */
    public static void delBefore7LogFiles(){

        ArrayList<String> unDelFileNames = new ArrayList<>();
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        for(int beforeDay = 0;beforeDay < 7;beforeDay++){
            now.setTime(nowtime);
            now.set(Calendar.DATE, now.get(Calendar.DATE)
                    - beforeDay);
            String unDelFileName = logfileSimpleDateFormate.format(now.getTime()) + MYLOGFILEName;
            unDelFileNames.add(unDelFileName);
        }

        File dirFile = new File(MYLOG_PATH_SDCARD_DIR);
        if(dirFile != null && dirFile.isDirectory() && dirFile.listFiles() != null){
            for(File logFile : dirFile.listFiles()){
                if(logFile != null
                        && logFile.getName() != null
                        && logFile.getName().endsWith(MYLOGFILEName)
                        && !unDelFileNames.contains(logFile.getName())){
                    ToolUtils.file().deleteFile(logFile);
                }
            }
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     * */
    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE)
                - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }
    /**
     * 删除指定日期前的日志文件
     * */
    public static void delFileToDay(int day) {
        String needDelFiel = logfileSimpleDateFormate.format(getDateBefore(day));
        File file = new File(MYLOG_PATH_SDCARD_DIR, needDelFiel + MYLOGFILEName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     *
     * @param day
     * @return
     */
    public static String fileName(int day) {
        String needDelFiel = logfileSimpleDateFormate.format(getDateBefore(day));
//        File file = new File(MYLOG_PATH_SDCARD_DIR, needDelFiel + MYLOGFILEName);
        return needDelFiel + MYLOGFILEName;
    }
    /**
     *
     * @param day
     * @return
     */
    public static File getFile(int day) {
        String needDelFiel = logfileSimpleDateFormate.format(getDateBefore(day));
        File file = new File(MYLOG_PATH_SDCARD_DIR, needDelFiel + MYLOGFILEName);
        return file;
    }

    /**
     * 得到需要的前几天文件名
     * */
    private static Date getDateBefore(int day) {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE)- day);
        return now.getTime();
    }
}
