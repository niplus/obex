package com.fota.android.moudles.exchange.orders;

import android.content.DialogInterface;
import android.view.View;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.moudles.exchange.BaseExchageChlidFragment;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

public class ExchangeOrdersFragment extends BaseExchageChlidFragment<ExchangeOrdersPresenter> {

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        onRefresh();
    }

    @Override
    protected ExchangeOrdersPresenter createPresenter() {
        return new ExchangeOrdersPresenter(this);
    }

    @Override
    protected boolean setRefreshEnable() {
        return false;
    }

    /**
     * list coins
     */
    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<ExchangeOrderBean, ViewHolder>(getContext(), R.layout.item_exchange_order) {

            @Override
            public void convert(final ViewHolder holder, final ExchangeOrderBean model, int position) {
                setHoldBg(holder);

                GradientDrawableUtils.setBgColor(holder.getView(R.id.order_left), AppConfigs.getColor(model.isBuy()));

                GradientDrawableUtils.setBoardColor(holder.getView(R.id.buy_or_sell), AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.buy_or_sell, AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.asset_name, AppConfigs.getColor(model.isBuy()));

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

                holder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DialogUtils.showDialog(getContext(), new DialogModel(getXmlString(R.string.exchange_comfirm_cancel_order))
                                .setSureText(getXmlString(R.string.sure))
                                .setSureClickListen(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getPresenter().deleteOrder(model.getId());
                                    }
                                })
                                .setCancelText(getXmlString(R.string.cancel))
                        );
                        return false;
                    }
                });

            }
        };
    }


    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_notitle_xml;
    }

}
