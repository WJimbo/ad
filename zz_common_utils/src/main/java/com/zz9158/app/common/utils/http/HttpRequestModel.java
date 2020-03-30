package com.zz9158.app.common.utils.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.reflect.TypeToken;
import com.mazouri.tools.Tools;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zz9158.app.common.utils.GsonUtil;
import com.zz9158.app.common.utils.LoggerHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tangyongx on 26/11/2018.
 */

public class HttpRequestModel {
    private HttpRequestModel(){

    }
    public interface RequestCallBack{
        void onResponseMainThread(BaseResponseData baseResponseData);
        void onResponseBackgroundThread(BaseResponseData baseResponseData);
        void dealBusinessError(boolean errorInMainThread, Exception ex);
    }
    public interface OnDownloadResponseListener{
        void progress(long totalSize, long downloadSize);
        void downloadError(String errorMsg);
        void downloadSuceess();
    }
    public static void downFile(final String downloadURL,final String savePath,final OnDownloadResponseListener listener) {

        OkHttpClient mOkHttpClent =new OkHttpClient.Builder().connectTimeout(60* 1000 * 2, TimeUnit.MILLISECONDS).readTimeout(60* 1000 * 2,TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder()
                .url(downloadURL)
                //.addHeader("Authorization","Bearer " + MConfig.getInstance().getAccessToken())
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(listener != null){
                            listener.downloadError(e.getMessage());
                        }
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;//输入流
                FileOutputStream fos = null;//输出流
                File file = new File(savePath);// 设置路径
                try {
                    if(file.exists()){
                        file.delete();
                    }
                }catch (Exception ex){
                }
                file.createNewFile();
                try {
                    is = response.body().byteStream();//获取输入流
                    final long total = response.body().contentLength();//获取文件大小
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if(listener != null){
                                listener.progress(total,0);
                            }
                        }
                    });

                    if(is != null){
                        fos = new FileOutputStream(file);
                        byte[] buf = new byte[1024 * 100];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fos.write(buf, 0, ch);
                            process += ch;
                            final int finalProcess = process;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(listener != null){
                                        listener.progress(total,finalProcess);
                                    }
                                }
                            });

                        }

                    }
                    fos.flush();
                    // 下载完成
                    if(fos != null){
                        fos.close();
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if(listener != null){
                                listener.downloadSuceess();
                            }
                        }
                    });

                } catch (final Exception e) {
                    try {
                        if(file.exists()){
                            file.delete();
                        }
                    }catch (Exception ex){
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if(listener != null){
                                listener.downloadError(e.getMessage());
                            }
                        }
                    });

                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }


    /**
     * 同步请求网络方法
     * @param requestData
     * @param dataModelClass
     * @param callBack
     */
    public static void synRequestData(BaseRequestData requestData,
                                                  final Class<? extends BaseResponseData> dataModelClass,RequestCallBack callBack){
        OkHttpClient okHttpClient =  initHttpClient(requestData);
        Request request = createRequest(requestData);
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String responseBody = response.body().string();
            dealResponseData(requestData,request,dataModelClass,responseBody,null,callBack);
        } catch (Exception e) {
            dealResponseData(requestData,request,dataModelClass,null,e,callBack);
        }
        System.gc();
    }


    /**
     * 异步网络请求
     * @param requestData
     * @param dataModelClass
     * @param callBack
     */
    public static void asynRequestData(final BaseRequestData requestData, final Class<? extends BaseResponseData> dataModelClass, final RequestCallBack callBack){
        OkHttpClient okHttpClient =  initHttpClient(requestData);
        final Request request = createRequest(requestData);
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, final IOException e) {
                dealResponseData(requestData,request,dataModelClass,null,e,callBack);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                dealResponseData(requestData,request,dataModelClass,body,null,callBack);
            }
        });
