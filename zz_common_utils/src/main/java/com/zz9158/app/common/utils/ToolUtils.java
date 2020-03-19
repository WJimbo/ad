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
    /*
获取版本号
*/
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
