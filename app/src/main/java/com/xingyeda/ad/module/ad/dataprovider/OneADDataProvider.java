package com.xingyeda.ad.module.ad.dataprovider;

import android.content.Context;
import com.xingyeda.ad.module.ad.data.AdItem;


public class OneADDataProvider extends BaseADDataProvider {
    private int currentShowAdIndex = -1;

    public OneADDataProvider(Context context) {
        super(context);
    }

    public AdItem getNextADItem() {
        synchronized (lockObject){
            if(mAdDataList == null){
                return null;
            }
            AdItem adItem = null;
            int tempShowIndex = -1;
            for (int index = 0; index < mAdDataList.size(); index++) {
                AdItem tempAdItem = mAdDataList.get(index);
                if (index > currentShowAdIndex) {
                    if (tempAdItem.isFileExsits()) {
                        adItem = tempAdItem;
                        tempShowIndex = index;
                        break;
                    }
                } else {
                    if (adItem == null) {
                        if (tempAdItem.isFileExsits()) {
                            adItem = tempAdItem;
                            tempShowIndex = index;
                        }
                    }
                }
            }
            currentShowAdIndex = tempShowIndex;
            if(adItem != null){
                try {
                    adItem = (AdItem)adItem.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            return adItem;
        }
    }
}
