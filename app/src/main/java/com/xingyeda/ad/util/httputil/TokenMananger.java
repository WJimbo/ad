package com.xingyeda.ad.util.httputil;

import android.content.Context;

import com.xingyeda.ad.config.URLConfig;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.http.BaseRequestData;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

import java.util.HashMap;
import java.util.Map;

public class TokenMananger {
    public interface CallBack{
        void getToken(boolean success,String token);
    }
    private static TokenMananger instance;
    private String token = "";
    private long tokenExpire = 0;

    public void getToken(CallBack callBack){
        if(!ToolUtils.string().isEmpty(token) && System.currentTimeMillis() < tokenExpire){
            if(callBack != null){
                callBack.getToken(true,token);
            }
        }else{
            requestToken(mContext,callBack);
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
        requestToken(mContext,null);
    }
    private void requestToken(Context context, final CallBack callBack){
        HttpRequestData requestData = new HttpRequestData();
        requestData.setEnableToken(false);
        requestData.setRequestURL(URLConfig.getPath(context,URLConfig.GET_LOGIN_USER_TOKEN));
        requestData.setRequestMode(BaseRequestData.RequestModeType.POST);
        requestData.setMediaType(BaseRequestData.JSON);
        Map map = new HashMap();
        map.put("name",loginName);
        map.put("pwd",password);
        map.put("type","3");
        requestData.addBody("loginDTO",map);
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
                    if(callBack != null){
                        callBack.getToken(true,token);
                    }
                }else{
                    if(callBack != null){
                        callBack.getToken(false,token);
                    }
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
