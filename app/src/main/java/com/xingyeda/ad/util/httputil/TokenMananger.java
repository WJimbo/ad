package com.xingyeda.ad.util.httputil;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xingyeda.ad.config.URLConfig;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.http.BaseRequestData;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenMananger {
    public interface CallBack{
        void getToken(boolean success,String token);
    }

    private List<CallBack> callBackList = new ArrayList<>();
    private static final Object lockObject = new Object();
    private static TokenMananger instance;
    private String token = "";
    private long tokenExpire = 0;
    private boolean isRequestToken = false;
    public void getToken(@NonNull CallBack callBack){
        if(!ToolUtils.string().isEmpty(token) && System.currentTimeMillis() < tokenExpire){
            callBack.getToken(true,token);
        }else{
            synchronized (lockObject){
                if(!ToolUtils.string().isEmpty(token) && System.currentTimeMillis() < tokenExpire){
                    callBack.getToken(true,token);
                }else{
                    callBackList.add(callBack);
                    requestToken(mContext);
                }
            }

        }
    }

    public static synchronized TokenMananger getInstance() {
        if(instance == null){
            instance = new TokenMananger();
        }
        return instance;
    }
    private String loginName,password;
    private Context mContext;

    public void init(Context context,String loginName, String password){
        this.loginName = loginName;
        this.password = password;
        this.mContext = context.getApplicationContext();
        requestToken(mContext);
    }
    private synchronized void requestToken(Context context){
        if(isRequestToken){
            return;
        }
        isRequestToken = true;
        HttpRequestData requestData = new HttpRequestData();
        requestData.setEnableToken(false);
        requestData.setRequestURL(URLConfig.getPath(context,URLConfig.GET_LOGIN_USER_TOKEN));
        requestData.setRequestMode(BaseRequestData.RequestModeType.POST);
        requestData.setMediaType(BaseRequestData.JSON);
        requestData.addBody("name",loginName);
        requestData.addBody("pwd",password);
        requestData.addBody("type","3");
        TokenHttpRequestModel.asynTokenRequestData(requestData, TokenResponseData.class, new HttpRequestModel.RequestCallBack() {
            @Override
            public void onResponseMainThread(BaseResponseData baseResponseData) {
                if(baseResponseData.isOperationSuccess()){
                    TokenResponseData tokenResponseData = (TokenResponseData)baseResponseData;
                    token = tokenResponseData.data.token;
                    tokenExpire = System.currentTimeMillis() + tokenResponseData.data.tokenExpire;
                    notifyToAllCallBack(true,token);
                }else{
                    notifyToAllCallBack(false,token);
                }
                isRequestToken = false;
            }

            @Override
            public void onResponseBackgroundThread(BaseResponseData baseResponseData) {

            }

            @Override
            public void dealBusinessError(boolean errorInMainThread, Exception ex) {

            }
        });
    }
    private void notifyToAllCallBack(boolean result,String token){
        synchronized (lockObject){
            ArrayList<CallBack> callBacks = new ArrayList<>();
            callBacks.addAll(callBackList);
            for(CallBack callBack : callBacks){
                callBackList.remove(callBack);
                callBack.getToken(result,token);
            }
        }
    }

    class TokenResponseData extends HttpObjResponseData{
        private TokenBean data;

        public TokenBean getData() {
            return data;
        }
    }
    static class TokenBean{

        /**
         * refleshToken :
         * refreshTokenExpire : 0
         * token :
         * tokenExpire : 0
         */

        private String refleshToken;
        private int refreshTokenExpire;
        private String token;
        private int tokenExpire;

        public String getRefleshToken() {
            return refleshToken;
        }

        public void setRefleshToken(String refleshToken) {
            this.refleshToken = refleshToken;
        }

        public int getRefreshTokenExpire() {
            return refreshTokenExpire;
        }

        public void setRefreshTokenExpire(int refreshTokenExpire) {
            this.refreshTokenExpire = refreshTokenExpire;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getTokenExpire() {
            return tokenExpire;
        }

        public void setTokenExpire(int tokenExpire) {
            this.tokenExpire = tokenExpire;
        }
    }

}
