package com.altang.app.common.utils;

import android.content.Context;

public class ToastUtils {
    private ToastUtils() {
    }

    public static void showToast(final Context context, final CharSequence text) {
        if(context == null){
            return;
        }
        UIUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ToolUtils.toast().showToast(context.getApplicationContext(),text.toString());
                }catch (Exception ex){

                }

            }
        });

    }

    public static void showToast(final int res) {
        UIUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ToolUtils.toast().showToast(res);
                }catch (Exception ex){

                }

            }
        });
    }
}
