package com.xingyeda.ad.module.versionmanager;


import com.xingyeda.ad.util.httputil.HttpObjResponseData;

import java.util.Map;

/**
 * @author tangyongx
 * @date 2018-12-12
 */
public class AppVersionResponseData extends HttpObjResponseData {
    private AppVersionBean data;

    public AppVersionBean getData() {
        return data;
    }

    public void setData(AppVersionBean data) {
        this.data = data;
    }


    @Override
    protected void initData(Map<String, Object> map) {
        super.initData(map);
    }

    // FIXME generate failure  field _$Files168
    static class AppVersionBean{

        /**
         * createAt :
         * createBy : 0
         * deleted : true
         * devicesVersion :
         * downloadUrl :
         * id : 0
         * isMaster : 0
         * md5 :
         * types : 0
         * updateAt :
         * updateBy : 0
         * version : 0
         */
        private String apkValidation;//编译版本
        private String devicesVersion;
        private String downloadUrl;
        private String id;
        private String md5;



        public String getApkValidation() {
            return apkValidation;
        }

        public void setApkValidation(String apkValidation) {
            this.apkValidation = apkValidation;
        }

        public String getDevicesVersion() {
            return devicesVersion;
        }

        public void setDevicesVersion(String devicesVersion) {
            this.devicesVersion = devicesVersion;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
}
