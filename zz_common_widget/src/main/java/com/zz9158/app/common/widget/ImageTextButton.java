package com.zz9158.app.common.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author tangyongx
 * @date 6/12/2018
 */
public class ImageTextButton extends FrameLayout {
    public ImageTextButton(@NonNull Context context) {
        super(context);
    }

    public ImageTextButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageTextButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void initView(Context context,AttributeSet attributeSet){
        View view = LayoutInflater.from(context).inflate(R.layout.widget_imagetextbutton,null);
        addView(view);
    }
}
