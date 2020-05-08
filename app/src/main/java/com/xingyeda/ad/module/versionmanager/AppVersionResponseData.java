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

        private String createAt;
        private int createBy;
        private boolean deleted;
        private String devicesVersion;
        private String downloadUrl;
        private String id;
        private int isMaster;
        private String md5;
        private int types;
        private String updateAt;
        private int updateBy;
        private int version;

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public int getCreateBy() {
            return createBy;
        }

        public void setCreateBy(int createBy) {
            this.createBy = createBy;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
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

        public int getIsMaster() {
            return isMaster;
        }

        public void setIsMaster(int isMaster) {
            this.isMaster = isMaster;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public int getTypes() {
            return types;
        }

        public void setTypes(int types) {
            this.types = types;
        }

        public String getUpdateAt() {
            return updateAt;
        }

        public void setUpdateAt(String updateAt) {
            this.updateAt = updateAt;
        }

        public int getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(int updateBy) {
            this.updateBy = updateBy;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
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
