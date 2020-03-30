package com.xingyeda.ad.util.httputil;


import com.zz9158.app.common.utils.LoggerHelper;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

public class TokenHttpRequestModel{

    /**
     * 异步网络请求
     * @param requestData
     * @param dataModelClass
     * @param listener
     */
    public static void asynTokenRequestData(final HttpRequestData requestData, final Class<? extends BaseResponseData> dataModelClass, final HttpRequestModel.RequestCallBack listener){
        if(requestData.isEnableToken()){
            TokenMananger.getInstance().getToken(new TokenMananger.CallBack() {
                @Override
                public void getToken(boolean success, String token) {
                    if(success){
                        requestData.setToken(token);
                        HttpRequestModel.asynRequestData(requestData,dataModelClass,listener);
                    }else {
                        LoggerHelper.i("获取TOKEN失败，无法进行后续接口访问：" + requestData.getRequestURL());
                    }
                }
            });
        }else{
            HttpRequestModel.asynRequestData(requestData,dataModelClass,listener);
        }
    }
}
