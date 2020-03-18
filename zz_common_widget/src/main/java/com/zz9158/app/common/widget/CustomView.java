package com.zz9158.app.common.widget;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author tangyongx
 * @date 2018-12-18
 */
public abstract class CustomView extends FrameLayout {
    protected View rootView;
    public CustomView(@NonNull Context context) {
        super(context);
        initAttr(null);
        initView();
    }

    public CustomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        initView();
    }

    public CustomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        initView();
    }

    /**
     * 获取布局文件中配置的属性
     * @param attrs
     */
    protected void initAttr(AttributeSet attrs){
//        TypedValue typedValue = new TypedValue();
//        context.getTheme().resolveAttribute(android.R.attr.textAppearanceLarge,android.R.attr.background, typedValue, true);
//        int[] attribute = new int[] { android.R.attr.textSize };
    }

    /**
     * 初始化View
     */
    protected void initView(){
        rootView = View.inflate(getContext(),getViewLayout(),null);
        addView(rootView);
    }
    protected abstract @LayoutRes int getViewLayout();


}
