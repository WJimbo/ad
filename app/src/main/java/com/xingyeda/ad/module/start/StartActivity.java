package com.xingyeda.ad.module.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xingyeda.ad.BuildConfig;
import com.xingyeda.ad.R;
import com.xingyeda.ad.base.BaseActivity;
import com.xingyeda.ad.config.DeviceUUIDManager;
import com.xingyeda.ad.config.SettingConfig;
import com.xingyeda.ad.config.SettingConfigManager;
import com.xingyeda.ad.module.main.NineADMainActivity;
import com.xingyeda.ad.module.main.OneADMainActivity;
import com.xingyeda.ad.module.register.RegisterManager;
import com.xingyeda.ad.service.TimerRebootService;
import com.xingyeda.ad.service.socket.CommandReceiveService;
import com.xingyeda.ad.util.MyLog;
import com.xingyeda.ad.widget.SquareHeightRelativeLayout;
import com.zz9158.app.common.utils.ToolUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


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
    @BindView(R.id.rootLayout_Versions)
    SquareHeightRelativeLayout rootLayoutVersions;
    private Disposable disposable;
//    private CountDownTimer countDownTimer;
    private WeakReference<TextView> textViewWeakReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isStarted) {
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

        textViewWeakReference = new WeakReference<>(infoTextView);

        versionInfoTextView.setText(new StringBuilder("MAC:")
                .append(DeviceUUIDManager.generateUUID(this))
                .append("\n版本号:")
                .append(ToolUtils.appTool().getVersionNameFromPackage(this))
                .append("_")
                .append(ToolUtils.appTool().getAppVersionCode(this))
                .append("\n编译时间:")
                .append(BuildConfig.BUILD_DATE)
                .toString());

        infoTextView.setRotation(SettingConfig.getScreenRotateAngle(this));
        rootLayoutVersions.setRotation(SettingConfig.getScreenRotateAngle(this));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)rootLayoutVersions.getLayoutParams();
        if(SettingConfig.getScreenRotateAngle(this) == 90){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }else if(SettingConfig.getScreenRotateAngle(this) == 270){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }else if(SettingConfig.getScreenRotateAngle(this) == 0){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }else{
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }
        rootLayoutVersions.setLayoutParams(layoutParams);

        CommandReceiveService.startService(this);
        TimerRebootService.startService(this);
        //SystemRunningMonitorService.startService(this);
        RegisterManager.getInstance().startToRegister(this);
        SettingConfigManager.getInstance().startUpdateSettingTimer(getApplicationContext());
        final int waitTime = BuildConfig.DEBUG ? 3 : 15;
        disposable = Flowable.intervalRange(0, waitTime, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aLong -> {
                    if(textViewWeakReference != null){
                        TextView weakTextView = textViewWeakReference.get();
                        if (weakTextView != null) {
                            weakTextView.setText("正在启动中(" + (waitTime - aLong) + "秒)...");
                        }
                    }
                })
                .doOnComplete(() -> {
                    if(SettingConfig.getADScreenNum(getApplicationContext()) == 9){
                        NineADMainActivity.startActivity(mContext);
                    }else{
                        OneADMainActivity.startActivity(mContext);
                    }

                    finish();
                })
                .subscribe();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
