package com.xingyeda.ad.config;

import android.content.Context;

import com.xingyeda.ad.util.SharedPreUtil;
import com.zz9158.app.common.utils.ToolUtils;

public class URLConfig {

//    public static String www = "http://120.25.245.234:8080/xydServer/servlet/";

    public static final String DOMAIN = "http://";

    //    public static final String HOST = "192.168.10.200";
    private static final String HOST = "service.xyd999.net";

    //    public static final String HOST_PATH = HOST + "xydServer/servlet/";
    private static final String IP_HOST_PATH = ":8080/xydServer/servlet/";
    private static final String DOMAIN_HOST_PATH = "/servlet/";


    //用户设置
    public static final String USERSET_PATH = "econfig";

    public static final String REQUEST_AD_LIST = "GetAdversitingByMac/R";

    public static final String CHECK_NEW_VERSIONS = "getServerVersionByAD";

    public static final String BIND_EQ_BY_MAC = "insertEqByMac/C";

    //获取地址
    public static String getPath(Context context, String url) {
        String host = getHost(context);
        String path = DOMAIN_HOST_PATH;
        if(ToolUtils.regex().isIPAddress(host)){
            path = IP_HOST_PATH;
        }
        return DOMAIN + host + path + url;
    }

    public static String getHost(Context context) {
        String host = SharedPreUtil.getString(context, "ip");
        if(ToolUtils.string().isEmpty(host)){
            host = HOST;
        }
        return host;
    }
    public static void saveHost(Context context,String host){
        SharedPreUtil.put(context,"ip",ToolUtils.string().nullStrToEmpty(host));
    }
}
