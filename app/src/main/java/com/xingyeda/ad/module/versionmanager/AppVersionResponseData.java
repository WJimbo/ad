package com.xingyeda.ad.module.versionmanager;


import com.xingyeda.ad.util.httputil.HttpStringResponseData;

import java.util.List;
import java.util.Map;

/**
 * @author tangyongx
 * @date 2018-12-12
 */
public class AppVersionResponseData extends HttpStringResponseData {
    private AppVersionBean appVersionBean;

    public AppVersionBean getAppVersionBean() {
        return appVersionBean;
    }

    @Override
    public void setResponseModelFromMap(Map<String, Object> map, String jsonValueString) {
        super.setResponseModelFromMap(map, jsonValueString);
        appVersionBean = (AppVersionBean)stringToObject(this,AppVersionBean.class);
    }

    // FIXME generate failure  field _$Files168
    static class AppVersionBean{

        /**
         * ServerURL  :
         *  files  : [{"fileName":"app-signed-sdk23-android51.apk","version":"2.0"}]
         * updateTime : 2016-07-07 12:13:20
         * versionNumber : 4
         */

        private String ServerURL;
        private String updateTime;
        private int versionNumber;
        private List<FileItem> files;

        public List<FileItem> getFiles() {
            return files;
        }

        public void setFiles(List<FileItem> files) {
            this.files = files;
        }

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

        public int getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(int versionNumber) {
            this.versionNumber = versionNumber;
        }

        static class FileItem {
            /**
             * fileName : app-signed-sdk23-android51.apk
             * version : 2.0
             */

            private String fileName;
            private String version;

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }
        }
    }
}
