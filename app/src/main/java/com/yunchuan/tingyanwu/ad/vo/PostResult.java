package com.yunchuan.tingyanwu.ad.vo;

/**
 * Created by tingyanwu on 2017/10/29.
 * post操作之后的返回值
 */

public class PostResult {
    private String result;
    private String errCode;
    private String errMsg;
    private String memo;
    private String id;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
