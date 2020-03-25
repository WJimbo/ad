package com.xingyeda.ad.module.dataprovider;

import android.content.Context;

import com.xingyeda.ad.module.addata.AdItem;

import java.util.ArrayList;


public class NineADDataProvider extends BaseADDataProvider {
    private ArrayList<AdItem> currentShowADItemList = new ArrayList<>();
    private int maxADViewNum = 9;

    public NineADDataProvider(Context context) {
        super(context);
    }

    public void setMaxADViewNum(int maxADViewNum) {
        this.maxADViewNum = maxADViewNum;
    }

    public AdItem getNextADItem(int defaultIndex,AdItem showItem) {
        synchronized (lockObject){
            if(mAdDataList == null){
                return null;
            }
            if(showItem != null){
                currentShowADItemList.remove(showItem);
            }

            //广告总数小于等于 广告位数量  直接返回对应位置的广告
            if(mAdDataList.size() <= this.maxADViewNum){
                if(defaultIndex < mAdDataList.size()){
                    return mAdDataList.get(defaultIndex);
                }else{
                    return null;
                }
            }
            AdItem adItem = null;
            return adItem;
        }
    }
}
