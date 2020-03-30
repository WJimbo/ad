package com.xingyeda.ad.util.httputil;

import com.zz9158.app.common.utils.http.BaseRequestData;

public class HttpRequestData extends BaseRequestData {
    private boolean enableToken = true;

    public boolean isEnableToken() {
        return enableToken;
    }

    public void setEnableToken(boolean enableToken) {
        this.enableToken = enableToken;
    }

    public void setToken(String token){
        addHeaderParam("Authorization",token);
    }

    public HttpRequestData(){
        super();
        setMediaType(JSON);
    }
}
