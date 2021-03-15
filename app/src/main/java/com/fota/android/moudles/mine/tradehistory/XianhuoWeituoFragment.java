package com.fota.android.moudles.mine.tradehistory;

import android.view.View;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.moudles.mine.bean.XianhuoWeituoBean;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

/**
 * 现货委托
 */
public class XianhuoWeituoFragment extends MvpListFragment<XianhuoWeituoPresenter> {
    @Override
    protected XianhuoWeituoPresenter createPresenter() {
        return new XianhuoWeituoPresenter(this);
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
    }

    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_notitle_trans_xml;
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
        adapter = new EasyAdapter<XianhuoWeituoBean.XianhuoWeituoBeanItem, ViewHolder>(getContext(), R.layout.item_his_xianhuoweituo) {

            @Override
            public void convert(final ViewHolder holder, final XianhuoWeituoBean.XianhuoWeituoBeanItem model, int position) {
                setHoldBg(holder);

                GradientDrawableUtils.setBgColor(holder.getView(R.id.order_left), AppConfigs.getColor(model.isBuy()));

                GradientDrawableUtils.setBoardColor(holder.getView(R.id.buy_or_sell), AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.buy_or_sell, AppConfigs.getColor(model.isBuy()));

//                holder.setTextColor(R.id.asset_name, AppConfigs.getColor(model.isBuy()));

                holder.setText(R.id.entrust_time, model.getFormatTime());

                holder.setText(R.id.buy_or_sell, model.getFormatBuyOrSell(getContext()));

                holder.setText(R.id.asset_name, model.getFormatAssetName());

                holder.setText(R.id.type, model.getFormatType(getContext()));

                holder.setText(R.id.entrust_price, model.getPrice());

                holder.setText(R.id.entrust_money, model.getTotalAmount());

                holder.setText(R.id.status, model.getFormatStatue(getContext()));

                holder.setText(R.id.avg_price, model.getPurchasePrice());

                holder.setText(R.id.done_money, model.getFilledAmount());

//                holder.setText(R.id.total_amount, model.getTotalAmount());
//
//                holder.setText(R.id.entrust_amount, model.getFilledAmount());
//
//                holder.setText(R.id.entrust_price, model.getPrice());
//
//                holder.setText(R.id.status, model.getFormatStatue(getContext()));


            }


        };
    }

    protected void setHoldBg(ViewHolder holder) {
        if (AppConfigs.isWhiteTheme()) {
            holder.setBackgroundRes(R.id.order_main, R.drawable.ft2_corner_mian_color);
        } else {
            holder.setBackgroundRes(R.id.order_main, R.drawable.ft_corner_mian_color);
        }
    }

    @Override
    public void onRefresh() {
        getPresenter().resetTime();
        super.onRefresh();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            L.a("onHiddenChanged XianhuoWeituoFragment hide");

        }else {
//            startProgressDialog();
//            onRefresh();
            L.a("onHiddenChanged XianhuoWeituoFragment show");
        }
    }

    @Override
    public boolean setFootAndHeadTrans() {
        return true;
    }


}
