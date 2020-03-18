package com.zz9158.app.common.utils;
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
}
