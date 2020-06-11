package com.xingyeda.ad.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.xingyeda.ad.R;

import java.io.File;

public class GlideUtil {
    public static void loadImage(Context context, final String imageUrl, final ImageView imageView, RotateTransformation rotateTransformation) {
        Glide.with(context).load(imageUrl).animate(android.R.anim.fade_in).transform(rotateTransformation).into(imageView);
    }
    public static void loadImage(Context context, final File file, final ImageView imageView) {
        loadImage(context,file,imageView,null);
    }
    public static void loadImage(Context context, final File file, final ImageView imageView, RotateTransformation rotateTransformation) {
        DrawableRequestBuilder drawableRequestBuilder = Glide.with(context).load(file).crossFade();//.transform(rotateTransformation);
        if(rotateTransformation != null){
            drawableRequestBuilder.transform(rotateTransformation);
        }
        if(imageView != null && imageView.getDrawable() != null){
            drawableRequestBuilder.placeholder(imageView.getDrawable());
        }else{
            if(rotateTransformation.getRotateRotationAngle() == 0){
                drawableRequestBuilder.placeholder(R.drawable.drawable_ad_loading);
            }else if(rotateTransformation.getRotateRotationAngle() == 90){
                drawableRequestBuilder.placeholder(R.drawable.drawable_ad_loading_90);
            }else if(rotateTransformation.getRotateRotationAngle() == 180){
                drawableRequestBuilder.placeholder(R.drawable.drawable_ad_loading_180);
            }else if(rotateTransformation.getRotateRotationAngle() == 270){
                drawableRequestBuilder.placeholder(R.drawable.drawable_ad_loading_270);
            }
        }
        drawableRequestBuilder.into(imageView);
    }
}
