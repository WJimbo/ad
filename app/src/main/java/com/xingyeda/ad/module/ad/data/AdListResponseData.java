package com.xingyeda.ad.module.ad.data;
import com.xingyeda.ad.util.httputil.HttpObjResponseData;

import java.util.ArrayList;
import java.util.List;

public class AdListResponseData extends HttpObjResponseData {
    private List<AdItem> data;
//    private String status;
//    private String msg;
//    private int state;  //1 三分屏  2 全屏   3 三分之二屏


    public List<AdItem> getData() {
        if(data == null){
            return new ArrayList<>();
        }
        return data;
    }

}
