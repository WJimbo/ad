package com.zz9158.app.common.utils.http;


import com.google.gson.JsonNull;
import com.mazouri.tools.Tools;
import com.zz9158.app.common.utils.GsonUtil;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;


/**
 * Created by tangyongx on 26/11/2018.
 */

public abstract class BaseRequestData {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    public static final MediaType TEXT
            = MediaType.parse("text/plain; charset=utf-8");

    static class UploadFileData{
        String key;
        String fileName;
        File file;
    }

    public enum RequestModeType{
        GET,POST,PUT,DELETE
        //,POSTFILE
    }
    private MediaType mediaType = JSON;
    private String authorization = "";
    private RequestModeType requestMode = RequestModeType.GET;
    private String requestURL;
    private int timeOutMill = 15* 1000;
    private boolean showLog = true;
    private boolean encrypt = false;
    //请求内容
    private Map<String,Object> bodyMap = new HashMap();
    //请求参数信息
    private Map<String,Object> requestParams = new HashMap();
    private List<UploadFileData> uploadFileDataList = new ArrayList<>();
    //请求参数信息
    private Map<String,String> headerParams = new HashMap();

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public RequestModeType getRequestMode() {
        return requestMode;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setRequestMode(RequestModeType requestMode) {
        this.requestMode = requestMode;
    }

    public void addHeaderParam(String key,String value){
        headerParams.put(key,value);
    }

    public Map<String, String> getHeaderParams() {
        return headerParams;
    }

    public BaseRequestData(){
        super();
        setDefaultData();
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void addUploadFileArray(String key, String fileName, File file){
        //若服务器为php，接口文件参数名称后面追加"[]"表示数组，示例：builder.addUploadFileArray("uploadFile[]",path);
        addFile(key,fileName,file);
    }
    public void addUploadSingleFile(String key,String fileName,File file){
        addFile(key,fileName,file);
    }

    private void addFile(String key,String fileName,File file){
        if(timeOutMill < 30 * 1000){
            timeOutMill = 30 * 1000;
        }
        UploadFileData fileData = new UploadFileData();
        fileData.key = key;
        fileData.fileName = fileName;
        fileData.file = file;
        uploadFileDataList.add(fileData);
    }

    public List<UploadFileData> getUploadFileDataList() {
        return uploadFileDataList;
    }

    private void setDefaultData(){
//        if(MConfig.getInstance() != null){
//            authorization = "Bearer " +  ToolUtils.string().nullStrToEmpty(MConfig.getInstance().getAccessToken());
//        }
    }
    private int limit = -1;
    public void addLimitParam(int limit){
        this.limit = limit;
        addRequestParams("Limit",limit);
    }

    public int getLimit() {
        return limit;
    }

    public void addMaxIdParam(int maxId){
        addRequestParams("MaxId",maxId);
    }
    public void addSinceIdParam(int sinceId){
        addRequestParams("SinceId",sinceId);
    }


    public void addBody(String key,Object value){
        if(value == null){
            value = JsonNull.INSTANCE;
        }
        this.bodyMap.put(key,value);
    }
    public void addRequestParams(String key,Object value){
        this.requestParams.put(key,value);
    }
    public String getBodyGsonString(){
        return GsonUtil.gson.toJson(bodyMap);
    }
    public String getBodyParamsString(){
        StringBuffer returnStringBuffer = new StringBuffer("");
        if(this.bodyMap != null){
            for (String key : this.bodyMap.keySet()) {
                Object object = this.bodyMap.get(key);
                String value;
                if(object instanceof String){
                    value = Tools.string().nullStrToEmpty(object);
                }else{
                    value = GsonUtil.gson.toJson(object);
                }
                if(returnStringBuffer.length() == 0){
                    returnStringBuffer.append("");
                }else{
                    returnStringBuffer.append("&");
                }
                returnStringBuffer.append(key + "="+ Tools.string().nullStrToEmpty(value));
            }
        }
        return returnStringBuffer.toString();
    }
    public void setBodyMap(Map<String,Object> bodyMap){
        this.bodyMap = bodyMap;
    }

    public Map<String, Object> getBodyMap() {
        return bodyMap;
    }

    public void setParamMap(Map<String,Object> paramMap){
        this.requestParams = paramMap;
    }

    public int getTimeOutMill() {
        return timeOutMill;
    }

    public void setTimeOutMill(int timeOutMill) {
        this.timeOutMill = timeOutMill;
    }

    public String getRequestURL() {
        return requestURL;
    }


    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public String getParamsString(){
        StringBuffer returnStringBuffer = new StringBuffer("");
        if(this.requestParams != null){
            for (String key : this.requestParams.keySet()) {
                Object object = this.requestParams.get(key);
                String value;
                if(object instanceof String){
                    value = Tools.string().nullStrToEmpty(object);
                }else{
                    value = GsonUtil.gson.toJson(object);
                }
                if(returnStringBuffer.length() == 0 && !requestURL.contains("?")){
                    returnStringBuffer.append("?");
                }else{
                    returnStringBuffer.append("&");
                }
                returnStringBuffer.append(key + "=");
                try {
                    returnStringBuffer.append(URLEncoder.encode(Tools.string().nullStrToEmpty(value),"utf-8"));
                }catch (Exception ex){
                    returnStringBuffer.append(Tools.string().nullStrToEmpty(value));
                }

            }
        }
        return returnStringBuffer.toString();
    }

    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }
}
