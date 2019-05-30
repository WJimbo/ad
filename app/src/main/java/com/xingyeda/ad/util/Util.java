package com.xingyeda.ad.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xingyeda.ad.AdEntity;
import com.xingyeda.ad.R;
import com.xingyeda.ad.vo.Ad;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Util {

    public static void loadImage(Context context, final String imageUrl, final ImageView imageView) {
        Glide.with(context).load(imageUrl).into(imageView);
    }

    public static void defaultImage(Context context, final ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        Glide.with(context).load(R.mipmap.p1).into(imageView);
    }

    public static String getAndroidId(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static void orientation()
    {
        /*Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        if (mConfiguration.orientation == mConfiguration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        }*/
    }

    public static Ad entityToPo(AdEntity entity) {
        Ad ad = new Ad();
        ad.setFileUrl(entity.getLocalUrl());
        ad.setDuration(entity.getDuration());
        ad.setFiletype(entity.getFiletype());
        return ad;
    }

    public static Map<Integer, Ad> sortMapByKey(Map<Integer, Ad> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Integer, Ad> sortMap = new TreeMap<Integer, Ad>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if( o1.intValue() < o2.intValue() )
                    return -1;
                else if(o1.intValue() == o2.intValue())
                    return 0;
                else
                    return 1;
            }
        });
        sortMap.putAll(map);
        return sortMap;
    }

}