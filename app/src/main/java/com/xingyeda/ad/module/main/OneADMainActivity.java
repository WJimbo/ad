package com.xingyeda.ad.module.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.xingyeda.ad.BaseActivity;
import com.xingyeda.ad.R;

import butterknife.BindView;

public class OneADMainActivity extends BaseActivity {
    @BindView(R.id.adView)
    com.xingyeda.lowermachine.business.modules.main.widget.ADView adView;
    @BindView(R.id.tv_LogDebug)
    TextView tvLogDebug;
    @BindView(R.id.tips)
    TextView tips;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, OneADMainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_one_ad);
    }
}
