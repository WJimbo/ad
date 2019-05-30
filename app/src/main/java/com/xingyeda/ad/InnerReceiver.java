package com.xingyeda.ad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InnerReceiver extends BroadcastReceiver {

    String SYSTEM_REASON = "reason";

    String DOWN_HOME = "homekey";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) { // 监听home键
            String reason = intent.getStringExtra(SYSTEM_REASON);
            // 表示按了home键,程序到了后台
            if(DOWN_HOME.equals(reason))
            {
                return;
            }


        }

    }
}
