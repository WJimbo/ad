package com.xingyeda.ad.module;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xingyeda.ad.R;
import com.xingyeda.ad.base.BaseActivity;
import com.xingyeda.ad.module.start.StartActivity;
import com.xingyeda.ad.util.DeviceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PowerTestActivity extends BaseActivity {
    @BindView(R.id.edit_PowerOff)
    EditText editPowerOff;
    @BindView(R.id.edit_PowerOn)
    EditText editPowerOn;
    @BindView(R.id.btn_Ok)
    Button btnOk;
    @BindView(R.id.btn_Close)
    Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_test);
        ButterKnife.bind(this);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUtil.timingSwitchForADTV(getApplicationContext(),editPowerOff.getText().toString(),editPowerOn.getText().toString());
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity.isStarted = false;
                finish();
            }
        });
    }
}
