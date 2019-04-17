package com.yunchuan.tingyanwu.ad.vo;

/**
 * Created by tingyanwu on 2017/10/29.
 * post操作之后的返回值
 */

public class UploadResult {
    private String result;
    private String name;
    private String flag;
    private String rid;
    private String tips;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
