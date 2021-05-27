package com.fota.android.moudles.futures.complete;

import android.view.View;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.moudles.exchange.BaseExchageChlidFragment;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

public class FuturesCompleteFragment extends BaseExchageChlidFragment<FuturesCompletePresenter> {


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
    }

    @Override
    protected boolean setRefreshEnable() {
        return false;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        onRefresh();
    }

    @Override
    protected FuturesCompletePresenter createPresenter() {
        return new FuturesCompletePresenter(this);
    }

    /**
     * list coins
     */
    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<FuturesCompleteBean, ViewHolder>(getContext(), R.layout.item_futures_completes) {

            @Override
            public void convert(final ViewHolder holder, final FuturesCompleteBean model, int position) {
                setHoldBg(holder);

                GradientDrawableUtils.setBgColor(holder.getView(R.id.order_left), AppConfigs.getColor(model.isBuy()));

                GradientDrawableUtils.setBoardColor(holder.getView(R.id.buy_or_sell), AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.buy_or_sell, AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.asset_name, AppConfigs.getColor(model.isBuy()));

                holder.setText(R.id.entrust_time, model.getFormatTime());

                holder.setText(R.id.buy_or_sell, model.getFormatBuyOrSell(getContext()));

                holder.setText(R.id.asset_name, model.getContractName().replace("永续", " "+getString(R.string.perp)));

                holder.setText(R.id.entrust_price, model.getFilledPrice());

                holder.setText(R.id.money, model.getMatchValue());

                holder.setText(R.id.order_type, model.getFormatType(getContext()));

                holder.setText(R.id.fee, model.getFee());

                holder.setText(R.id.profit, model.getRealizedPnl());
            }
        };
    }


    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_notitle_xml;
    }
}
