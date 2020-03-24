package com.xingyeda.ad.module.dataprovider;

import android.content.Context;

import com.xingyeda.ad.module.addata.ADListManager;
import com.xingyeda.ad.module.addata.AdItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BaseADDataProvider implements ADListManager.OnAdListChangedListener {
    protected ArrayList<AdItem> mAdDataList = new ArrayList<AdItem>();
    protected final Object lockObject = new Object();
    public void registerDataProvider(Context context){
        ADListManager.getInstance(context.getApplicationContext()).addOnAdListChangedListener(this);
    }
    public void unRegisterDataProvider(Context context){
        ADListManager.getInstance(context.getApplicationContext()).removeAdListChangedListener(this);
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
