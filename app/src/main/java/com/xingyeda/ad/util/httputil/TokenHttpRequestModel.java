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
        asynTokenRequestData(requestData,true,dataModelClass,listener);
    }

    /**
     * 异步网络请求
     * @param requestData
     * @param dataModelClass
     * @param listener
     */
    public static void asynTokenRequestData(final HttpRequestData requestData, final boolean firstRequest, final Class<? extends BaseResponseData> dataModelClass, final HttpRequestModel.RequestCallBack listener){
        if(requestData.isEnableToken()){
            TokenMananger.getInstance().getToken(new TokenMananger.CallBack() {
                @Override
                public void getToken(boolean success, String token) {
                    if(success){
                        requestData.setToken(token);
                        HttpRequestModel.asynRequestData(requestData, dataModelClass, new HttpRequestModel.RequestCallBack() {
                            @Override
                            public void onResponseMainThread(BaseResponseData baseResponseData) {
                                if(baseResponseData.isOperationSuccess() == false && baseResponseData.getErrorCode() == 40001){
                                    TokenMananger.getInstance().resetToken();
                                    //第一次请求这个地址 发现token过期了  就让他在去更新token请求一次
                                    if(firstRequest){
                                        asynTokenRequestData(requestData,false,dataModelClass,listener);
                                    }
                                    return;
                                }
                                if(listener != null){
                                    listener.onResponseMainThread(baseResponseData);
                                }
                            }

                            @Override
                            public void onResponseBackgroundThread(BaseResponseData baseResponseData) {
                                if(listener != null){
                                    listener.onResponseBackgroundThread(baseResponseData);
                                }
                            }

                            @Override
                            public void dealBusinessError(boolean errorInMainThread, Exception ex) {
                                if(listener != null){
                                    listener.dealBusinessError(errorInMainThread,ex);
                                }
                            }
                        });
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
