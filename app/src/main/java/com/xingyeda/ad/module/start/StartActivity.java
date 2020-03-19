package com.xingyeda.ad.module.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.xingyeda.ad.base.BaseActivity;
import com.xingyeda.ad.BuildConfig;
import com.xingyeda.ad.R;
import com.xingyeda.ad.config.SettingConfig;
import com.xingyeda.ad.module.main.OneADMainActivity;
import com.xingyeda.ad.module.register.RegisterManager;
import com.xingyeda.ad.service.socket.CommandReceiveService;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.ToolUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author tangyongx
 * @date 11/10/18
 */

public class StartActivity extends BaseActivity {
    public static boolean isStarted = false;
    @BindView(R.id.infoTextView)
    TextView infoTextView;
    @BindView(R.id.versionInfoTextView)
    TextView versionInfoTextView;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isStarted){
            MyLog.i("StartActivity被重复启动:" + this.toString());
            finish();
            return;
        }
        isStarted = true;
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    MyLog.i("StartActivity 启动失败 isTaskRoot:" + this.toString());
                    finish();
                    return;
                }
            }
        }

        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        int waitTime = 15 * 1000;
        if (BuildConfig.DEBUG) {
            waitTime = 3 * 1000;
        }
        countDownTimer = new CountDownTimer(waitTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (infoTextView != null) {
                    infoTextView.setText("正在启动中(" + (millisUntilFinished + 500) / 1000 + "秒)...");
                }
            }

            @Override
            public void onFinish() {
                OneADMainActivity.startActivity(mContext);
            }
        };
        countDownTimer.start();
        versionInfoTextView.setText("版本号:" + ToolUtils.appTool().getVersionNameFromPackage(this) + "_" + ToolUtils.appTool().getAppVersionCode(this) + "\n编译时间:" + BuildConfig.BUILD_DATE);
        infoTextView.setRotation(SettingConfig.getScreenRotateAngle(this));
        versionInfoTextView.setRotation(SettingConfig.getScreenRotateAngle(this));
        CommandReceiveService.startService(this);
        RegisterManager.getInstance().startToRegister(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
