package com.xingyeda.ad.module.logger;

import android.content.Context;

import com.xingyeda.ad.config.DeviceUUIDManager;
import com.xingyeda.ad.config.URLConfig;
import com.xingyeda.ad.util.MyLog;
import com.xingyeda.ad.util.httputil.HttpObjResponseData;
import com.xingyeda.ad.util.httputil.HttpRequestData;
import com.xingyeda.ad.util.httputil.TokenHttpRequestModel;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.http.BaseRequestData;
import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HttpRequestModel;

import java.io.File;

public class LogFileManager {
    public static void uploadFiles(Context context,final String[] filenames){
        if(filenames.length == 0){
            MyLog.i("上传文件数组为空");
            return;

        }
        HttpRequestData requestData = new HttpRequestData();
        requestData.setRequestURL(URLConfig.getPath(context,URLConfig.UPLOAD_LOG));
        requestData.setRequestMode(BaseRequestData.RequestModeType.POST);
        requestData.setEnableToken(true);
        requestData.addRequestParams("deviceId",DeviceUUIDManager.generateUUID(context));
        int exsitFileCount = 0;
        for (String fileName : filenames){
            fileName = fileName + ".txt";
            File logFile = new File(MyLog.getPATH_LOGCAT() ,fileName);

            if(ToolUtils.file().isFileExists(logFile)){
                File logUploadFile = new File(MyLog.getPATH_LOGCAT(),"upload_" + fileName);
                ToolUtils.file().copyFile(logFile,logUploadFile);
                exsitFileCount ++;
                requestData.addUploadFileArray("log",fileName,logUploadFile);
            }else{
                MyLog.i("上传文件" + fileName +"不存在");
            }
        }
        if(exsitFileCount == 0){
            MyLog.i("检测到上传文件不存在");
            return;
        }

        TokenHttpRequestModel.asynTokenRequestData(requestData, HttpObjResponseData.class, new HttpRequestModel.RequestCallBack() {
            @Override
            public void onResponseMainThread(BaseResponseData baseResponseData) {
                MyLog.i(baseResponseData.isOperationSuccess() ? "日志上传成功":"日志上传失败:" + baseResponseData.getErrorMsg());
                for (String fileName : filenames) {
                    fileName = fileName + ".txt";
                    File logUploadFile = new File(MyLog.getPATH_LOGCAT(),"upload_" + fileName);
                    ToolUtils.file().deleteFile(logUploadFile);
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
