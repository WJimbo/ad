package com.zz9158.app.common.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by tangyongx on 26/11/2018.
 */

public class UIUtils {
    /**
     * 保证runnable在主线程当中执行的
     *
     * @param runnable
     */
    public static void runOnMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
    public static void runOnMainThread(Runnable runnable,long delayMillis){
        new Handler(Looper.getMainLooper()).postDelayed(runnable,delayMillis);
    }
}
