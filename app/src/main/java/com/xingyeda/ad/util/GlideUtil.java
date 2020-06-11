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
    public static void loadImage(Context context, final File file, final ImageView imageView,float rotate) {
        DrawableRequestBuilder drawableRequestBuilder = Glide.with(context).load(file).crossFade();
        if(imageView != null && imageView.getDrawable() != null){
            drawableRequestBuilder.placeholder(imageView.getDrawable());
        }else{
            if(rotate == 90){
                drawableRequestBuilder.placeholder(R.drawable.drawable_ad_loading_90);
            }else if(rotate == 180){
                drawableRequestBuilder.placeholder(R.drawable.drawable_ad_loading_180);
            }else if(rotate == 270){
                drawableRequestBuilder.placeholder(R.drawable.drawable_ad_loading_270);
            }else{
                drawableRequestBuilder.placeholder(R.drawable.drawable_ad_loading);
            }
        }
        drawableRequestBuilder.into(imageView);
    }
}
