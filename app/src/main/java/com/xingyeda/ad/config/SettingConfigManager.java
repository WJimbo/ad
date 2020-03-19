package com.xingyeda.ad.config;

import android.content.Context;

import com.gavinrowe.lgw.library.SimpleTimerTask;
import com.gavinrowe.lgw.library.SimpleTimerTaskHandler;
import com.google.gson.annotations.SerializedName;
import com.xingyeda.ad.util.httputil.HttpObjResponseData;
import com.xingyeda.ad.util.httputil.HttpRequestData;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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
        }
    }

    public  void updateSettingForNet(final Context context){
        HttpRequestData requestData = new HttpRequestData();
        requestData.setRequestURL( URLConfig.getPath(context, URLConfig.USERSET_PATH));
        requestData.setRequestMode(HttpRequestData.RequestModeType.GET);
        requestData.addRequestParams("type","select");
        requestData.addRequestParams("eid", DeviceUUIDManager.generateUUID(context));
        HttpRequestModel.asynRequestData(requestData, SettingResponseData.class, new HttpRequestModel.OnLYHttpRequestResponseListener() {
            @Override
            public void onResponse(BaseResponseData responseData) {
                if(responseData.isOperationSuccess()){
                    SettingResponseData settingResponseData = (SettingResponseData)responseData;
                    if(settingResponseData.settingItems != null){
                        boolean settingChanged = false;
                        for (SettingResponseData.SettingItem settingItem : settingResponseData.settingItems){
                            if(settingItem.logo == null){
                                continue;
                            }

                            if("ADSetting_VideoRotateAngle".equals(settingItem.logo)){
                                try{
                                    float value = Float.parseFloat(settingItem.value);
                                    if (value != SettingConfig.getScreenRotateAngle(context)) {
                                        SettingConfig.setVideoRatateAngle(context,value);
                                        settingChanged = true;
                                    }
                                }catch (Exception ex){

                                }
                            }else if("ADSetting_ShowDebugView".equals(settingItem.logo)){
                                try{
                                    boolean show = "1".equals(settingItem.value);
                                    if (show != SettingConfig.isShowDebugView(context)) {
                                        SettingConfig.setShowDebugView(context,show);
                                        settingChanged = true;
                                    }
                                }catch (Exception ex){

                                }
                            }


                        }
                        if(settingChanged){
                            EventBus.getDefault().post(new SettingConfig.VideoRotateAngleChangedEventData());
                        }
                    }
                }
            }
        });
    }
    class SettingResponseData extends HttpObjResponseData {
        @SerializedName("obj")
        private List<SettingItem> settingItems;
        class SettingItem{

            /**
             * id : 11509
             * value : 70
             * eid : b6a9600f67d1
             * logo : sipTimeout
             */

            private int id;
            private String value;
            private String eid;
            private String logo;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getEid() {
                return eid;
            }

            public void setEid(String eid) {
                this.eid = eid;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }
        }
    }
}
