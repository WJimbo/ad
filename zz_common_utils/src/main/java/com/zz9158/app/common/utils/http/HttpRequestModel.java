package com.zz9158.app.common.utils.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zz9158.app.common.utils.GsonUtil;
import com.zz9158.app.common.utils.LoggerHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tangyongx on 26/11/2018.
 */

public class HttpRequestModel {
    public interface OnLYHttpRequestResponseListener{
        void onResponse(BaseResponseData responseData);
    }
    public interface OnUploadResponseListener{
        void onFinishWith(int index, BaseResponseData responseData);
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
     */
    public static BaseResponseData synRequestData(BaseRequestData requestData,
                                                  final Class<? extends BaseResponseData> dataModelClass){
        OkHttpClient okHttpClient =  initHttpClient(requestData);
        Request request = createRequest(requestData);
        Call call = okHttpClient.newCall(request);
        BaseResponseData baseResponseData;
        try {
            Response response = call.execute();
            String responseBody = response.body().string();
            if(requestData.isShowLog()){
                LoggerHelper.i("requestURL: %s\nheader:%s\nbody:%s\nresponseBody:%s",requestData.getRequestURL() + requestData.getParamsString(),request.header("Authorization"),requestData.getBody(),responseBody);
            }
            baseResponseData = dealResponseData(dataModelClass,responseBody,null);
        } catch (Exception e) {
            if(requestData.isShowLog()){
                LoggerHelper.i("requestURL: %s\nheader:%s\nbody:%s\ndealResponseDataError:%s",requestData.getRequestURL() + requestData.getParamsString(),request.header("Authorization"),requestData.getBody(),e.getMessage());
            }
            baseResponseData = dealResponseData(dataModelClass,null,e);
        }
//        System.gc();
        return baseResponseData;
    }


    /**
     * 异步网络请求
     * @param requestData
     * @param dataModelClass
     * @param listener
     */
    public static void asynRequestData(final BaseRequestData requestData, final Class<? extends BaseResponseData> dataModelClass, final OnLYHttpRequestResponseListener listener){
        OkHttpClient okHttpClient =  initHttpClient(requestData);
        final Request request = createRequest(requestData);
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call,final IOException e) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(requestData.isShowLog()){
                            LoggerHelper.i("requestURL: %s\nheader:%s\nbody:%s\ndealResponseDataError:%s",requestData.getRequestURL() + requestData.getParamsString(),request.header("Authorization"),requestData.getBody(),e.getMessage());
                        }
                        if(listener != null){
                            listener.onResponse(dealResponseData(dataModelClass,null,e));
                        }
                    }
                });
                System.gc();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(requestData.isShowLog()) {
                                LoggerHelper.i("requestURL: %s\nheader:%s\nbody:%s\nresponseBody:%s", requestData.getRequestURL() + requestData.getParamsString(), request.header("Authorization"), requestData.getBody(), body);
                            }
                            if(listener != null){
                                listener.onResponse(dealResponseData(dataModelClass,body,null));
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                        System.gc();
                    }
                });
            }
        });
    }
    private static Request createRequest(BaseRequestData requestData){

        Request.Builder requestBuilder = null;
        if(BaseRequestData.RequestModeType.POST == requestData.getRequestMode()){
            //MediaType  设置Content-Type 标头中包含的媒体类型值
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : requestData.getBodyMap().keySet()) {
                builder.add(key, requestData.getBodyMap().get(key));
            }
            requestBuilder = new Request.Builder().url(requestData.getRequestURL() + requestData.getParamsString()).post(builder.build());
        }else if(BaseRequestData.RequestModeType.GET == requestData.getRequestMode()){
            requestBuilder = new Request.Builder().url(requestData.getRequestURL() + requestData.getParamsString()).get();
        }else if(BaseRequestData.RequestModeType.PUT == requestData.getRequestMode()){
            //MediaType  设置Content-Type 标头中包含的媒体类型值
            RequestBody requestBody = FormBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")
                    , requestData.getBody());
            requestBuilder = new Request.Builder().url(requestData.getRequestURL() + requestData.getParamsString()).put(requestBody);
        }else if(BaseRequestData.RequestModeType.DELETE == requestData.getRequestMode()){
            RequestBody requestBody = FormBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")
                    , requestData.getBody());
            requestBuilder = new Request.Builder().url(requestData.getRequestURL() + requestData.getParamsString()).delete(requestBody);
        }
        return requestBuilder.build();
    }
    private static OkHttpClient initHttpClient(BaseRequestData requestData){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(requestData.getTimeOutMill(), TimeUnit.MILLISECONDS)
                //设置连接池的保活时间为1秒。
                .connectionPool(new ConnectionPool(5,1,TimeUnit.SECONDS))
                .readTimeout(requestData.getTimeOutMill(), TimeUnit.MILLISECONDS)
                .addInterceptor(new TokenInterceptor())
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        return okHttpClient;
    }
    private static BaseResponseData dealResponseData(final Class<? extends BaseResponseData> dataModelClass, String responseBody, Exception netException){
        BaseResponseData responseData;
        try {
            responseData = dataModelClass.newInstance();
        } catch (Exception e) {
            responseData = new BaseResponseData();
        }
        if(netException != null){
            responseData.setResponse_status(HTTP_RESPONSE_STATUS.NETERROR);
            responseData.setErrorMsg("网络异常，请确认网络是否连接好");
        }else{
            try {
                responseData = GsonUtil.gson.fromJson(responseBody,dataModelClass);
                TreeMap<String, Object> map =
                        GsonUtil.gson.fromJson(responseBody, new TypeToken<TreeMap<String, Object>>(){}.getType());
                responseData.setResponseModelFromMap(map,responseBody);
            } catch (Exception e) {
                responseData = new BaseResponseData();
                responseData.setResponse_status(HTTP_RESPONSE_STATUS.DATAERROR);
                responseData.setErrorMsg("服务器异常，请联系管理员");
//                if(responseBody != null && responseBody.indexOf("<html>") != -1){
//                    responseData.setErrorMsg("站点更新中，请稍后访问。");
//                }else{
//                    responseData.setErrorMsg("数据解析出错" + e.getMessage());
//                }
            }

        }
        return responseData;
    }
}

/**
 * 全局自动刷新Token的拦截器
 * <p>
 * 作者：余天然 on 16/9/5 下午3:31
 */
class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (isTokenExpired(response)) {//根据和服务端的约定判断token过期
            //同步请求方式，获取最新的Token
            String newSession = getNewToken();
            //使用新的Token，创建新的请求
            Request newRequest = chain.request()
                    .newBuilder()
                    //.addHeader("Authorization", "Bearer " + MConfig.getInstance().getAccessToken())
                    .build();
            //重新请求
            return chain.proceed(newRequest);
        }
        return response;
    }

    /**
     * 根据Response，判断Token是否失效
     *
     * @param response
     * @return
     */
    private boolean isTokenExpired(Response response) {
//        if (response.code() == 401 && !ToolUtils.string().isEmpty(MConfig.getInstance().getAccessToken())) {
//            return true;
//        }
        return false;
    }

    /**
     * 同步请求方式，获取最新的Token
     *
     * @return
     */
    private String getNewToken() throws IOException {
        return "";
//        return TokenManager.getNewToken();
    }
}
