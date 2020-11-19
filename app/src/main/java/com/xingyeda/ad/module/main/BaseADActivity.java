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
import com.xingyeda.ad.module.ad.data.ADListManager;
import com.xingyeda.ad.service.SystemRunningMonitorService;
import com.xingyeda.ad.service.socket.CommandReceiveService;
import com.xingyeda.ad.service.socket.ConnectChangedItem;
import com.xingyeda.ad.util.MyLog;
import com.xingyeda.ad.widget.SquareHeightRelativeLayout;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseADActivity extends BaseActivity {

    private TextView tvLogDebug;
    private TextView tips;
    private SquareHeightRelativeLayout rootLayoutLogDebug;
    private SquareHeightRelativeLayout rootLayoutTips;
    private Unbinder unbinder;
//    private boolean isFeedDogThreadRunning = false;
//    private Thread feedDogThread;
//    private WeakReference<BaseActivity> weakReferenceThis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(getLayout());
        unbinder = ButterKnife.bind(this);
        initView(savedInstanceState,SettingConfig.getScreenRotateAngle(this));
        initTipsView();
        initDebugView();
        rotationViews(SettingConfig.getScreenRotateAngle(this));
        onConnectionChanged(CommandReceiveService.isConnected);
        requestList();
//        isFeedDogThreadRunning = true;
//        weakReferenceThis = new WeakReference<>(this);
//        feedDogThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (isFeedDogThreadRunning
//                        && weakReferenceThis != null
//                        && weakReferenceThis.get() != null
//                        && !weakReferenceThis.get().isFinishing()){
//                    try {
//                        UIUtils.runOnMainThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                SystemRunningMonitorService.feedDog(getApplicationContext());
//                            }
//                        });
//
//                        Thread.sleep(10 * 1000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        feedDogThread.start();
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
    protected abstract void initView(Bundle saveInstanceState,float screenAngle);

    protected abstract void rotationADViews(float rotateAngle);

    public abstract void onConnectionChanged(boolean isConnecting);

    private void initTipsView(){
        tips = findViewById(R.id.tips);
        rootLayoutTips = findViewById(R.id.rootLayout_Tips);
        if(tips != null){
            tips.setVisibility(SettingConfig.isShowDebugView(this) ? View.VISIBLE:View.GONE);

            tips.setText("MAC:" + DeviceUUIDManager.generateUUID(this)
                    + " 版本信息:" + ToolUtils.getVersionName(this) + "_" +ToolUtils.getVersionCode(this)
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
    protected void onResume() {
        super.onResume();
        MyLog.i(this.toString() +" onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i(this.toString() +" onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i(this.toString() +" onDestroy");
        if(unbinder != null){
            unbinder.unbind();
        }
//        isFeedDogThreadRunning = false;
//        feedDogThread.interrupt();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        MyLog.i(this.toString() +" onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        MyLog.i(this.toString() +" onTrimMemory:" + level);
    }
}
