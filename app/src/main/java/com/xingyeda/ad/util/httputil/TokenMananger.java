package com.xingyeda.ad.util.httputil;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xingyeda.ad.config.URLConfig;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.http.BaseRequestData;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

import java.util.ArrayList;
import java.util.List;

public class TokenMananger {
    public interface CallBack{
        void getToken(boolean success, String token,BaseResponseData tokenResponseData);
    }
    private BaseResponseData mTokenResponseData;
    private List<CallBack> callBackList = new ArrayList<>();
    private static final Object lockObject = new Object();
    private static TokenMananger instance;
    private String token = "";
    private long tokenExpire = 0;
    private boolean isRequestToken = false;
    public void resetToken(){
        token = "";
        tokenExpire = 0;
    }
    public void getToken(@NonNull CallBack callBack){
        if(!ToolUtils.string().isEmpty(token) && System.currentTimeMillis() < tokenExpire){
            callBack.getToken(true,token,mTokenResponseData);
        }else{
            synchronized (lockObject){
                if(!ToolUtils.string().isEmpty(token) && System.currentTimeMillis() < tokenExpire){
                    callBack.getToken(true,token,mTokenResponseData);
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
            MyLog.i("当前正在刷新TOKEN接口");
            return;
        }
        MyLog.i("开始刷新TOKEN接口");
        isRequestToken = true;
        HttpRequestData requestData = new HttpRequestData();
        requestData.setEnableToken(false);
        requestData.setRequestURL(URLConfig.getPath(context,URLConfig.GET_LOGIN_USER_TOKEN));
        requestData.setRequestMode(BaseRequestData.RequestModeType.POST);
        requestData.setMediaType(BaseRequestData.JSON);
        requestData.addBody("name",loginName);
        requestData.addBody("pwd",password);
        requestData.addBody("type","1");//设备类型,1 设备登陆 2 APP/业主登陆 3 后端人员登陆
        TokenHttpRequestModel.asynTokenRequestData(requestData, TokenResponseData.class, new HttpRequestModel.RequestCallBack() {
            @Override
            public void onResponseMainThread(BaseResponseData baseResponseData) {
                mTokenResponseData = baseResponseData;
                isRequestToken = false;
                if(baseResponseData.isOperationSuccess()){
                    MyLog.i("刷新TOKEN接口完成");
                    TokenResponseData tokenResponseData = (TokenResponseData)baseResponseData;
                    token = tokenResponseData.data.token;
                    tokenExpire = System.currentTimeMillis() + tokenResponseData.data.tokenExpire;
                    notifyToAllCallBack(true,token);
                }else{
                    notifyToAllCallBack(false,token);
                    MyLog.i("刷新TOKEN接口失败：" + baseResponseData.getErrorMsg() + "  JSONVALUE:" + baseResponseData.getJsonValueString());
                }

            }

            @Override
            public void onResponseBackgroundThread(BaseResponseData baseResponseData) {

            }

            @Override
            public void dealBusinessError(boolean errorInMainThread, Exception ex) {
                isRequestToken = false;
                MyLog.i("刷新TOKEN接口异常:" + (ex != null ? ex.getMessage() : "null"));
            }
        });
    }
    private void notifyToAllCallBack(boolean result,String token){
        synchronized (lockObject){
            ArrayList<CallBack> callBacks = new ArrayList<>();
            callBacks.addAll(callBackList);
            for(CallBack callBack : callBacks){
                try {
                    callBackList.remove(callBack);
                    callBack.getToken(result,token,mTokenResponseData);
                }catch (Exception ex){

                }
            }
        }
    }

    static class TokenResponseData extends HttpObjResponseData{
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
