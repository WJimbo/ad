package com.xingyeda.ad.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * 使用SharedPreferences存储数据 
 */
public class SharedPreUtil {

	private static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(SharedPreUtil.class.getSimpleName(), 0);
	}

	private static SharedPreferences.Editor getEdit(Context context) {
		return getSharedPreferences(context).edit();
	}

	//存储
	public static void put(Context context, String key, Object object) {
		SharedPreferences.Editor editor = getEdit(context);
		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else if (object instanceof String[]) {
			StringBuilder datas = new StringBuilder();
			String[] data = (String[]) object;

			for (int i = 0; i < data.length; ++i) {
				if (i != 0) {
					datas.append(":");
				}

				datas.append(data[i]);
			}
			editor.putString(key, datas.toString());
		}

		editor.apply();
	}

	//数据取出
	public static String getString(Context context, String key, String defaultObject) {
		return getSharedPreferences(context).getString(key, defaultObject);
	}
	
	public static String getString(Context context, String key) {
		return getSharedPreferences(context).getString(key, "");
	}

	public static int getInt(Context context, String key,int defValue) {
		return getSharedPreferences(context).getInt(key, defValue);
	}

	public static boolean getBoolean(Context context, String key) {
		return getSharedPreferences(context).getBoolean(key, false);
	}
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		return getSharedPreferences(context).getBoolean(key, defValue);
	}

	public static float getFloat(Context context, String key,float defValue) {
		return getSharedPreferences(context).getFloat(key, defValue);
	}

	public static long getLong(Context context, String key,long defValue) {
		return getSharedPreferences(context).getLong(key, defValue);
	}

	public static String[] getStringArray(Context context, String key) {
		return getString(context, key).split(":");
	}

	//删除某个数据
	public static void remove(Context context, String key) {
		SharedPreferences.Editor editor = getEdit(context);
		editor.remove(key);
		editor.apply();
	}

	//清除数据
	public static void clear(Context context) {
		SharedPreferences.Editor editor = getEdit(context);
		editor.clear();
		editor.apply();
	}

	//查询数据
	public static boolean contains(Context context, String key) {
		return getSharedPreferences(context).contains(key);
	}

	//获得所有数据
	public static Map<String, ?> getAll(Context context) {
		return getSharedPreferences(context).getAll();
	}
}
