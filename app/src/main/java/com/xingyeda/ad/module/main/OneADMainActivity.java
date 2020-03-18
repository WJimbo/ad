package com.xingyeda.ad.module.main;

import android.content.Context;
import android.content.Intent;

import com.xingyeda.ad.BaseActivity;

public class OneADMainActivity extends BaseActivity {
    public static void startActivity(Context context){
        Intent intent = new Intent(context,OneADMainActivity.class);
        context.startActivity(intent);
    }
}
