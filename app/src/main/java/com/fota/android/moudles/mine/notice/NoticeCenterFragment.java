package com.fota.android.moudles.mine.notice;

import android.text.TextUtils;
import android.view.View;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.moudles.mine.bean.NoticeCenterBean;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;
import com.guoziwei.fota.util.DateUtils;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 通知中心
 */
public class NoticeCenterFragment extends MvpListFragment<NoticeCenterPresenter> {
    @Override
    protected NoticeCenterPresenter createPresenter() {
        return new NoticeCenterPresenter(this);
    }

    @Override
    protected boolean setRefreshEnable() {
        return true;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        onRefresh();
        startProgressDialog();
        JPushInterface.clearAllNotifications(FotaApplication.getInstance());
    }

    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_xml;
    }

    @Override
    public void onLoadMore() {
        super.onLoadMore();
    }

    @Override
    protected boolean setLoadEnable() {
        return true;
    }

    /**
     * list coins
     */
    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<NoticeCenterBean.NoticeCenterBeanItem, ViewHolder>(getContext(), R.layout.item_notice) {

            @Override
            public void convert(final ViewHolder holder, final NoticeCenterBean.NoticeCenterBeanItem model, int position) {
                if (model == null)
                    return;
                holder.setText(R.id.tv_msg, TextUtils.isEmpty(model.getInfoMsg()) ? mContext.getResources().getString(R.string.mine_hengxian) : model.getInfoMsg());
                holder.setText(R.id.tv_time, model.getInfoTime() > 0 ? DateUtils.formatDateTime(model.getInfoTime()) : mContext.getResources().getString(R.string.mine_hengxian));
            }
        };
    }

    @Override
    public void setDataList(List list) {
        super.setDataList(list);
        EventWrapper.post(Event.create(R.id.mine_noticedel));//通知我的页面刷新状态栏

    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.mine_notice_title);
    }

}
