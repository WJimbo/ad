package com.zz9158.app.common.utils.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.ResultReceiver;

import com.zz9158.app.common.utils.UIUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class WifiHelper {
    public static boolean setWifiApEnable(Context context, boolean enabled,String  ssid,String preSharedKey){
        if(enabled){//如果是打开热点操作，先关闭WIFI功能
            setWifiEnable(context,false);
        }

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.N){
            //获取wifi管理服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            try {
                //热点的配置类
                WifiConfiguration apConfig = new WifiConfiguration();
                apConfig.hiddenSSID = false;
                apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//                apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                apConfig.status = WifiConfiguration.Status.ENABLED;


                //配置热点的名称(可以在名字后面加点随机数什么的)
                apConfig.SSID = ssid;
                //配置热点的密码
                apConfig.preSharedKey = preSharedKey;
                //通过反射调用设置热点
                Method method = wifiManager.getClass().getMethod(
                        "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
                //返回热点打开状态
                return (Boolean) method.invoke(wifiManager, apConfig, enabled);
            } catch (Exception e) {
                return false;
            }
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mWifiManager != null) {
                int wifiState = mWifiManager.getWifiState();
                boolean isWifiEnabled = ((wifiState == WifiManager.WIFI_STATE_ENABLED) || (wifiState == WifiManager.WIFI_STATE_ENABLING));
                if (isWifiEnabled)
                    mWifiManager.setWifiEnabled(false);
            }
            if (mConnectivityManager != null) {
                try {
                    Field internalConnectivityManagerField = ConnectivityManager.class.getDeclaredField("mService");
                    internalConnectivityManagerField.setAccessible(true);
                    WifiConfiguration apConfig = new WifiConfiguration();
                    apConfig.SSID = ssid;
                    apConfig.preSharedKey = preSharedKey;

                    StringBuffer sb = new StringBuffer();
                    Class internalConnectivityManagerClass = Class.forName("android.net.IConnectivityManager");
                    ResultReceiver dummyResultReceiver = new ResultReceiver(null);
                    try {
                        Method mMethod = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
                        mMethod.invoke(mWifiManager, apConfig);
                        Method startTetheringMethod = internalConnectivityManagerClass.getDeclaredMethod("startTethering",
                                int.class,
                                ResultReceiver.class,
                                boolean.class);

                        startTetheringMethod.invoke(internalConnectivityManagerClass,
                                0,
                                dummyResultReceiver,
                                true);
                        return true;
                    } catch (NoSuchMethodException e) {
                        Method startTetheringMethod = internalConnectivityManagerClass.getDeclaredMethod("startTethering",
                                int.class,
                                ResultReceiver.class,
                                boolean.class,
                                String.class);

                        startTetheringMethod.invoke(internalConnectivityManagerClass,
                                0,
                                dummyResultReceiver,
                                false,
                                context.getPackageName());
                    } catch (InvocationTargetException e) {
                        sb.append(11 + (e.getMessage()));
                        e.printStackTrace();
                    } finally {

                    }


                } catch (Exception e) {
                    return false;
                }
            }
        }else{

        }
        return false;
    }
    public interface OpenAndConfigWifiCallback{
        void operationResult(boolean success);
    }
    public static void openAndConfigWifi(final Context context, final boolean enabled, final String ssid, final String password, final int type, final OpenAndConfigWifiCallback openAndConfigWifiCallback){
        WifiHelper.setWifiApEnable(context,false,"","");
        setWifiEnable(context,true);
        UIUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                WifiConfiguration configuration = WifiHelper.configWifiInfo(context,ssid,password,type);
                int netId = configuration.networkId;
                WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                mWifiManager.setWifiEnabled(true);
                if (netId == -1) {
                    netId = mWifiManager.addNetwork(configuration);
                }
                boolean result = mWifiManager.enableNetwork(netId, enabled);
                if(openAndConfigWifiCallback != null){
                    openAndConfigWifiCallback.operationResult(result);
                }
            }
        },5000);

    }
    public static boolean setWifiEnable(Context context, boolean enabled){
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return mWifiManager.setWifiEnabled(enabled);
    }

    private static WifiConfiguration configWifiInfo(Context context, String SSID, String password, int type) {
        WifiConfiguration config = null;
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null) {
            int wifiState = mWifiManager.getWifiState();
            boolean isWifiEnabled = ((wifiState == WifiManager.WIFI_STATE_ENABLED) || (wifiState == WifiManager.WIFI_STATE_ENABLING));
            if (!isWifiEnabled)
                mWifiManager.setWifiEnabled(true);
        }
        if (mWifiManager != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            if(existingConfigs != null){
                for (WifiConfiguration existingConfig : existingConfigs) {
                    if (existingConfig == null) continue;
                    if (existingConfig.SSID.equals("\"" + SSID + "\"")  /*&&  existingConfig.preSharedKey.equals("\""  +  password  +  "\"")*/) {
                        config = existingConfig;
                        break;
                    }
                }
            }

        }
        if (config == null) {
            config = new WifiConfiguration();
        }
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // 分为三种情况：0没有密码1用wep加密2用wpa加密
        if (type == 0) {// WIFICIPHER_NOPASSwifiCong.hiddenSSID = false;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == 1) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == 2) {   // WIFICIPHER_WPA
//            config.preSharedKey = "\"" + password + "\"";
//            config.hiddenSSID = true;
//            config.allowedAuthAlgorithms
//                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//            config.allowedPairwiseCiphers
//                    .set(WifiConfiguration.PairwiseCipher.TKIP);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            config.allowedPairwiseCiphers
//                    .set(WifiConfiguration.PairwiseCipher.CCMP);
//            config.status = WifiConfiguration.Status.CURRENT;


            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }else if(type == 4){
            config.preSharedKey = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     *获取热点的加密类型
     */
    private int getType(ScanResult scanResult){
        int type;
        if (scanResult.capabilities.contains("WPA"))
            type = 2;
        else if (scanResult.capabilities.contains("WEP"))
            type = 1;
        else
            type = 0;
        return type;
    }


    /**
     * 检查wifi是否处开连接状态
     *
     * @return
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }
}
