package com.xingyeda.ad.logdebug;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogDebugUtil {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    public static String currentDate(){
        return simpleDateFormat.format(new Date());
    }
    public static void appendLog(String logMessage){
        EventBus.getDefault().post(new LogDebugItem( currentDate()+ ":" + logMessage));
    }
}
