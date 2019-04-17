package com.yunchuan.tingyanwu.ad.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

public class MainBusiness {

    private static ProgressDialog mProgressDialog;
    private static int progress = 0;
    private static Handler mHandler = new Handler();

    /*
    获取设备SN码
     */
//    public static void getSN(final Context context) {
//        Map map = new HashMap();
//        map.put("mac", getMacAddress(context));
//        map.put("version", AppUtils.getVersionName(context));
//        LogUtils.d(ConnectPath.getPath(context, ConnectPath.ADDSHEBEIFORAPP) + map);
//        HttpUtils.doPost(ConnectPath.getPath(context, ConnectPath.ADDSHEBEIFORAPP), map, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                SNCode snCode = JsonUtils.getGson().fromJson(response.body().string(), SNCode.class);
//                SharedPreferencesUtils preferencesUtils = new SharedPreferencesUtils(context);
//                preferencesUtils.put("sncode", snCode.getObj());
//            }
//        });
//    }

    /*
    *获取mac地址
    */
    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String mac =  wifiInfo.getMacAddress().replaceAll(":", "");
        SharedPreUtil.put(context, "Mac", mac);

        return mac;
    }


}
