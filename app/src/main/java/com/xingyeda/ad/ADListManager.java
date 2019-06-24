package com.xingyeda.ad;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.altang.app.common.utils.GsonUtil;
import com.altang.app.common.utils.UIUtils;
import com.mazouri.tools.Tools;

import com.altang.app.common.utils.http.BaseResponseData;
import com.altang.app.common.utils.http.HttpRequestData;
import com.altang.app.common.utils.http.HttpRequestModel;
import com.xingyeda.ad.logdebug.LogDebugUtil;
import com.xingyeda.ad.util.MyLog;
import com.xingyeda.ad.vo.AdItem;
import com.xingyeda.ad.vo.AdListResponseData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ADListManager {
    public interface OnDataChangeCallBackListener{
        void dataChanged(AdListResponseData adListResponseData);
    }
    private OnDataChangeCallBackListener onDataChangeCallBackListener;

    public void setOnDataChangeCallBackListener(OnDataChangeCallBackListener onDataChangeCallBackListener) {
        this.onDataChangeCallBackListener = onDataChangeCallBackListener;
    }

    private static ADListManager instance;
    private AdListResponseData adListResponseData;
    private File locationSaveFile;//数据缓存文件
    private Context context;

    private ADListManager(Context context){
        this.context = context;
        init();
        readListFromLocation();
    }

    public AdListResponseData getAdListResponseData() {
        return adListResponseData;
    }

    public synchronized static ADListManager getInstance(Context context) {
        if(instance == null){
            instance = new ADListManager(context.getApplicationContext());
        }
        return instance;
    }
    /**
     *
     * 初始化目录
     *
     * */
    private void init() {
        String rootPath;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            rootPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            rootPath = context.getFilesDir().getAbsolutePath();
        }
        rootPath = rootPath + File.separator + "XYD_AD" + File.separator + "data";
        File rootFile = new File(rootPath);
        locationSaveFile = new File(rootPath,BaseApplication.andoridId + "_adList.db");
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
    }
    private boolean needUpdateList = false;
    private boolean isUpdatingList = false;
    public void setNeedUpdateList(){
        needUpdateList = true;
        updateList();
    }

    private void updateList(){
        if(isUpdatingList){
            return;
        }
        LogDebugUtil.appendLog("正在开始调用广告数据");
        isUpdatingList = true;
        needUpdateList = false;
        final HttpRequestData requestData = new HttpRequestData();
        requestData.setRequestURL(BaseApplication.www + "GetAdversitingByMac/R?mac=" + BaseApplication.andoridId);
        requestData.setRequestMode(HttpRequestData.RequestModeType.GET);
        HttpRequestModel.asynRequestData(requestData, AdListResponseData.class, new HttpRequestModel.OnLYHttpRequestResponseListener() {
            @Override
            public void onResponse(BaseResponseData responseData) {
                isUpdatingList = false;
                if(responseData.isOperationSuccess()){
                    adListResponseData = (AdListResponseData)responseData;
                    saveListToLocation();
                    LogDebugUtil.appendLog("调用广告数据成功:" + adListResponseData.getObj().size() + "条");
                    if(onDataChangeCallBackListener != null){
                        onDataChangeCallBackListener.dataChanged(adListResponseData);
                    }
                }else{
                    LogDebugUtil.appendLog("调用广告数据失败:" + responseData.getErrorMsg());
                    MyLog.d("更新广告接口出错:" + responseData.getErrorMsg());
                    needUpdateList = true;
                }
                if(needUpdateList){
                    UIUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            updateList();
                        }
                    },10000);
                }
            }
        });
    }

    private void saveListToLocation(){
        if(adListResponseData != null){
            String listStr = GsonUtil.gson.toJson(adListResponseData);
            Tools.file().writeFileFromString(locationSaveFile,listStr,false);
        }else{
            Tools.file().writeFileFromString(locationSaveFile,"",false);
        }
    }
    private void readListFromLocation(){
        String locationStr = Tools.file().readFile2String(locationSaveFile,null);
        if(!Tools.string().isEmpty(locationStr)){
            try {
                 adListResponseData = GsonUtil.gson.fromJson(locationStr,AdListResponseData.class);
                if(onDataChangeCallBackListener != null){
                    onDataChangeCallBackListener.dataChanged(adListResponseData);
                }
            }catch (Exception ex){

            }
        }
    }
}
