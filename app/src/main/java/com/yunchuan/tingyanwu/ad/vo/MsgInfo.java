package com.yunchuan.tingyanwu.ad.vo;

import java.util.List;

public class MsgInfo {
    private List<Msg> obj;
    private String status;
    private String msg;

    public List<Msg> getObj() {
        return obj;
    }

    public void setObj(List<Msg> obj) {
        this.obj = obj;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
