package com.xingyeda.ad.module.ad.dataprovider;

import android.content.Context;

import com.xingyeda.ad.module.ad.data.ADListManager;
import com.xingyeda.ad.module.ad.data.AdItem;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.ToolUtils;

import java.util.ArrayList;
import java.util.List;

public class BaseADDataProvider implements ADListManager.OnAdListChangedListener {
    protected ArrayList<AdItem> mAdDataList = new ArrayList<AdItem>();
    protected final Object lockObject = new Object();
    protected Context mContext;
    public BaseADDataProvider(Context context){
        mContext = context.getApplicationContext();
        if(ADListManager.getInstance(mContext).getAdListResponseData() != null){
            if(ADListManager.getInstance(mContext).getAdListResponseData().getData() != null){
                mAdDataList.addAll(ADListManager.getInstance(mContext).getAdListResponseData().getData());
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
                for(AdItem adItem : adItems){
                    if(!ToolUtils.string().isEmpty(adItem.getUrl())){
                        mAdDataList.add(adItem);
                    }else{
                        MyLog.i("广告过滤异常数据：" + adItem.getId());
                    }
                }

            }
        }
    }
}
