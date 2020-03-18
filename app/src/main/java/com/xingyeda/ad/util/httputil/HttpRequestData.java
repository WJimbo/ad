package com.xingyeda.ad.util.httputil;

import com.xingyeda.lowermachine.business.modules.setting.SettingConfig;
import com.zz9158.app.common.utils.http.BaseRequestData;

public class HttpRequestData extends BaseRequestData {
    public HttpRequestData(){
        super();
        addRequestParams("test", SettingConfig.isTestMode() ? "1" : "0");
    }
    public HttpRequestData(String url) {
        super(url);
        addRequestParams("test", SettingConfig.isTestMode() ? "1" : "0");
    }
}
