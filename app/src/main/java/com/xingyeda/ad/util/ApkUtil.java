package com.xingyeda.ad.util;

import com.zz9158.app.common.utils.ToolUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ApkUtil {

    /**
     * 返回ZIP中的文件列表（文件和文件夹）
     *
     * 读取apk中assets/product.txt中的信息
     * @param zipFileString  ZIP的名称
     * @return
     * @throws Exception
     */
    public static String findProductInfoInAPK(String zipFileString){
        String productInfo = "";
        try {
            ZipFile zipFile = new ZipFile(zipFileString);
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
            ZipEntry zipEntry;

            while ((zipEntry = inZip.getNextEntry()) != null) {
                if("assets/product.txt".equals(zipEntry.getName())){
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(zipFile.getInputStream(zipEntry)));
                    productInfo = br.readLine();
                    ToolUtils.close().closeIO(br);
                    break;
                }
            }
            inZip.close();
        }catch (Exception ex){

        }

        return productInfo;
    }
}
