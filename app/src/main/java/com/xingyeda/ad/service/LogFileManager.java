package com.xingyeda.ad.service;

import android.content.Context;


import com.ldl.okhttp.OkHttpUtils;
import com.ldl.okhttp.builder.PostFormBuilder;
import com.xingyeda.ad.config.DeviceUUIDManager;
import com.xingyeda.ad.config.URLConfig;
import com.xingyeda.ad.util.MyLog;
import com.xingyeda.ad.util.http.ConciseCallbackHandler;
import com.xingyeda.ad.util.http.ConciseStringCallback;

import com.zz9158.app.common.utils.ToolUtils;

import org.json.JSONObject;

import java.io.File;

public class LogFileManager {
    public static void uploadFiles(Context context,final String[] filenames){
        if(filenames.length == 0){
            MyLog.i("上传文件数组为空");
            return;

        }
        int exsitFileCount = 0;
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        for (String fileName : filenames){
            fileName = fileName + ".txt";
            File logFile = new File(MyLog.getPATH_LOGCAT() ,fileName);

            if(ToolUtils.file().isFileExists(logFile)){
                File logUploadFile = new File(MyLog.getPATH_LOGCAT(),"upload_" + fileName);
                ToolUtils.file().copyFile(logFile,logUploadFile);
                exsitFileCount ++;
                postFormBuilder.addFile("log",fileName, logUploadFile);
            }else{
                MyLog.i("上传文件" + fileName +"不存在");
            }
        }
        if(exsitFileCount == 0){
            MyLog.i("检测到上传文件不存在");
            return;
        }

        postFormBuilder.url(URLConfig.getPath(context,URLConfig.UPLOAD_LOG + "?eid=" + DeviceUUIDManager.generateUUID(context))).build().execute( new ConciseStringCallback(context, new ConciseCallbackHandler<String>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if ("200".equals(response.getString("status"))) {
                        MyLog.i("日志上传成功");
                    }else{
                        MyLog.i("日志上传失败");
                    }
                }catch (Exception ex){
                    MyLog.i("日志上传 Exception" + ex.getMessage());
                }
                for (String fileName : filenames) {
                    fileName = fileName + ".txt";
                    File logUploadFile = new File(MyLog.getPATH_LOGCAT(),"upload_" + fileName);
                    ToolUtils.file().deleteFile(logUploadFile);
                }
            }

            @Override
            public void onError(Exception e, int id) {
                MyLog.i("日志上传 onError" + e.getMessage());
            }
        }));
    }
}