//        if(requestData.getRequestMode() == BaseRequestData.RequestModeType.POSTFILE
//                && !requestData.getUploadFileDataList().isEmpty()){
//            OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
//            PostFormBuilder postFormBuilder = OkHttpUtils.post();
//            for(BaseRequestData.UploadFileData uploadFileData : requestData.getUploadFileDataList()){
//                postFormBuilder.addFile(uploadFileData.key,uploadFileData.fileName,uploadFileData.file);
//            }
//            postFormBuilder.url(requestData.getRequestURL() + requestData.getParamsString())
//                    .build()
//                    .connTimeOut(requestData.getTimeOutMill())
//                    .writeTimeOut(requestData.getTimeOutMill())
//                    .execute(new StringCallback() {
//                        @Override
//                        public void onError(Call call, Exception e, int id) {
//                            dealResponseData(requestData,null,dataModelClass,null,e,callBack);
//                        }
//
//                        @Override
//                        public void onResponse(String response, int id) {
//                            dealResponseData(requestData,null,dataModelClass,response,null,callBack);
//                        }
//                    });
//        }else{
//
//        }
    }

    private static Request createRequest(BaseRequestData requestData){
        Request.Builder requestBuilder = null;
        if(BaseRequestData.RequestModeType.POST == requestData.getRequestMode()){
            //MediaType  设置Content-Type 标头中包含的媒体类型值
            RequestBody requestBody;
            if(requestData.getUploadFileDataList().isEmpty()){//没有文件的话走普通POST流程
                requestBody = createRequestBody(requestData);
            }else{
                requestBody = createUploadFileRequestBody(requestData);
            }
            requestBuilder = new Request.Builder().url(requestData.getRequestURL() + requestData.getParamsString()).post(requestBody);
        }else if(BaseRequestData.RequestModeType.GET == requestData.getRequestMode()){
            requestBuilder = new Request.Builder().url(requestData.getRequestURL() + requestData.getParamsString()).get();
        }else if(BaseRequestData.RequestModeType.PUT == requestData.getRequestMode()){
            //MediaType  设置Content-Type 标头中包含的媒体类型值
            RequestBody requestBody = createRequestBody(requestData);
            requestBuilder = new Request.Builder().url(requestData.getRequestURL() + requestData.getParamsString()).put(requestBody);
        }else if(BaseRequestData.RequestModeType.DELETE == requestData.getRequestMode()){
            RequestBody requestBody = createRequestBody(requestData);
            requestBuilder = new Request.Builder().url(requestData.getRequestURL() + requestData.getParamsString()).delete(requestBody);
        }
//        requestBuilder.addHeader("Authorization",requestData.getAuthorization());
        if(requestData.getHeaderParams().size() > 0){
            for (String key : requestData.getHeaderParams().keySet()) {
                //map.keySet()返回的是所有key的值
                String value = requestData.getHeaderParams().get(key);//得到每个key多对用value的值
                requestBuilder.addHeader(key,value);
            }
        }

        return requestBuilder.build();
    }

    /**
     * post的请求参数，构造RequestBody
     * @param requestData
     * @return
     */
    private static RequestBody createRequestBody(BaseRequestData requestData){
        RequestBody body;
        if(requestData.isEncrypt()){
            body = RequestBody.create(BaseRequestData.FORM_CONTENT_TYPE, RequestEncrypt.encrypt(requestData.getBodyGsonString()));
        }else if(BaseRequestData.JSON.equals(requestData.getMediaType())){
            body = RequestBody.create(BaseRequestData.JSON, requestData.getBodyGsonString());
        }else if(BaseRequestData.FORM_CONTENT_TYPE.equals(requestData.getMediaType())){
            body = RequestBody.create(BaseRequestData.FORM_CONTENT_TYPE,requestData.getBodyParamsString());
        }else{
            body = RequestBody.create(BaseRequestData.TEXT,requestData.getBodyParamsString());
        }
        return body;

//        FormBody.Builder builder=new FormBody.Builder();
//        if(requestData.getBodyMap() != null){
//            Iterator<String> iterator = requestData.getBodyMap().keySet().iterator();
//            String key = "";
//            while (iterator.hasNext()) {
//                key = iterator.next().toString();
//
//                Object object = requestData.getBodyMap().get(key);
//                String value;
//                if(object instanceof String){
//                    value = Tools.string().nullStrToEmpty(object);
//                    builder.add(key, value);
//                }else{
//                    value = GsonUtil.gson.toJson(object);
//                    builder.add(key, value);
//                }
//
//            }
//        }
//        return builder.build();

    }


    /**
     * post的请求参数，构造RequestBody
     * @param requestData
     * @return
     */
    private static RequestBody createUploadFileRequestBody(BaseRequestData requestData){
        MultipartBody.Builder builder=new MultipartBody.Builder();
        if(requestData.getBodyMap() != null){
            Iterator<String> iterator = requestData.getBodyMap().keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();

                Object object = requestData.getBodyMap().get(key);
                String value;
                if(object instanceof String){
                    value = Tools.string().nullStrToEmpty(object);
                }else{
                    value = GsonUtil.gson.toJson(object);
                }
                builder.addFormDataPart(key, value);
            }
        }
        builder.setType(MultipartBody.FORM);
        if(requestData.getUploadFileDataList() != null){
            for(BaseRequestData.UploadFileData uploadFileData : requestData.getUploadFileDataList()){
                builder.addFormDataPart(uploadFileData.key,uploadFileData.fileName, RequestBody.create(MediaType.parse("image/png"),uploadFileData.file));
            }
        }
        return builder.build();

    }

    private static OkHttpClient initHttpClient(BaseRequestData requestData){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(requestData.getTimeOutMill(), TimeUnit.MILLISECONDS)
                //设置连接池的保活时间为1秒。
                .connectionPool(new ConnectionPool(5,1,TimeUnit.SECONDS))
                .readTimeout(requestData.getTimeOutMill(), TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        return okHttpClient;
    }
    private static void dealResponseData(BaseRequestData requestData,
                                                     Request request,
                                                     final Class<? extends BaseResponseData> dataModelClass,
                                                     String responseBody,
                                                     Exception netException,
                                                     final RequestCallBack callBack){

        if(requestData.isEncrypt()){
            responseBody = RequestEncrypt.encrypt(responseBody);
        }
        if(requestData.isShowLog()){
            StringBuffer loggerInfo = new StringBuffer();
            loggerInfo.append("RequestURL:" + requestData.getRequestURL() + requestData.getParamsString());
            loggerInfo.append("\n*********Header*********");
            if(request != null){
                loggerInfo.append("\nRequestMode:" + request.method());
                if(request.headers() != null ){
                    Set<String> names = request.headers().names();
                    if(names != null){
                        for(String name : names){
                            loggerInfo.append("\n" + name + ":" + request.header(name));
                        }
                    }
                }
            }


            loggerInfo.append("\nBody:" + requestData.getBodyGsonString());
            if(netException != null){
                loggerInfo.append("\n***********ResponseDataError*************");
                loggerInfo.append("\n" + netException.getMessage());
            }else{
                loggerInfo.append("\n***********ResponseData*************");
                loggerInfo.append("\n" + responseBody);
            }
            LoggerHelper.i(loggerInfo.toString());
        }
        BaseResponseData responseData;
        try {
            responseData = dataModelClass.newInstance();
        } catch (Exception e) {
            responseData = new BaseResponseData();
        }
        if(netException != null){
            responseData.setResponse_status(HTTP_RESPONSE_STATUS.NETERROR);
            responseData.setErrorMsg("网络错误" + (netException.getMessage() == null ? "" : netException.getMessage()));
        }else{
            try {
                responseData = GsonUtil.gson.fromJson(responseBody,dataModelClass);
                TreeMap<String, Object> map =
                        GsonUtil.gson.fromJson(responseBody, new TypeToken<TreeMap<String, Object>>(){}.getType());
                responseData.setResponseModelFromMap(map,responseBody);
            } catch (Exception e) {
                responseData = new BaseResponseData();
                responseData.setResponse_status(HTTP_RESPONSE_STATUS.DATAERROR);
                responseData.setJsonValueString(responseBody);
                if(responseBody != null && responseBody.contains("<html>")){
                    responseData.setErrorMsg("服务器处理异常");
                }else{
                    responseData.setErrorMsg("数据解析出错" + e.getMessage());
                }
            }

        }
        if(callBack != null){
            try {
                callBack.onResponseBackgroundThread(responseData);
            }catch (Exception ex){
                callBack.dealBusinessError(false,new Exception("数据处理异常:" + ex.getMessage()));
            }

            final BaseResponseData finalResponseData = responseData;
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        callBack.onResponseMainThread(finalResponseData);
                    }catch (Exception ex){
                        callBack.dealBusinessError(true,new Exception("数据处理异常:" + ex.getMessage()));
                    }
                }
            });
        }

        System.gc();
    }
}

