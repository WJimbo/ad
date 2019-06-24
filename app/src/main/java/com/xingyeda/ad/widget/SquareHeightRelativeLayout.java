package com.xingyeda.ad.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SquareHeightRelativeLayout extends RelativeLayout {
    public SquareHeightRelativeLayout(Context context) {
        super(context);
    }

    public SquareHeightRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareHeightRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
