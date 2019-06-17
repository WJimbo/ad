package com.xingyeda.ad.util.httputil;

import com.altang.app.common.utils.GsonUtil;
import com.altang.app.common.utils.http.BaseResponseData;
import com.altang.app.common.utils.http.HTTP_RESPONSE_STATUS;

import java.util.Map;

/**
 * 业务层数据基类
 * @author tangyongx
 * @date 2018-12-12
 */
public class HttpStringResponseData extends BaseResponseData {
    private int state;
    private String status;
    private String msg;
    protected String obj;

    @Override
    public void setResponseModelFromMap(Map<String, Object> map,String jsonValueString) {
        super.setResponseModelFromMap(map,jsonValueString);
        if("200".equals(status)){
            this.setResponse_status(HTTP_RESPONSE_STATUS.OK);
        }else{
            if(msg != null && !msg.isEmpty()){
                this.setResponse_status(HTTP_RESPONSE_STATUS.OPERATIONERROR);
                setErrorMsg(msg);
            }else{
                setResponse_status(HTTP_RESPONSE_STATUS.DATAERROR);
                setErrorMsg("数据错误");
            }
        }
    }
    public static Object stringToObject(HttpStringResponseData responseData, Class cls){
        if(responseData.isOperationSuccess()){
            try {
                return GsonUtil.gson.fromJson(responseData.getObj(),cls);
            }catch (Exception ex){

            }

        }
        return null;
    }
    public int getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }

    public String getStatus() {
        return status;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }
}
