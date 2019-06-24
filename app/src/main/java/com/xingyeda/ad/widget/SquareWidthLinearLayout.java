package com.xingyeda.ad.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareWidthLinearLayout extends LinearLayout {
    public SquareWidthLinearLayout(Context context) {
        super(context);
    }

    public SquareWidthLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareWidthLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
