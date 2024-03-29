package com.xingyeda.ad.module.ad.data;



import com.zz9158.app.common.utils.ToolUtils;

import java.io.File;

/**
 * Created by tingyanwu on 2017/10/29.
 */

public class AdItem implements Cloneable{


//              "id": 84,   //广告的id
//            "starttime": "Dec 14, 2017 12:00:00 AM", //广告的开始时间
//            "endtime": "Aug 11, 2018 12:00:00 AM",   //广告的结束时间
//            "state": 1,                //广告的状态
//            "duration": 240,         //广告的持续时间
//            "location": "1",       //广告位于屏幕的位置，1代表屏幕上部，2代表屏幕的中部，3代表屏幕的底部  4全屏  5上1下2
//            "bindtype": "1",       //表示广告的绑定类型，0代表绑定类型为区域，1代表绑定类型为设备
//            "flagid": 11       //代表绑定类型的标识，广告绑定类型为区域时，标识值为市，广告绑定类型为设备时，标识值为设备的id
//            "flietype" : 1	   //0: 图片 1 ：音频 2：视频
    public static float VideoRotateAngle = 0;
    public static String DownloadRootPath = "";

    /**
     * createDate :
     * id : 0
     * name :
     * type : 0
     * url :
     */


    private String createDate;
    private String id;
    private String name;
    private int type;
    private String url;
    private int duration;

    private String stateTime;
    private String endTime;
    private String advId;

    @Override
    public Object clone() throws CloneNotSupportedException {
        AdItem adItem = (AdItem)super.clone();
        adItem.advId = advId;
        adItem.createDate = createDate;
        adItem.duration = duration;
        adItem.endTime = endTime;
        adItem.id = id;
        adItem.name = name;
        adItem.type = type;
        adItem.url = url;
        return adItem;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AdItem){
            AdItem adItem = (AdItem)obj;
            if(adItem.url.equals(url)
                    && adItem.type == type
                    && adItem.duration == duration
                    && adItem.id.equals(id)){
                return true;
            }
        }
        return super.equals(obj);
    }

    public String getLocationFileName(){
        if(2 == type){
            return ("video_" + VideoRotateAngle + "_") + id + "_" + url.hashCode() + ".mp4";
        }else if(0 == type){
            return ("pic_" + VideoRotateAngle + "_") + id + "_" + url.hashCode() + ".jpg";
        }else if(1 == type){
            return id + "_" + url.hashCode() + ".mp3";
        }
        return id + "_" + url.hashCode() + ".unknow";
    }

    public boolean isFileExsits(){
        return ToolUtils.file().isFileExists(locationFile());
    }
    public File locationFile(){
        return new File(DownloadRootPath,getLocationFileName());
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStateTime() {
        return stateTime;
    }

    public void setStateTime(String stateTime) {
        this.stateTime = stateTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAdvId() {
        return advId;
    }

    public void setAdvId(String advId) {
        this.advId = advId;
    }
}


