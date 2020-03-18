package com.zz9158.app.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
public class PullRefreshListView extends SmartRefreshLayout {
    private ListView listView;

    public PullRefreshListView(Context context) {
        super(context);
        init(context,null,0);
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public PullRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }
    private void init(Context context,AttributeSet attrs, int defStyleAttr){
        //设置 Header 为 贝塞尔雷达 样式
        this.setPrimaryColorsId(R.color.LightSlateGray, android.R.color.white);
        this.setRefreshHeader(new ClassicsHeader(context));

        this.setRefreshFooter(new ClassicsFooter(context));


        listView = new ListView(context,attrs,defStyleAttr);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addView(listView,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
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

    public ListView getListView() {
        return listView;
    }
}
