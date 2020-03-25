package com.xingyeda.ad.module.dataprovider;

import android.content.Context;

import com.xingyeda.ad.module.addata.AdItem;

import java.util.ArrayList;


public class NineADDataProvider extends BaseADDataProvider {
    private ArrayList<AdItem> currentShowADItemList = new ArrayList<>();
    private int maxADViewNum = 9;
    private int currentShowAdIndex = -1;
    public NineADDataProvider(Context context) {
        super(context);
    }

    public void setMaxADViewNum(int maxADViewNum) {
        this.maxADViewNum = maxADViewNum;
    }

    public AdItem getNextADItem(int defaultIndex,AdItem finishPlayItem) {
        synchronized (lockObject){
            if(mAdDataList == null){
                return null;
            }
            AdItem adItem = null;
            if(finishPlayItem != null){
                currentShowADItemList.remove(finishPlayItem);
            }

            //广告总数小于等于 广告位数量  直接返回对应位置的广告
            if(mAdDataList.size() <= this.maxADViewNum){
                if(defaultIndex < mAdDataList.size()){
                    adItem = mAdDataList.get(defaultIndex);
                }else{
                    adItem = null;
                }
            }else{//不管是否已经下载完成的 判断只要没有正在显示屏上面的广告 就直接显示出来
                int tempShowIndex = -1;
                for (int index = 0; index < mAdDataList.size(); index++) {
                    AdItem tempAdItem = mAdDataList.get(index);
                    if (index > currentShowAdIndex) {
                        if (!currentShowADItemList.contains(tempAdItem)) {
                            adItem = tempAdItem;
                            tempShowIndex = index;
                            break;
                        }

                    } else {
                        if (adItem == null) {
                            if (!currentShowADItemList.contains(tempAdItem)) {
                                adItem = tempAdItem;
                                tempShowIndex = index;
                            }
                        }
                    }
                }
                currentShowAdIndex = tempShowIndex;
            }
            if(adItem != null){//广告显示出去了  就加入到已经显示的队列中  其他广告位播放完就不会抓这个广告了，避免一个广告同时出现
                currentShowADItemList.add(adItem);
            }

            return adItem;
        }
    }
}
