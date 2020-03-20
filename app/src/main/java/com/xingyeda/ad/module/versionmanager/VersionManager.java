package com.xingyeda.ad.module.versionmanager;

import android.content.Context;

import com.xingyeda.ad.config.URLConfig;
import com.xingyeda.ad.util.httputil.HttpObjResponseData;
import com.xingyeda.ad.util.httputil.HttpRequestData;
import com.zz9158.app.common.utils.http.BaseRequestData;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

public class VersionManager {
    public interface OnCheckCallBack{
        void callBack(boolean hasViewVersions,String downloadUrl,String errorInfo);
    }
    public static void checkNewVersions(Context context, final int currentVersionCode, final OnCheckCallBack checkCallBack){
        HttpRequestData requestData = new HttpRequestData();
        requestData.setRequestMode(BaseRequestData.RequestModeType.GET);
        requestData.setRequestURL(URLConfig.getPath(context,URLConfig.CHECK_NEW_VERSIONS));
        HttpRequestModel.asynRequestData(requestData, CheckVersionResponseData.class, new HttpRequestModel.OnLYHttpRequestResponseListener() {
            @Override
            public void onResponse(BaseResponseData responseData) {
                if(responseData.isOperationSuccess()){
                    CheckVersionResponseData checkVersionResponseData = (CheckVersionResponseData)responseData;
                    if(checkVersionResponseData.obj != null){
                        if(currentVersionCode < checkVersionResponseData.obj.versionNumber){
                            if(checkCallBack != null){
                                checkCallBack.callBack(true,checkVersionResponseData.obj.getUrl(),null);
                            }
                        }else{
                            if(checkCallBack != null){
                                checkCallBack.callBack(false,checkVersionResponseData.obj.getUrl(),"未检测到新版本");
                            }
                        }
                    }else{
                        if(checkCallBack != null){
                            checkCallBack.callBack(false,null,"升级接口：服务器未返回有效数据");
                        }
                    }
                }else{
                    if(checkCallBack != null){
                        checkCallBack.callBack(false,null,"升级接口：" + responseData.getErrorMsg());
                    }
                }
            }
        });
    }

    static class CheckVersionResponseData extends HttpObjResponseData{
        VersionItem obj;

        public VersionItem getObj() {
            return obj;
        }

        public void setObj(VersionItem obj) {
            this.obj = obj;
        }
    }
    static class VersionItem{
        private String url;

        private String updateTime;

        private int versionNumber;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(int versionNumber) {
            this.versionNumber = versionNumber;
        }
    }
}
