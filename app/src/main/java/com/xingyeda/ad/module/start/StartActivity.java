package com.xingyeda.ad.module.start;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.TextView;


import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.xingyeda.ad.BaseActivity;

import com.xingyeda.ad.BuildConfig;
import com.xingyeda.ad.R;
import com.xingyeda.ad.module.main.OneADMainActivity;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.DialogHelper;
import com.zz9158.app.common.utils.FileHelper;
import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.UIUtils;
import com.zz9158.app.common.utils.wifi.WifiHelper;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

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
        MyLog.i("StartActivity正常启动:" + this.toString());
        if(savedInstanceState != null){
            MyLog.i("StartActivity正常启动: savedInstanceState :" + savedInstanceState.toString());
        }
//        //这是为了应用程序安装完后直接打开，按home键退出后，再次打开程序出现的BUG
//        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
//            //结束你的activity
//            return;
//        }
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
