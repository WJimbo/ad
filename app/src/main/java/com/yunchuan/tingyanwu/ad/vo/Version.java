package com.yunchuan.tingyanwu.ad.vo;

import java.util.List;

/**
 * Created by tingyanwu on 2017/10/29.
 */

public class Version {


    /**
     * ServerURL : http://service.xyd999.com/download/
     * files : [{"fileName":"20180116.apk","version":"2.0"}]
     * updateTime : 2016-07-07 12:13:20
     * versionNumber : 2
     */

    private String ServerURL;
    private String updateTime;
    private String versionNumber;
    private List<FileBean> files;

    public String getServerURL() {
        return ServerURL;
    }

    public void setServerURL(String ServerURL) {
        this.ServerURL = ServerURL;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public List<FileBean> getFiles() {
        return files;
    }

    public void setFiles(List<FileBean> files) {
        this.files = files;
    }


}






