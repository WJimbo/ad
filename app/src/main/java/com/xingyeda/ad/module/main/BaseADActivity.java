package com.xingyeda.ad.module.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xingyeda.ad.R;
import com.xingyeda.ad.base.BaseActivity;
import com.xingyeda.ad.config.DeviceUUIDManager;
import com.xingyeda.ad.config.SettingConfig;
import com.xingyeda.ad.logdebug.LogDebugItem;
import com.xingyeda.ad.logdebug.LogDebugUtil;
import com.xingyeda.ad.module.addata.ADListManager;
import com.xingyeda.ad.module.addata.AdItem;
import com.xingyeda.ad.module.addata.AdListResponseData;
import com.xingyeda.ad.module.addata.DownloadManager;
import com.xingyeda.ad.service.socket.ConnectChangedItem;
import com.xingyeda.ad.widget.SquareHeightRelativeLayout;
import com.zz9158.app.common.utils.ToolUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseADActivity extends BaseActivity {

    private TextView tvLogDebug;
    private TextView tips;
    private SquareHeightRelativeLayout rootLayoutLogDebug;
    private SquareHeightRelativeLayout rootLayoutTips;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(getLayout());
        unbinder = ButterKnife.bind(this);
        initView(savedInstanceState);
        initTipsView();
        initDebugView();
        rotationViews(SettingConfig.getScreenRotateAngle(this));

        ADListManager.getInstance(this).setOnDataChangeCallBackListener(new ADListManager.OnDataChangeCallBackListener() {
            @Override
            public void dataChanged(AdListResponseData adListResponseData) {
                if (isFinishing()) {
                    return;
                }
                if (adListResponseData != null && adListResponseData.getObj() != null) {
                    List<AdItem> adItems = new ArrayList<>();
                    adItems.addAll(adListResponseData.getObj());
                    for (AdItem adItem : adItems) {
                        //不支持视频模式的时候 过滤掉视频文件的下载
                        if (!adItem.isFileExsits(DownloadManager.getDownloadRootPath(getApplicationContext()))) {
                            DownloadManager.DownloadItem downloadItem = new DownloadManager.DownloadItem();
                            downloadItem.url = adItem.getFileUrl();
                            downloadItem.fileType = adItem.getFiletype();
                            downloadItem.savePath = adItem.locationFile(DownloadManager.getDownloadRootPath(getApplicationContext()));
                            downloadItem.videoRotateAngle = SettingConfig.getScreenRotateAngle(getApplicationContext());
                            DownloadManager.getInstance().downloadWithUrl(downloadItem);
                        }
                    }
                }
            }
        });
        requestList();
    }
    private void requestList() {
        ADListManager.getInstance(getApplicationContext()).setNeedUpdateList();
    }
    private void rotationViews(float rotateAngle){
        //设置布局旋转角度
        if(rootLayoutLogDebug != null){
            rootLayoutLogDebug.setRotation(rotateAngle);
            RelativeLayout.LayoutParams debugLayoutParams = (RelativeLayout.LayoutParams)rootLayoutLogDebug.getLayoutParams();
            if(SettingConfig.getScreenRotateAngle(this) == 90){
                debugLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }else if(SettingConfig.getScreenRotateAngle(this) == 270){
                debugLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }else if(SettingConfig.getScreenRotateAngle(this) == 0){
                debugLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }else{
                debugLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            rootLayoutLogDebug.setLayoutParams(debugLayoutParams);
        }
        if(rootLayoutTips != null){
            rootLayoutTips.setRotation(rotateAngle);

            RelativeLayout.LayoutParams tipsLayoutParams = (RelativeLayout.LayoutParams)rootLayoutTips.getLayoutParams();
            if(SettingConfig.getScreenRotateAngle(this) == 90){
                tipsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }else if(SettingConfig.getScreenRotateAngle(this) == 270){
                tipsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }else if(SettingConfig.getScreenRotateAngle(this) == 0){
                tipsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }else{
                tipsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            rootLayoutTips.setLayoutParams(tipsLayoutParams);
        }
        rotationADViews(rotateAngle);
    }

    protected abstract  @LayoutRes int getLayout();
    protected abstract void initView(Bundle saveInstanceState);

    protected abstract void rotationADViews(float rotateAngle);

    public abstract void onConnectionChanged(boolean isConnecting);

    private void initTipsView(){
        tips = findViewById(R.id.tips);
        rootLayoutTips = findViewById(R.id.rootLayout_Tips);
        if(tips != null){
            tips.setVisibility(SettingConfig.isShowDebugView(this) ? View.VISIBLE:View.GONE);

            tips.setText("MAC:" + DeviceUUIDManager.generateUUID(this)
                    + " 版本信息:" + ToolUtils.getVersionCode(this)
                    + "\n启动时间:" + SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM).format(new Date()));
        }
    }
    private void initDebugView(){
        tvLogDebug = findViewById(R.id.tv_LogDebug);
        rootLayoutLogDebug = findViewById(R.id.rootLayout_LogDebug);
        if(tvLogDebug != null){
            tvLogDebug.setVisibility(SettingConfig.isShowDebugView(this) ? View.VISIBLE : View.GONE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusConnectionChanged(ConnectChangedItem connectChangedItem){
        LogDebugUtil.appendLog(connectChangedItem.isConnecting() ? "SOCKET已连接" : "SOCKET断开连接");
        if(tips != null){
            tips.setTextColor(connectChangedItem.isConnecting() ? Color.GREEN : Color.RED);
        }
        onConnectionChanged(connectChangedItem.isConnecting());
    }

    private StringBuffer logStringBuffer = new StringBuffer();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogEvent(LogDebugItem logDebugItem) {
        if(tvLogDebug != null){
            if (SettingConfig.isShowDebugView(this)) {
                logStringBuffer.insert(0, logDebugItem.getMessage() + "\n");
                tvLogDebug.setText(logStringBuffer.toString());
                if(logStringBuffer.length() > 5000){
                    logStringBuffer = new StringBuffer();
                }
            }
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVedioRematoAngleChanged(SettingConfig.VideoRotateAngleChangedEventData eventData) {
        if(SettingConfig.getADScreenNum(this) == 9){
            NineADMainActivity.startActivity(this);
        }else{
            OneADMainActivity.startActivity(this);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }
}
