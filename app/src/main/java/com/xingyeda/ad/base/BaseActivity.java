package com.xingyeda.ad.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.xingyeda.ad.MainApplication;
import com.xingyeda.ad.broadcast.BroadCasetKeys;
import com.xingyeda.ad.module.versionmanager.VersionManager;
import com.xingyeda.ad.receiver.InnerReceiver;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.ToastUtils;


public class BaseActivity extends Activity {
    protected Context mContext;
    private Handler mHandler;
    public String getTag()
    {

        return this.getClass().getName();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.i("OnCreate--->" + this.toString());
        MainApplication.isAnyActivityStartedFlag = true;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        mContext = this;
        mHandler = new Handler();
        //注册键盘
        receiverHome();
    }

    private boolean isKeyCode_BackClick = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isKeyCode_BackClick){
                isKeyCode_BackClick = false;
                moveTaskToBack(true);
                return false;
            }
            isKeyCode_BackClick = true;
            ToastUtils.showToast(this,"再按一次回到桌面");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isKeyCode_BackClick = false;
                }
            },1000);
            return false;
        }
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            Toast.makeText(getApplicationContext(), "HOME 键已被禁用...", Toast.LENGTH_SHORT).show();
            return true;//同理
        }
        return super.onKeyDown(keyCode, event);
    }

    //创建广播
    InnerReceiver innerReceiver = new InnerReceiver();

    private void receiverHome() {
        //动态注册广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //启动广播
        registerReceiver(innerReceiver, intentFilter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerBoradcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }
    private BroadcastReceiver mBroadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {//Socket广播获取
            String action = intent.getAction();
            if (action.equals(BroadCasetKeys.UPDATE_DEVICE)) {//版本更新
                VersionManager.checkVersions(BaseActivity.this);
            }
        }

    };
    private void registerBoradcastReceiver() {//广播注册
        IntentFilter intent = new IntentFilter();
        intent.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        intent.addAction(BroadCasetKeys.UPDATE_DEVICE);//更新设备
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(innerReceiver);
        MyLog.i("onDestroy--->" + this.toString());
    }
}
