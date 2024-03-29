package com.xingyeda.ad.config;

import android.content.Context;

import com.xingyeda.ad.util.SharedPreUtil;
import com.zz9158.app.common.utils.ToolUtils;

public class URLConfig {

//    public static String www = "http://120.25.245.234:8080/xydServer/servlet/";

    public static final String DOMAIN = "http://";
    //    public static final String HOST = "192.168.10.200";
//    private static final String HOST = "ytyty.utools.club";//新后台测试地址
    private static final String HOST = "api.xyd999.net";
//    private static final String HOST = "service.xyd999.net";



/*    //    public static final String HOST_PATH = HOST + "xydServer/servlet/";
    private static final String IP_HOST_PATH = ":8080/xydServer/servlet/";
    private static final String DOMAIN_HOST_PATH = "/servlet/";
    //上传日志
    public static final String UPLOAD_LOG = "submitLog";
    //设备配置
    public static final String USERSET_PATH = "econfig";
    //检测更新版本
    public static final String CHECK_NEW_VERSION = "getServerVersionByAD";
    //获取广告数据
    public static final String REQUEST_AD_LIST = "GetAdversitingByMac/R";
    //提交mac地址到后台
    public static final String BIND_EQ_BY_MAC = "insertEqByMac/C";
   */

    private static final String IP_HOST_PATH = ":8080";
    private static final String DOMAIN_HOST_PATH = "";

    //为了接口安全性 增加token机制 已测试
    public static final String GET_LOGIN_USER_TOKEN = "/api/user/login";

    //上传日志  111    // FIXME: 2020-04-10
    public static final String UPLOAD_LOG = "/api/file/upload/log";

    //设备配置  1111    // FIXME: 2020-04-10
    public static final String USERSET_PATH = "/api/devices/pointSetting";
    //检测更新版本 111     // FIXME: 2020-04-10
    public static final String CHECK_NEW_VERSION = "/api/devices/firmware/check";
    //获取广告数据    // FIXME: 2020-04-10
    public static final String REQUEST_AD_LIST = "/api/advertising/reAdvertising";
    //提交mac地址到后台  已测试
    public static final String BIND_EQ_BY_MAC = "/unauthenticate/register";




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
