package com.xingyeda.ad.module.addata;



import com.zz9158.app.common.utils.ToolUtils;

import java.io.File;

/**
 * Created by tingyanwu on 2017/10/29.
 */

public class AdItem {


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

    private int id;
    private String starttime;
    private String endtime;
    private String state;
    private int duration;
    private String location;
    private String bindtype;
    private String flagid;
    private String filetype;
    private String fileUrl;
    private String MD5;

    public String getLocationFileName(){
        if("2".equals(filetype)){
            return (VideoRotateAngle + "video_") + fileUrl.hashCode() + ".mp4";
        }else if("0".equals(filetype)){
            return fileUrl.hashCode() + ".jpg";
        }else if("1".equals(filetype)){
            return fileUrl.hashCode() + ".mp3";
        }
        return fileUrl.hashCode() + ".unknow";
    }

    public boolean isFileExsits(String rootPath){
        return ToolUtils.file().isFileExists(new File(rootPath,getLocationFileName()));
    }
    public File locationFile(String rootPath){
        return new File(rootPath,getLocationFileName());
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBindtype() {
        return bindtype;
    }

    public void setBindtype(String bindtype) {
        this.bindtype = bindtype;
    }

    public String getFlagid() {
        return flagid;
    }

    public void setFlagid(String flagid) {
        this.flagid = flagid;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}


