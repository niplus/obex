package com.fota.android.moudles.exchange.complete;

import android.view.View;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.moudles.exchange.BaseExchageChlidFragment;
import com.fota.android.moudles.mine.bean.XianhuoChengjiaoBean;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

/**
 * 现货成交
 */
public class ExchangeCompleteFragment extends BaseExchageChlidFragment<ExchangeCompletePresenter> {
    @Override
    protected ExchangeCompletePresenter createPresenter() {
        return new ExchangeCompletePresenter(this);
    }

    @Override
    protected boolean setRefreshEnable() {
        return true;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        onRefresh();
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
        adapter = new EasyAdapter<XianhuoChengjiaoBean.XianhuoChengjiaoBeanItem, ViewHolder>(getContext(), R.layout.item_his_xianhuochengjiao) {

            @Override
            public void convert(final ViewHolder holder, final XianhuoChengjiaoBean.XianhuoChengjiaoBeanItem model, int position) {
                setHoldBg(holder);

                holder.setVisible(R.id.order_left, true);

                GradientDrawableUtils.setBgColor(holder.getView(R.id.order_left), AppConfigs.getColor(model.isBuy()));

                GradientDrawableUtils.setBoardColor(holder.getView(R.id.buy_or_sell), AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.buy_or_sell, AppConfigs.getColor(model.isBuy()));

//                holder.setTextColor(R.id.asset_name, AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.buy_or_sell, AppConfigs.getColor(model.isBuy()));

//                holder.setTextColor(R.id.asset_name, AppConfigs.getColor(model.isBuy()));

                holder.setText(R.id.asset_name, model.getFormatAssetName());

                holder.setText(R.id.entrust_time, model.getFormatTime());

                holder.setText(R.id.buy_or_sell, model.getFormatBuyOrSell(getContext()));

                holder.setText(R.id.order_amount, model.getFilledAmount());

                holder.setText(R.id.tv_price, model.getFilledPrice());

                holder.setText(R.id.buy_or_sell, model.getFormatBuyOrSell(getContext()));

                //holder.setText(R.id.tv_fee, model.getFormatFee());


//                holder.setText(R.id.asset_name, model.getFormatAssetName());

            }
        };
    }

    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_notitle_xml;
    }

}
