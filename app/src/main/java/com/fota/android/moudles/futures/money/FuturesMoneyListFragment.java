package com.fota.android.moudles.futures.money;

import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fota.android.R;
import com.fota.android.app.SocketKey;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.BaseHttpPage;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.moudles.InviteViewModel;
import com.fota.android.moudles.exchange.BaseExchageChlidFragment;
import com.fota.android.moudles.futures.FuturesFragment;
import com.fota.android.widget.dialog.ShareDialog;
import com.fota.android.widget.myview.LevelView;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;
import com.ndl.lib_common.utils.LiveDataBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 合约资产
 */
public class FuturesMoneyListFragment extends BaseExchageChlidFragment<FuturesMoneyPresenter> implements FuturesMoneyView {

    private String closeContractId="";
    private InviteViewModel inviteViewModel;

    @Override
    protected boolean setRefreshEnable() {
        return false;
    }

    @Override
    protected boolean setLoadEnable() {
        return false;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        inviteViewModel = new ViewModelProvider(getParentFragment()).get(InviteViewModel.class);
        onRefresh();

        LiveDataBus.INSTANCE.getBus(SocketKey.MinePositionReqType+"").observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                BaseHttpPage<FuturesMoneyBean> list = (BaseHttpPage<FuturesMoneyBean>)o;
                getPresenter().setData(list.getItem(), getPresenter().isLoadMore());
            }
        });
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

                if (model.isBuy()){
                    GradientDrawableUtils.setBgColor(holder.getView(R.id.buy_or_sell),(int)0x3349AA6C);
                }else {
                    GradientDrawableUtils.setBgColor(holder.getView(R.id.buy_or_sell),(int)0x33CC5753);
                }


                holder.setTextColor(R.id.buy_or_sell, AppConfigs.getColor(model.isBuy()));

                holder.setTextColor(R.id.asset_name, AppConfigs.getColor(model.isBuy()));

                holder.setText(R.id.entrust_time, model.getFormatTime());

                holder.setText(R.id.buy_or_sell, model.getFormatBuyOrSell(getContext()));

                holder.setText(R.id.asset_name, model.getContractName().replace("永续", " " + getString(R.string.perp)));

                holder.setText(R.id.average_price, model.getAveragePrice());

                holder.setText(R.id.open_position_price, model.getPositionQty());

                holder.setText(R.id.current_price, model.getAvaQty());

                holder.setText(R.id.margin, model.getMargin());

                holder.setText(R.id.applies, model.getApplies());

                holder.setText(R.id.earning_rate, model.getEarningRate());

                holder.<LevelView>getView(R.id.futures_level).setLevel(model.getQuantile());

                holder.getView(R.id.tv_stop).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showStopDialog(model);
                    }
                });

                holder.getView(R.id.tv_close_order).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(model);
                    }
                });

//                holder.setTextColor(R.id.futures_tv_lever, AppConfigs.getColor(model.isBuy()));

//                GradientDrawableUtils.setBoardColor(holder.getView(R.id.futures_tv_lever), AppConfigs.getColor(model.isBuy()));

                holder.setText(R.id.futures_tv_lever, "X" + model.getLever());

                holder.getView(R.id.iv_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ShareDialog(getActivity(),
                                model.isBuy(),
                                model.getLever(),
                                model.getContractName(),
                                model.getEarningRate(),
                                model.getAveragePrice(),
                                model.getLastMatchPrice(),
                                inviteViewModel.getInviteRecordLiveData().getValue().getInviteUrl(),
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
        closeContractId = model.getContractId();
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

        if (!closeContractId.equals("")) {
            int i = 0;
            for (; i < list.size(); i++) {
                FuturesMoneyBean futuresMoneyBean = (FuturesMoneyBean) list.get(i);
                if (futuresMoneyBean.getContractId().equals(closeContractId)){
                    break;
                }
            }

            if (i == list.size() && getParentFragment() instanceof FuturesFragment) {
                ((FuturesFragment)getParentFragment()).cancelSuccess();
                closeContractId = "";
            }

        }


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
