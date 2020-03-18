package com.xingyeda.ad.module.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xingyeda.ad.BaseActivity;
import com.xingyeda.ad.R;

public class OneADMainActivity extends BaseActivity {
    public static void startActivity(Context context){
        Intent intent = new Intent(context,OneADMainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_one_ad);
    }
}
