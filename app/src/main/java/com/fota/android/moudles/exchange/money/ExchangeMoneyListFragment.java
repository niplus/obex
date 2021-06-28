package com.fota.android.moudles.exchange.money;

import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.fota.android.R;
import com.fota.android.app.SocketKey;
import com.fota.android.common.BusKey;
import com.fota.android.common.bean.wallet.WalletBean;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.common.bean.wallet.WalletPresenter;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.WebSocketClient1;
import com.fota.android.moudles.exchange.BaseExchageChlidFragment;
import com.fota.android.moudles.exchange.index.ExchangeFragment;
import com.fota.android.socket.WebSocketEntity;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;
import com.ndl.lib_common.utils.LiveDataBus;

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

        LiveDataBus.INSTANCE.getBus(BusKey.EVENT_LOGIN).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (o != null){
                    WebSocketEntity<BtbMap> socketEntity = new WebSocketEntity<>();
                    socketEntity.setReqType(SocketKey.MineAssetReqType);
                    BtbMap map = new BtbMap();
                    map.put("pageNo", "1");
                    map.p("pageSize", getPresenter().getPageSize());
                    socketEntity.setParam(map);
                    socketEntity.setHandleType(1);
                    WebSocketClient1.INSTANCE.register(socketEntity);
                }else {
                    WebSocketClient1.INSTANCE.unRegist(SocketKey.MineAssetReqType);
                }
            }
        });
        LiveDataBus.INSTANCE.getBus(SocketKey.MineAssetReqType+"").observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                WalletBean list =  (WalletBean)o;
                getPresenter().setData(list.getItem(), false);
            }
        });
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
