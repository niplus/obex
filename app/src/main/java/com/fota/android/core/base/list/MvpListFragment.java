package com.fota.android.core.base.list;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fota.android.R;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BaseListPresenter;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.MvpFragment;
import com.fota.android.widget.btbwidget.FotaRecyclerView;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.RecyclerViewUtils;
import com.fota.android.widget.recyclerview.SmartRefreshLayoutUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

/**
 * 这里需要写RecyclerView框架了 不然太麻烦了
 * Created by Dell on 2018/6/20.
 */

public abstract class MvpListFragment<P extends BasePresenter> extends MvpFragment<P> implements BaseListView {

    /**
     * 我用这里就用基本的类吧
     */
    protected FotaRecyclerView mRecyclerView;
    protected View root;
    protected EasyAdapter adapter;
    protected View headView;
    protected SmartRefreshLayout refreshLayout;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * 决定还是抛弃DataBinding的学法  重写不方便
         */
        root = View.inflate(getContext(), getContainerLayout(), null);
        return root;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        //首先隐藏布局
        initMainAdapter();
        //定制样式
        initLayoutManger();
        //设置RecyclerView
        initRecyclerView();
    }

  /*  public LayoutInflater getLayoutInflater() {
        return (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }*/

    /**
     * 默认是有按推按钮的
     *
     * @return
     */
    protected boolean isBackEnable() {
        return true;
    }

    /**
     * 定制样式
     */
    protected void initLayoutManger() {
        if (mRecyclerView == null) {
            return;
        }
        RecyclerViewUtils.initRecyclerView(mRecyclerView, getContext());
    }


    @Override
    public void onResume() {
        super.onResume();
        if (refreshLayout != null) {
            SmartRefreshLayoutUtils.refreshHeadLanguage(refreshLayout, getContext());
            SmartRefreshLayoutUtils.refreshFooterLanguage(refreshLayout, getContext());
        }
    }

    /**
     * 设置背景颜色
     */
    protected void setBg(int color) {
        if (refreshLayout == null)
            return;
        refreshLayout.setBackgroundColor(color);
    }

    /**
     * 初始化RecyclerView组件
     */
    protected void initRecyclerView() {
        if (mRecyclerView == null) {
            return;
        }
//        if (setLoadEnable() || setRefreshEnable()) {
//            mRecyclerView.setLoadingListener(this);
//        }

        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        if (refreshLayout != null) {
            if (setFootAndHeadTrans()) {
                SmartRefreshLayoutUtils.initHeaderTrans(refreshLayout, getContext());
            } else {
                SmartRefreshLayoutUtils.initHeader(refreshLayout, getContext());
            }
            refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(RefreshLayout refreshlayout) {
                    MvpListFragment.this.onRefresh();
                    //refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                }
            });
        }

        if (setLoadEnable() && refreshLayout != null) {
            if (setFootAndHeadTrans()) {
                SmartRefreshLayoutUtils.initFooterTrans(refreshLayout, getContext());
            } else {
                SmartRefreshLayoutUtils.initFooter(refreshLayout, getContext());
            }
            refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(RefreshLayout refreshlayout) {
                    MvpListFragment.this.onLoadMore();
                    //refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                }
            });
        }

        if (adapter != null) {
            mRecyclerView.setAdapter(adapter);
        }

        refreshLayout.setEnableRefresh(setRefreshEnable());
        refreshLayout.setEnableLoadMore(setLoadEnable());
//        mRecyclerView.setPullRefreshEnabled(setRefreshEnable());
//        mRecyclerView.setLoadingMoreEnabled(setLoadEnable());
        headView = setHeadView();
        emptyContainer = headView;
        if (headView != null) {
            //refreshLayout.setRefreshContent(headView);
            mRecyclerView.addHeaderView(headView);
        }
        afterSetHeadView();
        setEmptyView();

    }

    /**
     * 是否使用透明的header和footer
     *
     * @return
     */
    public boolean setFootAndHeadTrans() {
        return false;
    }

    /**
     * 默认显示动画
     *
     * @return
     */
    protected boolean showItemAnimation() {
        return true;
    }

    /**
     * 设置具体的setHeadView事件
     */
    protected void afterSetHeadView() {

    }

    protected void setEmptyView() {
        emptyContainer = headView;
    }

    protected View setHeadView() {
        View view = View.inflate(getContext(), R.layout.common_empty_layout, null);
        return view;
    }

    public void setDataList(List list) {
        Log.i("nidongliang", "list: " + list);
        if (list == null) return;
        if (mRecyclerView == null) {
            return;
        }
        doComplete();
        if (adapter != null) {
            adapter.putList(list);
        }
        if (Pub.isListExists(list)) {
            //refreshLayout.finishLoadMoreWithNoMoreData();
            //mRecyclerView.setNoMore(false);
            showdata();
        } else {
            showNoData();
        }
    }

    public void addDataList(List list) {
        if (mRecyclerView == null)
            return;
        doComplete();
        if (adapter != null) {
            adapter.addList(list);
        }
        if (!Pub.isListExists(list)) {
            refreshLayout.finishLoadMoreWithNoMoreData();
            // mRecyclerView.setNoMore(true);
        }

    }


    /**
     * 默认可以刷新
     *
     * @return
     */
    protected boolean setRefreshEnable() {
        return true;
    }

    /**
     * 默认不可加载更多
     *
     * @return
     */
    protected boolean setLoadEnable() {
        return false;
    }

    /**
     * 必须要实现的
     * 这里是我们的主adapter  也就是mvplist  会帮你自动实现逻辑的adapter
     */
    protected void initMainAdapter() {

    }


    @Override
    protected void findViewById() {
        super.findViewById();
        mRecyclerView = findViewById(R.id.btb_recycler_view);
    }

    protected int getContainerLayout() {
        return R.layout.recyclerview_xml;
    }

    public void setRecyclerView(FotaRecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public void showNoNetWork() {
        doComplete();
        super.showNoNetWork();
    }

    @Override
    public void showdata() {
        doComplete();
        super.showdata();
    }

    public void doComplete() {
        if (refreshLayout != null) {
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
        }
    }

    @Override
    public void showFailer(String msg, ApiException e) {
        doComplete();
        super.showFailer(msg, e);
    }

    @Override
    public void showNoData() {
        doComplete();
        super.showNoData();
    }

    @Override
    protected void emptyButtonReloadEvent() {
        //这个和onRefresh是等价的  只是加了个动画
        if (mRecyclerView != null && setRefreshEnable()) {
            refreshLayout.autoRefresh();
            //mRecyclerView.refresh();
        } else {
            super.emptyButtonReloadEvent();
        }
    }

    @Override
    public void onLoadMore() {
        super.onLoadMore();
        if (getPresenter() == null) {
            return;
        }
        if (getPresenter() instanceof BaseListPresenter) {
            ((BaseListPresenter) getPresenter()).onLoadData(true);
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        if (refreshLayout != null) {
            refreshLayout.setNoMoreData(false);
            //mRecyclerView.refresh();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        onLoadData();

    }

    protected void onLoadData() {
        if (getPresenter() instanceof BaseListPresenter) {
            ((BaseListPresenter) getPresenter()).onLoadData(false);
        }
    }

    @Override
    public void refreshComplete() {
        doComplete();
    }

    public void adapterNotifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}
