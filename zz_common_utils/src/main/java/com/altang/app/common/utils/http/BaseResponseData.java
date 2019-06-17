package com.altang.app.common.utils.http;

import java.util.Map;

/**
 * Created by tangyongx on 26/11/2018.
 */

public class BaseResponseData {
    private String errorMsg;
    private int response_status;//请求状态
    protected Map<String, Object> returnMap;
    protected String jsonValueString = "";

    public String getJsonValueString() {
        return jsonValueString;
    }

    public void setResponseModelFromMap(Map<String, Object> map, String jsonValueString) {
        this.returnMap = map;
        this.jsonValueString = jsonValueString;
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

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
