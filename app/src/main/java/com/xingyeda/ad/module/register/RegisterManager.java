package com.xingyeda.ad.module.register;

import android.content.Context;

import com.xingyeda.ad.config.DeviceUUIDManager;
import com.xingyeda.ad.config.URLConfig;
import com.xingyeda.ad.util.httputil.HttpObjResponseData;
import com.xingyeda.ad.util.httputil.HttpRequestData;
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
                    if(isRegister){
                        return;
                    }
                    try {
                        register();
                        Thread.sleep(30 * 1000);
                    }catch (Exception ex){

                    }

                }
            }).start();
        }
    }
    private void register(){
        final HttpRequestData requestData = new HttpRequestData();
        requestData.setRequestMode(BaseRequestData.RequestModeType.GET);
        requestData.setRequestURL(URLConfig.getPath(appContext,URLConfig.BIND_EQ_BY_MAC));
        requestData.addRequestParams("mac", DeviceUUIDManager.generateUUID(appContext));
        requestData.addRequestParams("eq_Version","version" + ToolUtils.getVersionCode(appContext));
        HttpRequestModel.asynRequestData(requestData, HttpObjResponseData.class, new HttpRequestModel.OnLYHttpRequestResponseListener() {
            @Override
            public void onResponse(BaseResponseData responseData) {
                if(responseData.isOperationSuccess()){
                    isRegister = true;
                    ToastUtils.showToastLong(appContext,"绑定成功");
                }else{
                    ToastUtils.showToastLong(appContext,"绑定失败:" + responseData.getErrorMsg());
                }
            }
        });
    }
}
