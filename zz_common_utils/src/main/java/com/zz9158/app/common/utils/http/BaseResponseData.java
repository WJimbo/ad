package com.zz9158.app.common.utils.http;

import java.util.Map;

/**
 * Created by tangyongx on 26/11/2018.
 */

public class BaseResponseData {
    private int response_status;//请求状态
    private String errorMsg;
    private Map<String, Object> returnMap;
    private String jsonValueString = "";


    void setResponseModelFromMap(Map<String, Object> map,String jsonValueString) {
        try {
            this.returnMap = map;
            this.jsonValueString = jsonValueString;
            initData(map);
        }catch (Exception ex){
            this.response_status = HTTP_RESPONSE_STATUS.OPERATIONERROR;
            this.setErrorMsg("数据处理异常");
        }
    }

    protected void initData(Map<String,Object> map){

    }
    public boolean isOperationSuccess(){
        return response_status == HTTP_RESPONSE_STATUS.OK;
    }
    public int getResponse_status() {
        return response_status;
    }

    public void setResponse_status(int response_status) {
        this.response_status = response_status;
    }


    public Map<String, Object> getReturnMap() {
        return returnMap;
    }

    public void setReturnMap(Map<String, Object> returnMap) {
        this.returnMap = returnMap;
    }

    public String getJsonValueString() {
        return jsonValueString;
    }

    public void setJsonValueString(String jsonValueString) {
        this.jsonValueString = jsonValueString;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
