package com.fota.android.moudles.futures.money;

import android.view.View;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.moudles.exchange.BaseExchageChlidFragment;
import com.fota.android.moudles.futures.FuturesFragment;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.widget.dialog.MessageDialog;
import com.fota.android.widget.dialog.ShareDialog;
import com.fota.android.widget.myview.LevelView;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * 合约资产
 */
public class FuturesMoneyListFragment extends BaseExchageChlidFragment<FuturesMoneyPresenter> implements FuturesMoneyView {

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
    protected FuturesMoneyPresenter createPresenter() {
        return new FuturesMoneyPresenter(this);
    }

    /**
     * list coins
     */
    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<FuturesMoneyBean, ViewHolder>(getContext(), R.layout.item_futures_money) {

            @Override
            public void convert(final ViewHolder holder, final FuturesMoneyBean model, int position) {
                setHoldBg(holder);

                GradientDrawableUtils.setBgColor(holder.getView(R.id.order_left), AppConfigs.getColor(model.isBuy()));

                GradientDrawableUtils.setBgColor(holder.getView(R.id.order_left), AppConfigs.getColor(model.isBuy()));

                GradientDrawableUtils.setBoardColor(holder.getView(R.id.buy_or_sell), AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.buy_or_sell, AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.asset_name, AppConfigs.getColor(model.isBuy()));

                holder.setText(R.id.entrust_time, model.getFormatTime());

                holder.setText(R.id.buy_or_sell, model.getFormatBuyOrSell(getContext()));

                holder.setText(R.id.asset_name, model.getContractName());

                holder.setText(R.id.average_price, model.getAveragePrice());

                holder.setText(R.id.open_position_price, model.getOpenPositionPrice());

                holder.setText(R.id.current_price, model.getCurrentPrice());

                holder.setText(R.id.average_price, model.getAveragePrice());

                holder.setText(R.id.margin, model.getMargin());

                holder.setText(R.id.applies, model.getApplies());

                holder.setText(R.id.earning_rate, model.getEarningRate());

                holder.<LevelView>getView(R.id.futures_level).setLevel(model.getQuantile());

//                holder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        if (model.isCanceled()) {
//                            return false;
//                        }
//                        DialogUtils.showDialog(getContext(), new DialogModel(getXmlString(R.string.sure_liquidation))
//                                .setSureText(getXmlString(R.string.sure))
//                                .setSureClickListen(new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//
//                                    }
//                                })
//                                .setCancelText(getXmlString(R.string.cancel))
//                        );
//                        return false;
//                    }
//                });

                holder.getView(R.id.tv_stop).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showStopDialog(model);
                    }
                });

                holder.getView(R.id.tv_close_order).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new MessageDialog(getContext(), "确定平仓？", new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                deleteOrder(model);
                                return null;
                            }
                        }).show();

//                        closeOrder(model);
                    }
                });

                holder.setTextColor(R.id.futures_tv_lever, AppConfigs.getColor(model.isBuy()));

                GradientDrawableUtils.setBoardColor(holder.getView(R.id.futures_tv_lever), AppConfigs.getColor(model.isBuy()));

                holder.setText(R.id.futures_tv_lever, "X" + model.getLever());

                holder.getView(R.id.iv_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        List<FutureItemEntity> applicationCache = FotaApplication.getInstance().getMarketsCardsList();
                        String price = "";
                        for (FutureItemEntity each : applicationCache){
                            if (each.getEntityType() == 2 && model.getContractId().equals(each.getEntityId()+"")){
                                price = each.getLastPrice();
                                if (model.getAveragePrice().contains("."))
                                    price = new BigDecimal(each.getLastPrice()).setScale(model.getAveragePrice().split("\\.")[1].length()).toPlainString();
                            }
                        }

                        new ShareDialog(getActivity(),
                                model.isBuy(),
                                model.getLever(),
                                model.getContractName(),
                                model.getEarningRate(),
                                model.getAveragePrice(),
                                price,
                                getActivity()).show();
                    }
                });

            }
        };
    }

    private String getLevel() {
        if (getParentFragment() != null && getParentFragment() instanceof FuturesFragment) {
            return ((FuturesFragment) getParentFragment()).getLevel();
        }
        return "10";
    }

    private void deleteOrder(FuturesMoneyBean model) {
        ((FuturesFragment) getParentFragment()).removeMoney(model);
    }

    private void showStopDialog(FuturesMoneyBean model){
        ((FuturesFragment) getParentFragment()).showStopDialog(model);
    }

    private void closeOrder(FuturesMoneyBean model){
        ((FuturesFragment) getParentFragment()).closeOrder(model);
    }

    @Override
    public void setDataList(List list) {
        super.setDataList(list);
    }

    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_notitle_xml;
    }


    @Override
    public void replaceData(List<FuturesMoneyBean> item) {
        List<FuturesMoneyBean> oldData = adapter.getListData();
        if (Pub.getListSize(oldData) <= 50) {
            setDataList(item);
            return;
        }
        List<FuturesMoneyBean> newArray = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            oldData.remove(0);
        }
        newArray.addAll(item);
        newArray.addAll(oldData);
        setDataList(newArray);

    }
}
