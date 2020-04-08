package com.xingyeda.ad.config;

import android.content.Context;

import com.gavinrowe.lgw.library.SimpleTimerTask;
import com.gavinrowe.lgw.library.SimpleTimerTaskHandler;
import com.xingyeda.ad.util.DeviceUtil;
import com.xingyeda.ad.util.httputil.HttpObjResponseData;
import com.xingyeda.ad.util.httputil.HttpRequestData;
import com.xingyeda.ad.util.httputil.TokenHttpRequestModel;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

import org.greenrobot.eventbus.EventBus;

public class SettingConfigManager {

    private boolean isStartTimer = false;
    private static SettingConfigManager instance;
    private final static int AUTO_UPDATE_SETTINGCONFIG_TIME = 60 * 60 * 1000;
    public static synchronized SettingConfigManager getInstance() {
        if(instance == null){
            instance =  new SettingConfigManager();
        }
        return instance;
    }
    private SettingConfigManager(){

    }
    public void startUpdateSettingTimer(Context context){
        if(!isStartTimer){
            isStartTimer = true;
            final Context appContext = context.getApplicationContext();
            //开始请求数据
            //容错，怕偶尔收不到服务器推送，采用轮询的方式获取数据。
            SimpleTimerTask loopTask = new SimpleTimerTask(AUTO_UPDATE_SETTINGCONFIG_TIME) {
                @Override
                public void run() {
                    updateSettingForNet(appContext);
                }
            };
            SimpleTimerTaskHandler.getInstance().sendTask(401, loopTask);
            updateSettingForNet(appContext);
        }
    }
    private boolean isRequesting = false;
    private String lastSettingJsonValue = "";
    public synchronized void updateSettingForNet(final Context context){
        if(isRequesting){
            return;
        }
        isRequesting = true;
        HttpRequestData requestData = new HttpRequestData();
        requestData.setRequestURL( URLConfig.getPath(context, URLConfig.USERSET_PATH));
        requestData.setRequestMode(HttpRequestData.RequestModeType.GET);
        requestData.setEnableToken(true);
        requestData.addRequestParams("mac", DeviceUUIDManager.generateUUID(context));
        TokenHttpRequestModel.asynTokenRequestData(requestData, SettingResponseData.class, new HttpRequestModel.RequestCallBack() {
            @Override
            public void onResponseMainThread(BaseResponseData baseResponseData) {
                if(baseResponseData.isOperationSuccess()){
                    SettingResponseData settingResponseData = (SettingResponseData)baseResponseData;
                    if(settingResponseData.data != null){
                        if(settingResponseData.getJsonValueString() != null && settingResponseData.getJsonValueString().equals(lastSettingJsonValue)){
                            //获取设置信息和上次一样 就没必要去做后续的操作了
                            return;
                        }
                        SettingResponseData.SettingItem settingItem = settingResponseData.data;

                        boolean settingChanged = false;
                        if(settingItem.adsetting_VideoRotateAngle != SettingConfig.getScreenRotateAngle(context)){
                            SettingConfig.setScreenRatateAngle(context,settingItem.adsetting_VideoRotateAngle);
                            settingChanged = true;
                        }
                        boolean show = settingItem.adsetting_ShowDebugView == 1;
                        if (show != SettingConfig.isShowDebugView(context)) {
                            SettingConfig.setShowDebugView(context,show);
                            settingChanged = true;
                        }
                        if(settingItem.adsetting_ADShowMode != SettingConfig.getADScreenNum(context)){
                            SettingConfig.setADScreenNum(context,settingItem.adsetting_ADShowMode);
                            settingChanged = true;
                        }
                        DeviceUtil.setMusicVolume(context,settingItem.adsetting_MusicVolume);
                        DeviceUtil.setSystemScreenBrightness(context,settingItem.adsetting_SystemScreenBrightness);
                        DeviceUtil.timingSwitchForADTV(context,settingItem.adsetting_PowerOff,settingItem.adsetting_PowerOn);

                        if(settingChanged){
                            EventBus.getDefault().post(new SettingConfig.VideoRotateAngleChangedEventData());
                        }
                        isRequesting = false;
                    }
                    lastSettingJsonValue = settingResponseData.getJsonValueString();
                }
            }

            @Override
            public void onResponseBackgroundThread(BaseResponseData baseResponseData) {

            }

            @Override
            public void dealBusinessError(boolean errorInMainThread, Exception ex) {

            }
        });
    }
    class SettingResponseData extends HttpObjResponseData {
        private SettingItem data;


        class SettingItem{

            /**
             * adsetting_ADShowMode : 0
             * adsetting_ShowDebugView : 0
             * adsetting_VideoRotateAngle : 0
             */

            private int adsetting_ADShowMode;
            private int adsetting_ShowDebugView;
            private int adsetting_VideoRotateAngle;

            private String adsetting_MusicVolume;
            private String adsetting_SystemScreenBrightness;

            private String adsetting_PowerOff;
            private String adsetting_PowerOn;

            public String getAdsetting_MusicVolume() {
                return adsetting_MusicVolume;
            }

            public void setAdsetting_MusicVolume(String adsetting_MusicVolume) {
                this.adsetting_MusicVolume = adsetting_MusicVolume;
            }

            public String getAdsetting_SystemScreenBrightness() {
                return adsetting_SystemScreenBrightness;
            }

            public void setAdsetting_SystemScreenBrightness(String adsetting_SystemScreenBrightness) {
                this.adsetting_SystemScreenBrightness = adsetting_SystemScreenBrightness;
            }

            public String getAdsetting_PowerOff() {
                return adsetting_PowerOff;
            }

            public void setAdsetting_PowerOff(String adsetting_PowerOff) {
                this.adsetting_PowerOff = adsetting_PowerOff;
            }

            public String getAdsetting_PowerOn() {
                return adsetting_PowerOn;
            }

            public void setAdsetting_PowerOn(String adsetting_PowerOn) {
                this.adsetting_PowerOn = adsetting_PowerOn;
            }

            public int getAdsetting_ADShowMode() {
                return adsetting_ADShowMode;
            }

            public void setAdsetting_ADShowMode(int adsetting_ADShowMode) {
                this.adsetting_ADShowMode = adsetting_ADShowMode;
            }

            public int getAdsetting_ShowDebugView() {
                return adsetting_ShowDebugView;
            }

            public void setAdsetting_ShowDebugView(int adsetting_ShowDebugView) {
                this.adsetting_ShowDebugView = adsetting_ShowDebugView;
            }

            public int getAdsetting_VideoRotateAngle() {
                return adsetting_VideoRotateAngle;
            }

            public void setAdsetting_VideoRotateAngle(int adsetting_VideoRotateAngle) {
                this.adsetting_VideoRotateAngle = adsetting_VideoRotateAngle;
            }
        }
    }
}
