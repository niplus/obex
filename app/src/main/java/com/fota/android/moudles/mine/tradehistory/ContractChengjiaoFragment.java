package com.fota.android.moudles.mine.tradehistory;

import android.view.View;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.commonlib.utils.L;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.moudles.mine.bean.ContractChengjiaoBean;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

/**
 * 合约成交
 */
public class ContractChengjiaoFragment extends MvpListFragment<ContractChengjiaoPresenter> {
    @Override
    protected ContractChengjiaoPresenter createPresenter() {
        return new ContractChengjiaoPresenter(this);
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
        adapter = new EasyAdapter<ContractChengjiaoBean.ContractChengjiaoBeanItem, ViewHolder>(getContext(), R.layout.item_his_contractchengjiao) {

            @Override
            public void convert(final ViewHolder holder, final ContractChengjiaoBean.ContractChengjiaoBeanItem model, int position) {
                setHoldBg(holder);

                GradientDrawableUtils.setBgColor(holder.getView(R.id.order_left), AppConfigs.getColor(model.isBuy()));

                GradientDrawableUtils.setBgColor(holder.getView(R.id.order_left), AppConfigs.getColor(model.isBuy()));

                GradientDrawableUtils.setBoardColor(holder.getView(R.id.buy_or_sell), AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.buy_or_sell, AppConfigs.getColor(model.isBuy()));

//                holder.setTextColor(R.id.asset_name, AppConfigs.getColor(model.isBuy()));

                holder.setText(R.id.entrust_time, model.getFormatTime());

                holder.setText(R.id.buy_or_sell, model.getFormatBuyOrSell(getContext()));

                holder.setText(R.id.asset_name, model.getContractName());

                holder.setText(R.id.entrust_price, model.getFilledPrice());

                holder.setText(R.id.money, model.getMatchValue());

                holder.setText(R.id.order_type, model.getFormatType(getContext()));

                holder.setText(R.id.fee, model.getFee());

            }

            protected void setHoldBg(ViewHolder holder) {
                if (AppConfigs.isWhiteTheme()) {
                    holder.setBackgroundRes(R.id.order_main, R.drawable.ft2_corner_mian_color);
                } else {
                    holder.setBackgroundRes(R.id.order_main, R.drawable.ft_corner_mian_color);
                }
            }
        };
    }

    @Override
    public void onRefresh() {
        getPresenter().resetTime();
        super.onRefresh();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            L.a("onHiddenChanged ContractChengjiaoFragment hide");

        } else {
//            startProgressDialog();
//            onRefresh();
            L.a("onHiddenChanged ContractChengjiaoFragment show");
        }
    }

    @Override
    public boolean setFootAndHeadTrans() {
        return true;
    }
}
