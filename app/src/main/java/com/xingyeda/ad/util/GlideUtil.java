package com.xingyeda.ad.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class GlideUtil {
    public static void loadImage(Context context, final String imageUrl, final ImageView imageView, RotateTransformation rotateTransformation) {
        Glide.with(context).load(imageUrl).animate(android.R.anim.fade_in).transform(rotateTransformation).into(imageView);
    }

    public static void loadImage(Context context, final File file, final ImageView imageView, RotateTransformation rotateTransformation) {
        Glide.with(context).load(file).animate(android.R.anim.fade_in).transform(rotateTransformation).into(imageView);
    }
}
