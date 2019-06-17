package com.altang.app.common.utils.http;

import com.mazouri.tools.Tools;
import com.altang.app.common.utils.GsonUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by tangyongx on 26/11/2018.
 */

public class HttpRequestData {
    public enum RequestModeType{
        GET,POST,PUT,DELETE;
    }
    private RequestModeType requestMode = RequestModeType.POST;
    private String requestURL;
    private int timeOutMill = 15* 1000;
    private boolean showLog = false;


    //请求内容
    private Map<String,String> bodyMap = new HashMap();
    //请求参数信息
    private Map<String,Object> requestParams = new HashMap();

    public RequestModeType getRequestMode() {
        return requestMode;
    }


    public void setRequestMode(RequestModeType requestMode) {
        this.requestMode = requestMode;
    }

    public HttpRequestData(String _requestURL){
        super();
        this.requestURL = _requestURL;
        setDefaultData();
    }
    public HttpRequestData(){
        super();
        setDefaultData();
    }
    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    private void setDefaultData(){
//        if(MConfig.getInstance() != null){
//            authorization = "Bearer " +  ToolUtils.string().nullStrToEmpty(MConfig.getInstance().getAccessToken());
//        }
    }

    public void addBody(String key,String value){
        if(value == null){
            value = "";
        }
        this.bodyMap.put(key,value);
    }
    public void addRequestParams(String key,Object value){
        this.requestParams.put(key,value);
    }
    public String getBody(){
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

                }else{
                    returnStringBuffer.append("&");
                }
                returnStringBuffer.append(key + "="+ Tools.string().nullStrToEmpty(value));
            }
        }
        return returnStringBuffer.toString();
    }

    public void setBodyMap(Map<String,String> bodyMap){
        this.bodyMap = bodyMap;
    }

    public Map<String, String> getBodyMap() {
        return bodyMap;
    }

    public void setParamMap(Map<String,Object> paramMap){
        this.requestParams = paramMap;
    }

    public int getTimeOutMill() {
        return timeOutMill;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
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
                if(returnStringBuffer.length() == 0){
                    returnStringBuffer.append("?");
                }else{
                    returnStringBuffer.append("&");
                }
                returnStringBuffer.append(key + "="+ Tools.string().nullStrToEmpty(value));
            }
        }
        return returnStringBuffer.toString();
    }
}
