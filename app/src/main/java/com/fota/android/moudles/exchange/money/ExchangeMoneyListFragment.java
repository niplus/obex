package com.fota.android.moudles.exchange.money;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fota.android.R;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.common.bean.wallet.WalletPresenter;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.moudles.exchange.BaseExchageChlidFragment;
import com.fota.android.moudles.exchange.index.ExchangeFragment;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

import java.util.List;


public class ExchangeMoneyListFragment extends BaseExchageChlidFragment<WalletPresenter> {

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
        getPresenter().setSocket(true);
        onRefresh();
    }

    @Override
    public void setDataList(List list) {
        super.setDataList(list);
        if (getParentFragment() instanceof ExchangeFragment) {
            ((ExchangeFragment) getParentFragment()).event(ExchangeFragment.EVENT_MONEY_CHANGED);
        }
    }

    @Override
    protected WalletPresenter createPresenter() {
        return new WalletPresenter(this);
    }

    /**
     * list coins
     */
    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<WalletItem, ViewHolder>(getContext(), R.layout.item_exchange_money) {

            @Override
            public void convert(final ViewHolder holder, final WalletItem model, int position) {
                setHoldBg(holder);
                holder.setText(R.id.asset_name, model.getAssetName());
                holder.setText(R.id.amount, model.getAmount());
                holder.setText(R.id.locked_amount, model.getLockedAmount());
                holder.setText(R.id.valuation, model.getValuation());
                holder.setText(R.id.total_money, model.getAllAmount());
                Glide.with(getContext()).load(model.getCoinIconUrl())
                        .into(holder.<ImageView>getView(R.id.asset_icon));

//                holder.getView(R.id.iv_share).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new ShareDialog(getActivity(),
//                                model.isBuy(),
//                                model.getLever(),
//                                model.getContractName(),
//                                model.getEarningRate(),
//                                model.getAveragePrice(),
//                                ((FuturesFragment)getParentFragment()).getCurrentPrice(),
//                                getActivity()).show();
//                    }
//                });
            }
        };
    }


    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_notitle_xml;
    }


    public String geItemAmount(String name) {
        //jiang adapter有些情况会为空
        if (adapter == null) {
            return "--";
        }

        if (!Pub.isListExists(adapter.getListData())) {
            return "--";
        }
        if (name == null) {
            return "--";
        }
        for (Object item : adapter.getListData()) {
            if (item instanceof WalletItem) {
                if (item != null && name.equals(((WalletItem) item).getAssetName())) {
                    return ((WalletItem) item).getAmount();
                }
            }
        }
        return "--";
    }


}
