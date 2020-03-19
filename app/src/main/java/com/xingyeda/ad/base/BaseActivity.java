package com.xingyeda.ad.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.xingyeda.ad.receiver.InnerReceiver;

import top.wuhaojie.installerlibrary.AutoInstaller;

public class BaseActivity extends Activity {
    protected Context mContext;
    protected AutoInstaller autoInstaller;
    public String getTag()
    {

        return this.getClass().getName();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        mContext = this;

        //注册键盘
        receiverHome();

        autoInstaller = new AutoInstaller.Builder(this)
                .setMode(AutoInstaller.MODE.AUTO_ONLY)
                .build();

        autoInstaller.setOnStateChangedListener(new AutoInstaller.OnStateChangedListener() {
            @Override
            public void onStart() {
                // 当后台安装线程开始时回调
            }

            @Override
            public void onComplete() {
                // 当请求安装完成时回调
            }

            @Override
            public void onNeed2OpenService() {
                // 当需要用户手动打开 `辅助功能服务` 时回调
                // 可以在这里提示用户打开辅助功能
                Toast.makeText(getApplicationContext(), "请打开辅助功能服务", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(getApplicationContext(), "返回键无效", Toast.LENGTH_SHORT).show();
            return true;//return true;拦截事件传递,从而屏蔽back键。
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(innerReceiver);
    }
}
