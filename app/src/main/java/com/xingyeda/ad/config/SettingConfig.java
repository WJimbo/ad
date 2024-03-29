package com.xingyeda.ad.config;

import android.content.Context;

import com.xingyeda.ad.util.SharedPreUtil;

/**
 * @author tangyongx
 * @date 2018-12-29
 */
public class SettingConfig {

    public static class VideoRotateAngleChangedEventData{

    }

    /**
     * 旋转视频角度
     */
    private static final float DEFAULT_VIDEO_ROTATE_ANGLE = 270;
    private static final String KEY_VIDEO_ROTATE_ANGLE = "KEY_VIDEO_ROTATE_ANGLE";

    public static float getScreenRotateAngle(Context context){
        return SharedPreUtil.getFloat(context,KEY_VIDEO_ROTATE_ANGLE,DEFAULT_VIDEO_ROTATE_ANGLE);
    }
    public static void setScreenRatateAngle(Context context, float angle){
        SharedPreUtil.put(context,KEY_VIDEO_ROTATE_ANGLE,angle);
    }
    /**
     * 主界面日志调试接口
     */
    private static final boolean DEFAULT_SHOW_DEBUGVIEW = false;
    private static final String KEY_SHOW_DEBUGVIEW =  "KEY_SHOW_DEBUGVIEW";

    public static boolean isShowDebugView(Context context){
        return SharedPreUtil.getBoolean(context,KEY_SHOW_DEBUGVIEW,DEFAULT_SHOW_DEBUGVIEW);
    }
    public static void setShowDebugView(Context context,boolean show){
         SharedPreUtil.put(context,KEY_SHOW_DEBUGVIEW,show);
    }

    private static final int DEFAULT_AD_SCREEN_NUM_MODEL = 1;
    private static final String KEY_AD_SCREEN_NUM_MODEL = "KEY_AD_SCREEN_NUM_MODEL";

    public static int getADScreenNum(Context context){
        return SharedPreUtil.getInt(context,KEY_AD_SCREEN_NUM_MODEL,DEFAULT_AD_SCREEN_NUM_MODEL);
    }
    public static void setADScreenNum(Context context,int num){
        SharedPreUtil.put(context,KEY_AD_SCREEN_NUM_MODEL, num);
    }
}
