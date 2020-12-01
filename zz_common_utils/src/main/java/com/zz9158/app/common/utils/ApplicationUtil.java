package com.zz9158.app.common.utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

/**
 * @author tangyongx
 * @date 2018-12-12
 */
public final class ApplicationUtil {

    private ApplicationUtil() {
    }

    public static boolean isMainProcess(Context context) {
        if (context == null) {
            return false;
        }

        String packageName = context.getApplicationContext().getPackageName();
        String processName = ApplicationUtil.getProcessName(context);
        return packageName.equals(processName);
    }

    public static String getProcessName(Context context) {
        String processName = getProcessFromFile();
        if (processName == null) {
            // 如果装了xposed一类的框架，上面可能会拿不到，回到遍历迭代的方式
            processName = getProcessNameByAM(context);
        }
        return processName;
    }

    private static String getProcessFromFile() {
        BufferedReader reader = null;
        try {
            int pid = android.os.Process.myPid();
            String file = "/proc/" + pid + "/cmdline";
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "iso-8859-1"));
            int c;
            StringBuilder processName = new StringBuilder();
            while ((c = reader.read()) > 0) {
                processName.append((char) c);
            }
            return processName.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getProcessNameByAM(Context context) {
        String processName = null;

        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        if (am == null) {
            return null;
        }

        while (true) {
            List<ActivityManager.RunningAppProcessInfo> plist = am.getRunningAppProcesses();
            if (plist != null) {
                for (ActivityManager.RunningAppProcessInfo info : plist) {
                    if (info.pid == android.os.Process.myPid()) {
                        processName = info.processName;

                        break;
                    }
                }
            }

            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            try {
                Thread.sleep(100L); // take a rest and again
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean isMainProcessLive(Context context) {
        if (context == null) {
            return false;
        }

        final String processName = context.getPackageName();
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> plist = am.getRunningAppProcesses();
            if (plist != null) {
                for (ActivityManager.RunningAppProcessInfo info : plist) {
                    if (info.processName.equals(processName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static void restartApp(Context context,Class startActivityClass){
        Intent mStartActivity = new Intent(context, startActivityClass);

        int mPendingIntentId = 123456;

        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);

        System.exit(0);
    }


    /**
     * 判断当前应用是否启动
     *
     * @param context
     * @return
     */
    public static boolean getCurrentTask(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取当前所有存活task的信息
        List<ActivityManager.RunningTaskInfo> appProcessInfos = activityManager.getRunningTasks(Integer.MAX_VALUE);
        //遍历，若task的name与当前task的name相同，则返回true，否则，返回false
        for (ActivityManager.RunningTaskInfo process : appProcessInfos) {
            if (process.baseActivity.getPackageName().equals(context.getPackageName())
                    || process.topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回app运行状态
     *
     * @param context 一个context
     * @return int 1:前台 2:后台 0:不存在
     */
    public static int isAppAlive(Context context) {
        try {
            String packageName = context.getPackageName();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> listInfos = activityManager.getRunningTasks(20);
            // 判断程序是否在栈顶
            if (listInfos.get(0).topActivity.getPackageName().equals(packageName)) {
                return 1;
            } else {
                // 判断程序是否在栈里
                for (ActivityManager.RunningTaskInfo info : listInfos) {
                    if (info.topActivity.getPackageName().equals(packageName)) {
                        return 2;
                    }
                }
                return 0;// 栈里找不到，返回0
            }
        }catch (Exception ex){

        }
        return -1;
    }


    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    public static boolean isActivityForeground(Context context, String className) {
        try {
            if (context == null || TextUtils.isEmpty(className)) {
                return false;
            }
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
            if (list != null && list.size() > 0) {
                ComponentName cpn = list.get(0).topActivity;
                if (className.equals(cpn.getClassName())) {
                    return true;
                }
            }
        }catch (Exception ex){

        }

        return false;
    }
}