package com.xingyeda.ad.util.httputil;
import com.zz9158.app.common.utils.GsonUtil;

/**
 * 业务层数据基类
 * @author tangyongx
 * @date 2018-12-12
 */
public class HttpStringResponseData extends HttpObjResponseData {
    protected String data;

    public static Object stringToObject(HttpStringResponseData responseData, Class cls){
        if(responseData.isOperationSuccess()){
            try {
                return GsonUtil.gson.fromJson(responseData.getData(),cls);
            }catch (Exception ex){

            }

        }
        return null;
    }

    public String getData() {
        return data;
    }
}
