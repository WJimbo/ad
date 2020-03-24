package com.xingyeda.ad.module.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.xingyeda.ad.module.main.widget.ADView;
import com.xingyeda.ad.module.versionmanager.VersionManager;
import com.xingyeda.ad.service.socket.CommandReceiveService;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OneADMainActivity extends BaseActivity {
    @BindView(R.id.adView)
    ADView adView;
    @BindView(R.id.tv_LogDebug)
    TextView tvLogDebug;
    @BindView(R.id.tips)
    TextView tips;
    @BindView(R.id.rootLayout_LogDebug)
    SquareHeightRelativeLayout rootLayoutLogDebug;
    @BindView(R.id.rootLayout_Tips)
    SquareHeightRelativeLayout rootLayoutTips;
    Unbinder unbinder;
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, OneADMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_one_ad);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        //设置布局旋转角度
        float rotateAngle = SettingConfig.getScreenRotateAngle(this);

        rootLayoutLogDebug.setRotation(rotateAngle);
        rootLayoutTips.setRotation(rotateAngle);

        RelativeLayout.LayoutParams debugLayoutParams = (RelativeLayout.LayoutParams)rootLayoutLogDebug.getLayoutParams();
        RelativeLayout.LayoutParams tipsLayoutParams = (RelativeLayout.LayoutParams)rootLayoutTips.getLayoutParams();
        if(SettingConfig.getScreenRotateAngle(this) == 90){
            debugLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            tipsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }else if(SettingConfig.getScreenRotateAngle(this) == 270){
            debugLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tipsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }else if(SettingConfig.getScreenRotateAngle(this) == 0){
            debugLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            tipsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }else{
            debugLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            tipsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }
        rootLayoutLogDebug.setLayoutParams(debugLayoutParams);
        rootLayoutTips.setLayoutParams(tipsLayoutParams);



        if(SettingConfig.getScreenRotateAngle(this) == 90){
            adView.setDefaultImage(R.mipmap.bg_defualt_landscape_90);
            adView.setRotation(90f);
        }else if(SettingConfig.getScreenRotateAngle(this) == 270){
            adView.setDefaultImage(R.mipmap.bg_defualt_landscape_270);
            adView.setRotation(270f);
        }else if(SettingConfig.getScreenRotateAngle(this) == 180){
            adView.setRotation(180f);
            adView.setDefaultImage(R.mipmap.bg_defualt_portrait_180);
        }else{
            adView.setRotation(0f);
            adView.setDefaultImage(R.mipmap.bg_defualt_portrait);
        }
        tvLogDebug.setVisibility(SettingConfig.isShowDebugView(this) ? View.VISIBLE : View.GONE);
        tips.setVisibility(SettingConfig.isShowDebugView(this) ? View.VISIBLE:View.GONE);

        tips.setText("MAC:" + DeviceUUIDManager.generateUUID(this)
                + " 版本信息:" + ToolUtils.getVersionCode(this)
                + "\n启动时间:" + SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM).format(new Date()));

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
        onConnectionChanged(new ConnectChangedItem(CommandReceiveService.isConnected));
    }
    private void requestList() {
        ADListManager.getInstance(getApplicationContext()).setNeedUpdateList();
    }
    private StringBuffer logStringBuffer = new StringBuffer();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogEvent(LogDebugItem logDebugItem) {
        if (SettingConfig.isShowDebugView(this)) {
            logStringBuffer.insert(0, logDebugItem.getMessage() + "\n");
            tvLogDebug.setText(logStringBuffer.toString());
            if(logStringBuffer.length() > 5000){
                logStringBuffer = new StringBuffer();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionChanged(ConnectChangedItem connectChangedItem){
        LogDebugUtil.appendLog(connectChangedItem.isConnecting() ? "SOCKET已连接" : "SOCKET断开连接");
        if(tips != null){
            tips.setTextColor(connectChangedItem.isConnecting() ? Color.GREEN : Color.RED);
        }
        if(adView != null){
            adView.setCountDownTitleColor(connectChangedItem.isConnecting() ? Color.WHITE : Color.RED);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVedioRematoAngleChanged(SettingConfig.VideoRotateAngleChangedEventData eventData) {
        OneADMainActivity.startActivity(this);
        finish();
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
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        adView.onDestroy();
    }
}
