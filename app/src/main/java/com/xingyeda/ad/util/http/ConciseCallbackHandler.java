package com.xingyeda.ad.util.http;

import org.json.JSONObject;

public abstract class ConciseCallbackHandler<T> {
	public abstract void onResponse(JSONObject response);
	public abstract void onError(Exception e, int id);
}
