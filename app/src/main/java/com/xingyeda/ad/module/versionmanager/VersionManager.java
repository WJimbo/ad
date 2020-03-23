package com.xingyeda.ad.module.versionmanager;

import android.content.Context;
import android.os.Build;

import com.xingyeda.ad.MainApplication;
import com.xingyeda.ad.config.SettingConfig;
import com.xingyeda.ad.config.URLConfig;
import com.xingyeda.ad.util.httputil.HttpObjResponseData;
import com.xingyeda.ad.util.httputil.HttpRequestData;
import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.http.BaseRequestData;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

import top.wuhaojie.installerlibrary.AutoInstaller;

public class VersionManager {
    interface OnCheckCallBack{
        void callBack(boolean hasViewVersions,String downloadUrl,String errorInfo);
    }
    public static void checkVersions(final Context context){
        VersionManager.checkNewVersions(context, ToolUtils.getVersionCode(context), new VersionManager.OnCheckCallBack() {
            @Override
            public void callBack(boolean hasViewVersions, String downloadUrl, String errorInfo) {
                if(hasViewVersions){
                    if(!ToolUtils.string().isEmpty(downloadUrl)){
                        ToastUtils.showToastLong(context,"检测到升级版本");
                        installNewVersion(context,downloadUrl);
                    }else{
                        ToastUtils.showToastLong(context,"检测到升级，但是升级地址为空");
                    }
                }else{
                    ToastUtils.showToastLong(context,errorInfo);
                }
            }
        });
    }
    private static void installNewVersion(final Context context, String downloadUrl){
        AutoInstaller autoInstaller = new AutoInstaller.Builder(context)
                .setMode(AutoInstaller.MODE.AUTO_ONLY)
                .setOnStateChangedListener(new AutoInstaller.OnStateChangedListener() {
                    @Override
                    public void onStart() {
                        // 当后台安装线程开始时回调
                        ToastUtils.showToastLong(context.getApplicationContext(),"开始安装");
                    }

                    @Override
                    public void onComplete() {
                        // 当请求安装完成时回调
                        ToastUtils.showToastLong(context.getApplicationContext(),"安装完成");
                    }

                    @Override
                    public void onNeed2OpenService() {
                        // 当需要用户手动打开 `辅助功能服务` 时回调
                        // 可以在这里提示用户打开辅助功能
                        ToastUtils.showToastLong(context.getApplicationContext(),"请打开辅助功能服务");
                    }
                })
                .build();
        autoInstaller.installFromUrl(downloadUrl);
    }

    private static void checkNewVersions(Context context, final int currentVersionCode, final OnCheckCallBack checkCallBack){
        String url = URLConfig.getPath(context,URLConfig.CHECK_NEW_VERSION);
        final HttpRequestData requestData = new HttpRequestData();
        requestData.addRequestParams("sdkversion","" + Build.VERSION.SDK_INT);//系统版本  21代表5.0的系统  < 21则判断是4.4的机器  >= 21判断是5.0以上机器
        requestData.addRequestParams("test","0");
        requestData.addRequestParams("os_model", "ad_tv");
        requestData.setRequestURL(url);
        requestData.setRequestMode(HttpRequestData.RequestModeType.GET);
        HttpRequestModel.asynRequestData(requestData, AppVersionResponseData.class, new HttpRequestModel.OnLYHttpRequestResponseListener() {
            @Override
            public void onResponse(BaseResponseData responseData) {
                if(responseData.isOperationSuccess()){
                    try{
                        AppVersionResponseData appVersionResponseData = (AppVersionResponseData)responseData;
                        if(appVersionResponseData.getAppVersionBean() != null){
                            AppVersionResponseData.AppVersionBean versionBean = appVersionResponseData.getAppVersionBean();
                            if(versionBean.getFiles() != null && versionBean.getFiles().size() > 0) {
                                if (currentVersionCode < Integer.valueOf(versionBean.getVersionNumber())) {
                                    String downloadURL = versionBean.getServerURL() + versionBean.getFiles().get(0).getFileName();
                                    if(checkCallBack != null){
                                        checkCallBack.callBack(true,downloadURL,null);
                                    }
                                }else{
                                    if(checkCallBack != null){
                                        checkCallBack.callBack(false,null,"未检测到新版本");
                                    }
                                }
                            }else{
                                if(checkCallBack != null){
                                    checkCallBack.callBack(false,null,"升级接口：服务器未返回有效数据3");
                                }
                            }

                        }else{
                            if(checkCallBack != null){
                                checkCallBack.callBack(false,null,"升级接口：服务器未返回有效数据1");
                            }
                        }
                    }catch (Exception ex){
                        if(checkCallBack != null){
                            checkCallBack.callBack(false,null,"升级接口：服务器未返回有效数据2");
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
