package com.altang.app.common.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.Button;

/**
 * Created by tangyongx on 2018/4/4.
 */

public class DialogHelper {
    /**
     * 系统弹框（一个确定按钮）
     *
     * @param context
     * @param title   标题
     * @param message 内容
     */
    public static void showOneButtonDialog(Context context, String title, String message) {
        showOneButtonDialog(context,title,message,null);
    }

    /**
     * 系统弹框（一个确定按钮）
     *
     * @param context
     * @param title   标题
     * @param message 内容
     */
    public static void showOneButtonDialog(Context context, String title, String message, final AlertDialog.OnClickListener btnClickListener) {
        showThreeButtonDialog(context,title,message,null,null,"确定",null,null,btnClickListener);
    }


    /**
     * 系统弹框（一个确定按钮）默认标题
     *
     * @param context
     * @param message 内容
     */
    public static void showOneButtonDialog(Context context, String message) {
        showOneButtonDialog(context, "提示", message);
    }


    /**
     * <请求失败时，退出or重试>
     *
     * @param context
     * @param title             标题
     * @param msg               内容
     * @param btnTitle1         按钮1
     * @param btnTitle2         按钮2
     * @param btnClickListener1
     * @param btnClickListener2
     */
    public static void showTwoButtonDialog(Context context, String title, String msg, String btnTitle1, String btnTitle2, final AlertDialog.OnClickListener btnClickListener1, final AlertDialog.OnClickListener btnClickListener2) {
        showThreeButtonDialog(context,title,msg,null,btnTitle1,btnTitle2,null,btnClickListener1,btnClickListener2);
    }

    public static void showThreeButtonDialog(Context context, String title, String msg, String btnTitle1, String btnTitle2,String btnTitle3, final AlertDialog.OnClickListener btnClickListener1, final AlertDialog.OnClickListener btnClickListener2, final AlertDialog.OnClickListener btnClickListener3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(msg);
        if(btnTitle1 != null){
            builder.setNeutralButton(btnTitle1, new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (btnClickListener1 != null) {
                        btnClickListener1.onClick(dialog, which);
                    }
                }
            });
        }
        if(btnTitle2 != null){
            builder.setNegativeButton(btnTitle2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (btnClickListener2 != null) {
                        btnClickListener2.onClick(dialog, which);
                    }
                }
            });
        }
        if(btnTitle3 != null){
            builder.setPositiveButton(btnTitle3, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                   dialog.dismiss();
                   if(btnClickListener3 != null){
                       btnClickListener3.onClick(dialog,i);
                   }
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        if(btnTitle2 != null && btnTitle3 != null){
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            if(negativeButton != null){
                negativeButton.setTextColor(Color.RED);
            }
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if(positiveButton != null){
                positiveButton.setTextColor(Color.GRAY);
            }
        }
    }
}
