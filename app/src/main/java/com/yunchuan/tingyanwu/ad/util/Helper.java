package com.yunchuan.tingyanwu.ad.util;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {

    public static void loadImage(Context context, final String imageUrl, final ImageView imageView) {
        Glide.with(context).load(imageUrl).into(imageView);
        Log.e("helper",imageUrl);
        //Picasso.with(context).load(imageUrl).into(imageView);

    }


    public static Date getDate(String s)
    {
            SimpleDateFormat  sdf=new SimpleDateFormat();
       sdf.applyPattern("Y-m-d");
        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("helper",e.toString());
        }
        return null;
    }









}
