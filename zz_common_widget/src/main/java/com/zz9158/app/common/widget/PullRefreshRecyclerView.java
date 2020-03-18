package com.zz9158.app.common.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * 下拉刷新ListView
 * @author tangyongx
 * @date 5/12/2018
 */
public class PullRefreshRecyclerView extends SmartRefreshLayout {
    private RecyclerView recyclerView;

    public PullRefreshRecyclerView(Context context) {
        super(context);
        init(context,null,0);
    }

    public PullRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public PullRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }
    private void init(Context context,AttributeSet attrs, int defStyleAttr){
        //设置 Header 为 贝塞尔雷达 样式
        this.setPrimaryColorsId(R.color.SteelBlue, android.R.color.white);
        this.setRefreshHeader(new ClassicsHeader(context));

        this.setRefreshFooter(new ClassicsFooter(context));
        recyclerView = new RecyclerView(context,attrs,defStyleAttr);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addView(recyclerView,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public SmartRefreshLayout setOnRefreshListener(OnRefreshListener listener) {
        return super.setOnRefreshListener(listener);
    }

    @Override
    public SmartRefreshLayout setOnLoadMoreListener(OnLoadMoreListener listener) {
        return super.setOnLoadMoreListener(listener);
    }

    @Override
    public SmartRefreshLayout finishLoadMore() {
        return super.finishLoadMore();
    }

    @Override
    public SmartRefreshLayout finishRefresh() {
        return super.finishRefresh();
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
