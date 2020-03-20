package com.xingyeda.ad.config;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.zz9158.app.common.utils.FileHelper;
import com.zz9158.app.common.utils.ToolUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * 设备唯一标识
 * @author tangyongx
 * @date 2018-12-12
 */
public class DeviceUUIDManager {
    private static String getDeviceID(Context context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }

        String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if(deviceId == null){
            deviceId = "";
        }
        return deviceId;
    }
    /*
     *获取mac地址
     */
    private static String getMacAddress(Context context) {
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                macSerial += line.trim();
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String mac =  macSerial.replaceAll(":", "");
        return mac;
    }
    /*产生numSize位16进制的数*/
    private static String getRandomValue(int numSize) {
        String str = "";
        for (int i = 0; i < numSize; i++) {
            char temp = 0;
            int key = (int) (Math.random() * 2);
            switch (key) {
                case 0:
                    temp = (char) (Math.random() * 10 + 48);//产生随机数字
                    break;
                case 1:
                    temp = (char) (Math.random() * 6 + 'a');//产生a-f
                    break;
                default:
                    break;
            }
            str = str + temp;
        }
        return str;
    }


    private static String uuid;
    public static void setUUID(String tempUuid){
        uuid = tempUuid;
    }
    public static final synchronized String generateUUID(Context context) {
        if(!ToolUtils.string().isEmpty(uuid)){
            return uuid;
        }
        String uuidFilePath = FileHelper.getExternalStorageRootPath() + "deviceID.db";
        if(ToolUtils.file().isFileExists(uuidFilePath)){
            uuid = ToolUtils.readFile2String(uuidFilePath,null);
        }

        if(ToolUtils.string().isEmpty(uuid)){
            ToolUtils.file().deleteFile(uuidFilePath);
            uuid = getAndroidId(context);
            if(ToolUtils.string().isEmpty(uuid)){
                String content = getMacAddress(context);
                if(ToolUtils.string().isEmpty(content) || "020000000000".equals(content)){
//                content += getDeviceID(context) + getAndroidID(context) + System.currentTimeMillis();
                    uuid = getRandomValue(12);//ToolUtils.secureMD5().getMD5Code(content);
                }else{
                    uuid = content;
                }
            }
            ToolUtils.file().createOrExistsFile(uuidFilePath);
            ToolUtils.file().writeFileFromString(uuidFilePath,uuid,false);
        }

        return uuid;
    }
    private static String getAndroidId(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }
}
