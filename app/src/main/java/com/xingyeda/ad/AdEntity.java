package com.xingyeda.ad;

import java.util.Date;

import cn.ittiger.database.annotation.PrimaryKey;
import cn.ittiger.database.annotation.Table;

@Table(name="xyd_ad_info")
public class AdEntity {

    /**
     * 廣告ID
     */
    @PrimaryKey
    private int id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 本地地址
     */
    private String localUrl;

    /**
     * 下载中 1
     * 已下载 0
     * 下载失败 2
     */
    private String state;


    private String MD5;


    /**
     * 持续时间
     */
    private int duration;

    /**
     * 位置
     */
    private String location;

    /**
     * 文件类型
     */
    private String filetype;


    private String endtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
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

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}
