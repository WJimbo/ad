package com.xingyeda.ad.module.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.xingyeda.ad.R;
import com.xingyeda.ad.module.main.widget.ADView;

import butterknife.BindView;

public class NineADMainActivity extends BaseADActivity {
    @BindView(R.id.adView)
    ADView adView;
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, NineADMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main_nine_ad;
    }
    @Override
    protected void initView(Bundle saveInstanceState) {

    }
    @Override
    protected void rotationADViews(float rotateAngle) {
        if(rotateAngle == 90){
            adView.setDefaultImage(R.mipmap.bg_defualt_landscape_90);
            adView.setRotation(90f);
        }else if(rotateAngle == 270){
            adView.setDefaultImage(R.mipmap.bg_defualt_landscape_270);
            adView.setRotation(270f);
        }else if(rotateAngle == 180){
            adView.setRotation(180f);
            adView.setDefaultImage(R.mipmap.bg_defualt_portrait_180);
        }else{
            adView.setRotation(0f);
            adView.setDefaultImage(R.mipmap.bg_defualt_portrait);
        }
    }
    @Override
    public void onConnectionChanged(boolean isConnecting) {
        if(adView != null){
            adView.setCountDownTitleColor(isConnecting ? Color.WHITE : Color.RED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resumeAD();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adView.pauseAD();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adView.onDestroy();
    }
}
