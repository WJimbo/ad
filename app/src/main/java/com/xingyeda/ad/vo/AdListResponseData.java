package com.xingyeda.ad.vo;
import com.xingyeda.ad.util.httputil.HttpObjResponseData;

import java.util.List;

public class AdListResponseData extends HttpObjResponseData {
    private List<AdItem> obj;
//    private String status;
//    private String msg;
//    private int state;  //1 三分屏  2 全屏   3 三分之二屏

    public List<AdItem> getObj() {
        return obj;
    }

    public void setObj(List<AdItem> obj) {
        this.obj = obj;
    }

}
