package com.xingyeda.ad.module.dataprovider;

import android.content.Context;

import com.xingyeda.ad.module.addata.ADListManager;
import com.xingyeda.ad.module.addata.AdItem;

import java.util.ArrayList;
import java.util.List;

public class BaseADDataProvider implements ADListManager.OnAdListChangedListener {
    protected ArrayList<AdItem> mAdDataList = new ArrayList<AdItem>();
    protected final Object lockObject = new Object();
    protected Context mContext;
    public BaseADDataProvider(Context context){
        mContext = context.getApplicationContext();
        if(ADListManager.getInstance(mContext).getAdListResponseData() != null){
            if(ADListManager.getInstance(mContext).getAdListResponseData().getObj() != null){
                mAdDataList.addAll(ADListManager.getInstance(mContext).getAdListResponseData().getObj());
            }
        }
    }
    public void registerDataProvider(){
        ADListManager.getInstance(mContext).addOnAdListChangedListener(this);
    }
    public void unRegisterDataProvider(){
        ADListManager.getInstance(mContext).removeAdListChangedListener(this);
    }

    @Override
    public void adListChanged(List<AdItem> adItems) {
        synchronized (lockObject){
            mAdDataList.clear();
            if(adItems != null){
                mAdDataList.addAll(adItems);
            }
        }
    }
}
