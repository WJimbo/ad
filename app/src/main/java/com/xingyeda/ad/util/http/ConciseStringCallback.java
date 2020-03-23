package com.xingyeda.ad.util.http;

import android.content.Context;

import com.ldl.okhttp.callback.StringCallback;
import com.zz9158.app.common.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;


public class ConciseStringCallback extends StringCallback {

    private ConciseCallbackHandler<String> mCallbackHandler;
    private Context mContext;


    public ConciseStringCallback(Context context, ConciseCallbackHandler<String> handler) {
		mContext = context;
		mCallbackHandler = handler;
    }
    @Override
    public void onError(Call call, Exception e,int id) {
//        ToastUtils.showToast(mContext, "连接超时");
		mCallbackHandler.onError(e,id);
    }
    @Override
    public void onResponse(String response,int id) {
		if (response!=null) {

			try {
				JSONObject jobj= new JSONObject(response);
				if (!jobj.get("status").equals("200")) {
					if (jobj.has("msg")) {
						ToastUtils.showToast(mContext, jobj.getString("msg"));
					}
					return;
				}

				mCallbackHandler.onResponse(jobj);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
    }
}
