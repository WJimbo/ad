package com.xingyeda.ad.module.register;

import android.content.Context;

import com.xingyeda.ad.config.DeviceUUIDManager;
import com.xingyeda.ad.config.URLConfig;
import com.xingyeda.ad.util.httputil.HttpObjResponseData;
import com.xingyeda.ad.util.httputil.HttpRequestData;
import com.xingyeda.ad.util.httputil.TokenHttpRequestModel;
import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.http.BaseRequestData;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

public class RegisterManager {

    private static RegisterManager instance = new RegisterManager();
    private Context appContext;

    private RegisterManager(){

    }

    public static RegisterManager getInstance() {
        return instance;
    }
    private boolean isStartTimer = false;
    private boolean isRegister = false;
    public  void startToRegister(Context context){
        if(!isStartTimer){
            isStartTimer = true;
            appContext = context.getApplicationContext();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isRegister){
                        try {
                            register();
                            Thread.sleep(30 * 1000);
                        }catch (Exception ex){

                        }
                    }
                }
            }).start();
        }
    }
    private void register(){
        final HttpRequestData requestData = new HttpRequestData();
        requestData.setRequestMode(BaseRequestData.RequestModeType.POST);
        requestData.setRequestURL(URLConfig.getPath(appContext,URLConfig.BIND_EQ_BY_MAC));
        requestData.setEnableToken(true);
        requestData.addRequestParams("command","");
        requestData.addRequestParams("deviceId",DeviceUUIDManager.generateUUID(appContext));
        requestData.addRequestParams("info","");
        requestData.addRequestParams("os_model","3");//设备型号，Q588=0,3188=1,3288=2,电视广告机=3,桌面广告机=4
        requestData.addRequestParams("pushType","");
        requestData.addRequestParams("version",ToolUtils.getVersionName(appContext) + "_" + ToolUtils.getVersionCode(appContext));
        TokenHttpRequestModel.asynTokenRequestData(requestData, HttpObjResponseData.class, new HttpRequestModel.RequestCallBack() {
            @Override
            public void onResponseMainThread(BaseResponseData baseResponseData) {
                if(baseResponseData.isOperationSuccess()){
                    isRegister = true;
                    ToastUtils.showToastLong(appContext,"绑定成功");
                }else{
                    ToastUtils.showToastLong(appContext,"绑定失败:" + baseResponseData.getErrorMsg());
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
}
