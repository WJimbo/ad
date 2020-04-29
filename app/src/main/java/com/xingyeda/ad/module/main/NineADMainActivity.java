package com.xingyeda.ad.module.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.xingyeda.ad.R;
import com.xingyeda.ad.module.ad.data.AdItem;
import com.xingyeda.ad.module.ad.dataprovider.NineADDataProvider;
import com.xingyeda.ad.module.ad.widget.ADView;
import com.xingyeda.ad.util.MyLog;

import butterknife.BindView;

public class NineADMainActivity extends BaseADActivity {

    @BindView(R.id.adView1)
    ADView adView1;
    @BindView(R.id.adView2)
    ADView adView2;
    @BindView(R.id.adView3)
    ADView adView3;
    @BindView(R.id.adView4)
    ADView adView4;
    @BindView(R.id.adView5)
    ADView adView5;
    @BindView(R.id.adView6)
    ADView adView6;
    @BindView(R.id.adView7)
    ADView adView7;
    @BindView(R.id.adView8)
    ADView adView8;
    @BindView(R.id.adView9)
    ADView adView9;
    private NineADDataProvider dataProvider;
    private ADView[] adViewList;
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, NineADMainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main_nine_ad;
    }

    @Override
    protected void initView(Bundle saveInstanceState,float screenAngle) {
        dataProvider = new NineADDataProvider(this);
        dataProvider.registerDataProvider();
        dataProvider.setMaxADViewNum(9);
        if(screenAngle == 90){
            adViewList = new ADView[]{adView3,adView6,adView9,adView2,adView5,adView8,adView1,adView4,adView7};
        }else if(screenAngle == 180){
            adViewList = new ADView[]{adView9,adView8,adView7,adView6,adView5,adView4,adView3,adView2,adView1};
        }else if(screenAngle == 270){
            adViewList = new ADView[]{adView7,adView4,adView1,adView8,adView5,adView2,adView9,adView6,adView3};
        }else{
            adViewList = new ADView[]{adView1,adView2,adView3,adView4,adView5,adView6,adView7,adView8,adView9};
        }

        for(int index = 0;index < adViewList.length;index++){
            final int tempIndex = index;
            //9广告位视频播放静音
            adViewList[index].setVideoMute(true);
            adViewList[index].setDataSourceListener(new ADView.IADDataSourceListener() {
                @Override
                public AdItem getNextAD(AdItem finishPlayItem) {
                    return dataProvider.getNextADItem(tempIndex,finishPlayItem);
                }
            });
            adViewList[index].setTvCountSecondSize(15,15);
            adViewList[index].setTvCountSecondTextSize(6);
            adViewList[index].setAutoFadeInWhenNoAD(true);
        }
    }

    @Override
    protected void rotationADViews(float rotateAngle) {
        for(int index = 0;index < adViewList.length;index++){
            setADViewRotation(adViewList[index],rotateAngle);
        }
    }

    private void setADViewRotation(ADView adView,float rotateAngle){
        if (rotateAngle == 90) {
            adView.setDefaultImage(R.mipmap.bg_defualt_landscape_90);
            adView.setRotation(90f);
        } else if (rotateAngle == 270) {
            adView.setDefaultImage(R.mipmap.bg_defualt_landscape_270);
            adView.setRotation(270f);
        } else if (rotateAngle == 180) {
            adView.setRotation(180f);
            adView.setDefaultImage(R.mipmap.bg_defualt_portrait_180);
        } else {
            adView.setRotation(0f);
            adView.setDefaultImage(R.mipmap.bg_defualt_portrait);
        }
    }

    @Override
    public void onConnectionChanged(boolean isConnecting) {
        if(adViewList != null){
            for(int index = 0;index < adViewList.length;index++){
                adViewList[index].setCountDownTitleColor(isConnecting ? Color.WHITE : Color.RED);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i(this.toString() +" onResume");
        if(adViewList != null){
            for(int index = 0;index < adViewList.length;index++){
                adViewList[index].resumeAD();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i(this.toString() +" onPause");
        if(adViewList != null){
            for(int index = 0;index < adViewList.length;index++){
                adViewList[index].pauseAD();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i(this.toString() +" onDestroy");
        if(adViewList != null){
            for(int index = 0;index < adViewList.length;index++){
                adViewList[index].onDestroy();
            }
        }
        adViewList = null;
        if(dataProvider != null){
            dataProvider.unRegisterDataProvider();
        }

    }

}
