package com.xingyeda.ad.util;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.xingyeda.ad.module.start.StartActivity;


/**
 * Created by LDL on 2017/10/12.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = CrashHandler.class.getSimpleName();
    private Context mContext;
    private static CrashHandler INSTANCE = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {

    }


    public static CrashHandler getInstance() {
        return INSTANCE;
    }


    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                MyLog.e("uncaughtException:" + ex.getMessage());
                Looper.prepare();
                Toast.makeText(mContext, "程序异常崩溃，详情请查看日志文件", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Log.e(TAG, "error : ", e);
        }

        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
//            mDefaultHandler.uncaughtException(thread, ex);
//            DeviceUtil.reboot(mContext);
//            reStartApp();
            CustomMainBoardUtil.reboot(mContext,"闪退重启");
        } else {
//            reStartApp();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                Log.e(TAG, "error : ", e);
//            }
            CustomMainBoardUtil.reboot(mContext,"闪退重启");
            // 退出程序
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);

        }
    }
    public void reStartApp(){
        StartActivity.isStarted = false;
        Intent intent = new Intent(mContext, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        final StringBuffer sb = getTraceInfo(ex);
        MyLog.e("handleException:" + sb);
        return true;
    }

    public static StringBuffer getTraceInfo(Throwable e) {
        StringBuffer sb = new StringBuffer();
        Throwable ex = e.getCause() == null ? e : e.getCause();
        StackTraceElement[] stacks = ex.getStackTrace();
        for (int i = 0; i < stacks.length; i++) {
            sb.append("class: ").append(stacks[i].getClassName()).append("; method: ").append(stacks[i].getMethodName()).append("; line: ").append(stacks[i].getLineNumber()).append("; Exception: ").append(ex.toString() + "\n");
        }
        Log.d(TAG, sb.toString());
        return sb;
    }

}
