package com.xingyeda.ad.module.dataprovider;

import android.content.Context;
import com.xingyeda.ad.module.addata.AdItem;
import com.xingyeda.ad.module.addata.DownloadManager;


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
                    if (tempAdItem.isFileExsits(DownloadManager.getDownloadRootPath(mContext))) {
                        adItem = tempAdItem;
                        tempShowIndex = index;
                        break;
                    }
                } else {
                    if (adItem == null) {
                        if (tempAdItem.isFileExsits(DownloadManager.getDownloadRootPath(mContext))) {
                            adItem = tempAdItem;
                            tempShowIndex = index;
                        }
                    }
                }
            }
            currentShowAdIndex = tempShowIndex;
            return adItem;
        }
    }
}
