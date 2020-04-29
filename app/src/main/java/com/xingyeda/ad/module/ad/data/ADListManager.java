package com.xingyeda.ad.module.ad.data;

import android.content.Context;
import android.os.Environment;

import com.gavinrowe.lgw.library.SimpleTimerTask;
import com.gavinrowe.lgw.library.SimpleTimerTaskHandler;
import com.mazouri.tools.Tools;
import com.xingyeda.ad.config.DeviceUUIDManager;
import com.xingyeda.ad.config.SettingConfig;
import com.xingyeda.ad.config.URLConfig;
import com.xingyeda.ad.logdebug.LogDebugUtil;
import com.xingyeda.ad.util.httputil.HttpRequestData;
import com.xingyeda.ad.util.httputil.TokenHttpRequestModel;
import com.zz9158.app.common.utils.GsonUtil;
import com.zz9158.app.common.utils.http.BaseRequestData;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ADListManager {
    public interface OnAdListChangedListener{
        void adListChanged(List<AdItem> adItems);
    }

    private List<OnAdListChangedListener> onAdListChangedListenerList = new ArrayList<>();
    private final static Object lockObject = new Object();
    public void addOnAdListChangedListener(OnAdListChangedListener adListChangedListener){
        synchronized (lockObject){
            if(adListChangedListener != null){
                onAdListChangedListenerList.add(adListChangedListener);
            }
        }
    }
    public void removeAdListChangedListener(OnAdListChangedListener onAdListChangedListener){
        synchronized (lockObject){
            if(onAdListChangedListener != null){
                onAdListChangedListenerList.remove(onAdListChangedListener);
            }
        }
    }

    private void sendEventToDataChangedListeners(){
        synchronized (lockObject){
            for(OnAdListChangedListener listChangedListener : onAdListChangedListenerList){
                try {
                    if(adListResponseData != null && adListResponseData.getData() != null){
                        listChangedListener.adListChanged(adListResponseData.getData());
                    }else{
                        listChangedListener.adListChanged(new ArrayList<AdItem>());
                    }
                }catch (Exception ex){

                }
            }
        }
    }

    /**
     * 自动请求广告接口列表间隔时间
     */
    private static final long AUTO_REQUEST_ADLIST_TIME = 5 * 60 * 1000;

    private static ADListManager instance;
    private AdListResponseData adListResponseData;
    private File locationSaveFile;//数据缓存文件
    private Context context;

    private ADListManager(Context context){
        this.context = context;
        init();
        AdItem.DownloadRootPath = DownloadManager.getDownloadRootPath(context);
        AdItem.VideoRotateAngle = SettingConfig.getScreenRotateAngle(context);
        readListFromLocation();
        //开始请求数据
        //容错，怕偶尔收不到服务器推送，采用轮询的方式获取数据。
        SimpleTimerTask loopTask = new SimpleTimerTask(AUTO_REQUEST_ADLIST_TIME) {
            @Override
            public void run() {
                setNeedUpdateList();
            }
        };
        SimpleTimerTaskHandler.getInstance().sendTask(1, loopTask);
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
        locationSaveFile = new File(rootPath,DeviceUUIDManager.generateUUID(context) + "_adList.db");
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
    }
    private boolean isUpdatingList = false;
    public void setNeedUpdateList(){
        updateList();
    }

    private synchronized void updateList(){
        if(isUpdatingList){
            return;
        }
        LogDebugUtil.appendLog("正在开始调用广告数据");
        isUpdatingList = true;
        final HttpRequestData requestData = new HttpRequestData();
        requestData.setRequestURL(URLConfig.getPath(context, URLConfig.REQUEST_AD_LIST));
        requestData.setRequestMode(BaseRequestData.RequestModeType.POST);
        requestData.setEnableToken(true);
        requestData.addRequestParams("mac", DeviceUUIDManager.generateUUID(context));
        TokenHttpRequestModel.asynTokenRequestData(requestData, AdListResponseData.class, new HttpRequestModel.RequestCallBack() {
            @Override
            public void onResponseMainThread(BaseResponseData baseResponseData) {
                isUpdatingList = false;
                if(baseResponseData.isOperationSuccess()){
                    AdItem.VideoRotateAngle = SettingConfig.getScreenRotateAngle(context);
                    String lastStr = "";
                    if(adListResponseData != null && adListResponseData.getJsonValueString() != null){
                        lastStr = adListResponseData.getJsonValueString();
                    }
                    adListResponseData = (AdListResponseData)baseResponseData;
                    LogDebugUtil.appendLog("调用广告数据成功:" + adListResponseData.getData().size() + "条");
//                    MyLog.i("调用广告数据成功:" + adListResponseData.getObj().size() + "条");
                    if(!lastStr.equals(baseResponseData.getJsonValueString())){
                        saveListToLocation();
                        sendEventToDataChangedListeners();
                    }else{
//                        MyLog.i("广告数据接口请求成功，无需保存");
                    }
                    downloadADFiles();
                }else{
                    LogDebugUtil.appendLog("调用广告数据失败:" + baseResponseData.getErrorMsg());
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

    private synchronized void saveListToLocation(){
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
                downloadADFiles();
                sendEventToDataChangedListeners();
            }catch (Exception ex){

            }
        }
    }

    private synchronized void downloadADFiles(){
        if (adListResponseData != null && adListResponseData.getData() != null) {
            List<AdItem> adItems = new ArrayList<>();
            adItems.addAll(adListResponseData.getData());
            List<DownloadManager.DownloadItem> downloadItemList = new ArrayList<>();
            for (AdItem adItem : adItems) {
                //不支持视频模式的时候 过滤掉视频文件的下载
                if (!adItem.isFileExsits()) {
                    DownloadManager.DownloadItem downloadItem = new DownloadManager.DownloadItem();
                    downloadItem.url = adItem.getUrl();
                    downloadItem.fileType = adItem.getType();
                    downloadItem.savePath = adItem.locationFile();
                    downloadItem.videoRotateAngle =  AdItem.VideoRotateAngle;
                    downloadItemList.add(downloadItem);
                }
            }
            DownloadManager.getInstance().startDownLoadWithList(downloadItemList);
        }
    }
}
