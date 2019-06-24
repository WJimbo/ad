package com.xingyeda.ad.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareHeightLinearLayout extends LinearLayout {
    public SquareHeightLinearLayout(Context context) {
        super(context);
    }

    public SquareHeightLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareHeightLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
