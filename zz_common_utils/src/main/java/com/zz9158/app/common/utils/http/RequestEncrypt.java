package com.zz9158.app.common.utils.http;

/**
 * @author tangyongx
 * @date 2019-02-21
 */
class RequestEncrypt {
    public static String encrypt(String value)
    {
        if(value == null){
            return "";
        }
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int enchar = chars[i]^105;
            sbu.append(String.valueOf((char) enchar));
        }
        return sbu.toString();
    }
}
