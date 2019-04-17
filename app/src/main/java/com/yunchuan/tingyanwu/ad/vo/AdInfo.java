package com.yunchuan.tingyanwu.ad.vo;

import java.util.List;

public class AdInfo {
    private List<Ad> obj;
    private String status;
    private String msg;
    private String state;  //1 三分屏  2 全屏   3 三分之二屏


    public List<Ad> getObj() {
        return obj;
    }

    public void setObj(List<Ad> obj) {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
