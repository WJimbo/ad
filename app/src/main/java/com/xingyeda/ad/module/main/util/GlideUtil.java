package com.xingyeda.ad.module.main.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class GlideUtil {
    public static void loadImage(Context context, final String imageUrl, final ImageView imageView) {
        Glide.with(context).load(imageUrl).animate(android.R.anim.fade_in).into(imageView);
    }

    public static void loadImage(Context context, final File file, final ImageView imageView) {
        Glide.with(context).load(file).animate(android.R.anim.slide_in_left).into(imageView);
    }
}
