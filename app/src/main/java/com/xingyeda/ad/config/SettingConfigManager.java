package com.xingyeda.ad.config;

import android.content.Context;

import com.gavinrowe.lgw.library.SimpleTimerTask;
import com.gavinrowe.lgw.library.SimpleTimerTaskHandler;
import com.xingyeda.ad.util.CustomMainBoardUtil;
import com.xingyeda.ad.util.DeviceUtil;
import com.xingyeda.ad.util.httputil.HttpObjResponseData;
import com.xingyeda.ad.util.httputil.HttpRequestData;
import com.xingyeda.ad.util.httputil.TokenHttpRequestModel;
import com.zz9158.app.common.utils.ToolUtils;
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
                isRequesting = false;
                if(baseResponseData.isOperationSuccess()){
                    SettingResponseData settingResponseData = (SettingResponseData)baseResponseData;
                    if(settingResponseData.data != null){
                        if(settingResponseData.getJsonValueString() != null && settingResponseData.getJsonValueString().equals(lastSettingJsonValue)){
                            //获取设置信息和上次一样 就没必要去做后续的操作了
                            return;
                        }
                        SettingResponseData.SettingItem settingItem = settingResponseData.data;

                        boolean settingChanged = false;

                        if(!ToolUtils.string().isEmpty(settingItem.ad_videoRotateAngle)){
                            try{
                                int value = Integer.parseInt(settingItem.ad_videoRotateAngle);
                                if(value != SettingConfig.getScreenRotateAngle(context)){
                                    SettingConfig.setScreenRatateAngle(context,value);
                                    settingChanged = true;
                                }
                            }catch (Exception ex){

                            }
                        }
                        boolean show = "1".equals(settingItem.ad_showDebugView);
                        if (show != SettingConfig.isShowDebugView(context)) {
                            SettingConfig.setShowDebugView(context,show);
                            settingChanged = true;
                        }
                        if(!ToolUtils.string().isEmpty(settingItem.ad_showMode)){
                            try{
                                int value = Integer.parseInt(settingItem.ad_showMode);
                                if(value != SettingConfig.getADScreenNum(context)){
                                    SettingConfig.setADScreenNum(context,value);
                                    settingChanged = true;
                                }
                            }catch (Exception ex){

                            }

                        }
                        DeviceUtil.setMusicVolume(context,settingItem.musicVolume);
                        DeviceUtil.setSystemScreenBrightness(context,settingItem.ad_systemScreenBrightness);
                        CustomMainBoardUtil.powerOffAndOn(context,settingItem.shutdown,settingItem.bootup);

                        if(settingChanged){
                            EventBus.getDefault().post(new SettingConfig.VideoRotateAngleChangedEventData());
                        }

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
    static class SettingResponseData extends HttpObjResponseData {
        private SettingItem data;


        class SettingItem{


            /**
             * ad_showDebugView :
             * ad_showMode :
             * ad_systemScreenBrightness :
             * ad_videoRotateAngle :
             * agoraDuration :
             * agoraTimeout :
             * aliName :
             * aliPayQRcode :
             * bootup :
             * createAt :
             * createBy : 0
             * deleted : true
             * enableShowFaceInfo :
             * enableTTS :
             * id : 0
             * musicVolume :
             * pointId :
             * remarks :
             * serialNumber :
             * shutdown :
             * sipDuration :
             * sipServer :
             * sipTimeout :
             * updateAt :
             * updateBy : 0
             * version : 0
             * wechatName :
             * wechatPayQRcode :
             */

            private String ad_showDebugView;
            private String ad_showMode;
            private String ad_systemScreenBrightness;
            private String ad_videoRotateAngle;
            private String bootup;
            private String musicVolume;
            private String serialNumber;
            private String shutdown;
            private String sipServer;

            public String getAd_showDebugView() {
                return ad_showDebugView;
            }

            public void setAd_showDebugView(String ad_showDebugView) {
                this.ad_showDebugView = ad_showDebugView;
            }

            public String getAd_showMode() {
                return ad_showMode;
            }

            public void setAd_showMode(String ad_showMode) {
                this.ad_showMode = ad_showMode;
            }

            public String getAd_systemScreenBrightness() {
                return ad_systemScreenBrightness;
            }

            public void setAd_systemScreenBrightness(String ad_systemScreenBrightness) {
                this.ad_systemScreenBrightness = ad_systemScreenBrightness;
            }

            public String getAd_videoRotateAngle() {
                return ad_videoRotateAngle;
            }

            public void setAd_videoRotateAngle(String ad_videoRotateAngle) {
                this.ad_videoRotateAngle = ad_videoRotateAngle;
            }

            public String getBootup() {
                return bootup;
            }

            public void setBootup(String bootup) {
                this.bootup = bootup;
            }


            public String getMusicVolume() {
                return musicVolume;
            }

            public void setMusicVolume(String musicVolume) {
                this.musicVolume = musicVolume;
            }

            public String getSerialNumber() {
                return serialNumber;
            }

            public void setSerialNumber(String serialNumber) {
                this.serialNumber = serialNumber;
            }

            public String getShutdown() {
                return shutdown;
            }

            public void setShutdown(String shutdown) {
                this.shutdown = shutdown;
            }

            public String getSipServer() {
                return sipServer;
            }

            public void setSipServer(String sipServer) {
                this.sipServer = sipServer;
            }
        }
    }
}
