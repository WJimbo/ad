package com.xingyeda.ad.util.httputil;


import com.zz9158.app.common.utils.http.BaseResponseData;
import com.zz9158.app.common.utils.http.HTTP_RESPONSE_STATUS;

import java.util.Map;

/**
 * 业务层数据基类
 * @author tangyongx
 * @date 2018-12-12
 */
public class HttpObjResponseData extends BaseResponseData {
    private int code;
    private String msg;

    @Override
    protected void initData(Map<String, Object> map) {
        super.initData(map);
        if(code == 0){
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

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
