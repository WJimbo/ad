package com.zz9158.app.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.mazouri.tools.Tools;

import java.io.File;

/**
 *
 * @author tangyongx
 * @date 26/11/2018
 */

public class ToolUtils extends Tools {
    public static String readFile2String(String path,String charsetName){
        String content = "";
        try{
            content = ToolUtils.file().readFile2String(path,charsetName);
        }catch (Exception ex){

        }
        return content;
    }
    public static String readFile2String(File file, String charsetName){
        String content = "";
        try{
            content = ToolUtils.file().readFile2String(file,charsetName);
        }catch (Exception ex){

        }
        return content;
    }
    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
