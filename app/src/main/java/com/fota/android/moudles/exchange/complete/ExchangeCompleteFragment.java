package com.fota.android.moudles.exchange.complete;

import android.view.View;

import androidx.lifecycle.Observer;

import com.fota.android.R;
import com.fota.android.app.SocketKey;
import com.fota.android.common.BusKey;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.BaseHttpPage;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.WebSocketClient1;
import com.fota.android.moudles.exchange.BaseExchageChlidFragment;
import com.fota.android.moudles.mine.bean.XianhuoChengjiaoBean;
import com.fota.android.socket.WebSocketEntity;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;
import com.ndl.lib_common.utils.LiveDataBus;

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

        LiveDataBus.INSTANCE.getBus(BusKey.EVENT_LOGIN).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (o != null){
                    WebSocketEntity<BtbMap> socketEntity = new WebSocketEntity<>();
                    socketEntity.setReqType(SocketKey.MineDealReqType);
                    BtbMap map = new BtbMap();
                    map.p("pageNo", getPresenter().getPageNo());
                    map.p("pageSize", getPresenter().getPageSize());
                    socketEntity.setParam(map);
                    socketEntity.setHandleType(1);
                    WebSocketClient1.INSTANCE.register(socketEntity);
                }else {
                    WebSocketClient1.INSTANCE.unRegist(SocketKey.MineDealReqType);
                }
            }
        });
        LiveDataBus.INSTANCE.getBus(SocketKey.MineDealReqType + "").observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                BaseHttpPage<XianhuoChengjiaoBean.XianhuoChengjiaoBeanItem> list = (BaseHttpPage<XianhuoChengjiaoBean.XianhuoChengjiaoBeanItem>)o;
                getPresenter().setData(list.getItem(), getPresenter().isLoadMore());
            }
        });
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
