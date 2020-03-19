package com.xingyeda.ad.util;

import com.xingyeda.ad.module.addata.AdItem;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class Util {

    public static int compareDate(String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date dt1 = new Date();
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }


    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static Map<Integer, AdItem> sortMapByKey(Map<Integer, AdItem> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Integer, AdItem> sortMap = new TreeMap<Integer, AdItem>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1.intValue() < o2.intValue())
                    return -1;
                else if (o1.intValue() == o2.intValue())
                    return 0;
                else
                    return 1;
            }
        });
        sortMap.putAll(map);
        return sortMap;
    }

}
