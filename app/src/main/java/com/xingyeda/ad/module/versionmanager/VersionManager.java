package com.xingyeda.ad.module.versionmanager;

import android.content.Context;
import android.os.Build;

import com.xingyeda.ad.config.URLConfig;
import com.xingyeda.ad.util.CustomMainBoardUtil;
import com.xingyeda.ad.util.httputil.HttpRequestData;
import com.xingyeda.ad.util.httputil.TokenHttpRequestModel;
import com.xingyeda.config.DeviceConfig;
import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.http.BaseRequestData;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

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
                        CustomMainBoardUtil.installWithApkUrl(context,downloadUrl);
                    }else{
                        ToastUtils.showToastLong(context,"检测到升级，但是升级地址为空");
                    }
                }else{
                    ToastUtils.showToastLong(context,errorInfo);
                }
            }
        });
    }


    private static void checkNewVersions(Context context, final int currentVersionCode, final OnCheckCallBack checkCallBack){
        final HttpRequestData requestData = new HttpRequestData();
        requestData.setRequestURL(URLConfig.getPath(context,URLConfig.CHECK_NEW_VERSION));
        requestData.setRequestMode(BaseRequestData.RequestModeType.GET);
        requestData.setEnableToken(true);
        requestData.addRequestParams("os_version","" + Build.VERSION.SDK_INT);//系统版本  21代表5.0的系统  < 21则判断是4.4的机器  >= 21判断是5.0以上机器
        requestData.addRequestParams("os_model", DeviceConfig.os_model); // Q588=0,3188=1,3288=2,电视广告机=3,桌面广告机=4
        TokenHttpRequestModel.asynTokenRequestData(requestData, AppVersionResponseData.class, new HttpRequestModel.RequestCallBack() {
            @Override
            public void onResponseMainThread(BaseResponseData baseResponseData) {
                if(baseResponseData.isOperationSuccess()){
                    try{
                        AppVersionResponseData appVersionResponseData = (AppVersionResponseData)baseResponseData;
                        if(appVersionResponseData.getData() != null){
                            AppVersionResponseData.AppVersionBean versionBean = appVersionResponseData.getData();
                            if (currentVersionCode < ToolUtils.parseLong(versionBean.getVersionCode())) {
                                String downloadURL = versionBean.getDownloadUrl();
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
                        checkCallBack.callBack(false,null,"升级接口：" + baseResponseData.getErrorMsg());
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
}
