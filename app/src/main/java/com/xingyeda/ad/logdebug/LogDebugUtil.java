package com.xingyeda.ad.logdebug;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Debug;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogDebugUtil {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    public static String currentDate(){
        return simpleDateFormat.format(new Date());
    }
    public static void appendLog(String logMessage){
        //最大分配内存获取方法2
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() * 1.0/ (1024 * 1024));
        //当前分配的总内存
        int totalMemory = (int) (Runtime.getRuntime().totalMemory() * 1.0/ (1024 * 1024));

        EventBus.getDefault().post(new LogDebugItem( currentDate()+"(" + getMemory() + "/" + maxMemory+  "):" + logMessage));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getMemory() {
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);
        // dalvikPrivateClean + nativePrivateClean + otherPrivateClean;
        int totalPrivateClean = memoryInfo.getTotalPrivateClean();
        // dalvikPrivateDirty + nativePrivateDirty + otherPrivateDirty;
        int totalPrivateDirty = memoryInfo.getTotalPrivateDirty();
        // dalvikPss + nativePss + otherPss;
        int totalPss = memoryInfo.getTotalPss();
        // dalvikSharedClean + nativeSharedClean + otherSharedClean;
        int totalSharedClean = memoryInfo.getTotalSharedClean();
        // dalvikSharedDirty + nativeSharedDirty + otherSharedDirty;
        int totalSharedDirty = memoryInfo.getTotalSharedDirty();
        // dalvikSwappablePss + nativeSwappablePss + otherSwappablePss;
        int totalSwappablePss = memoryInfo.getTotalSwappablePss();

        int total = totalPrivateClean + totalPrivateDirty + totalPss + totalSharedClean + totalSharedDirty + totalSwappablePss;
        return total ;
    }
}
