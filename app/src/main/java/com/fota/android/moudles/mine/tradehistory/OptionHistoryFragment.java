package com.fota.android.moudles.mine.tradehistory;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.URLUtils;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.moudles.mine.bean.OptionHisBean;
import com.fota.android.widget.bubblepopup.BubblePopupWindow;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

/**
 * 期权交易记录
 */
public class OptionHistoryFragment extends MvpListFragment<OptionHistoryPresenter> {
    private BubblePopupWindow popupTypeWindow;
    TextView tv_asset_check;
    private String currencyid = "";//当前账户

    @Override
    protected OptionHistoryPresenter createPresenter() {
        return new OptionHistoryPresenter(this);
    }

    @Override
    protected boolean setRefreshEnable() {
        return true;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        tv_asset_check = view.findViewById(R.id.tv_asset_check);
        tv_asset_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypePopWindow();
            }
        });
//        mvpPresenter.initList();
        onRefresh();
        startProgressDialog();
    }

    @Override
    protected int getContainerLayout() {
        return R.layout.fragment_optionhistory;
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
        adapter = new EasyAdapter<OptionHisBean.OptionHisBeanItem, ViewHolder>(getContext(), R.layout.item_optionhistory) {

            @Override
            public void convert(final ViewHolder holder, final OptionHisBean.OptionHisBeanItem model, int position) {


                holder.setText(R.id.tv_asset, model.getAsset() + "/USDT");
                holder.setText(R.id.tv_price, model.getPrice());
                holder.setText(R.id.tv_profit, model.getProfit());
                holder.setText(R.id.tv_total, model.getCalAmountPrice());
                holder.setText(R.id.tv_price_unit, mContext.getResources().getString(R.string.option_investment) + model.getCurrencyName());
                holder.setText(R.id.tv_profit_unit, mContext.getResources().getString(R.string.his_option_pl) + model.getCurrencyName());
                holder.setText(R.id.tv_total_unit, mContext.getResources().getString(R.string.his_option_equity) + model.getCurrencyName());
                holder.setText(R.id.tv_time, model.getDate());
//                holder.setImageResource(R.id.imv_icon, model.getIconID());
                holder.setText(R.id.tv_type, model.getOptionTypeDesc());
                holder.setText(R.id.tv_direction, model.getDirectionDesc());
                holder.setText(R.id.tv_index, model.getSpotIndex());
                holder.setText(R.id.tv_price_strike, model.getStrikePrice());
                holder.setText(R.id.tv_price_settle, model.getSettlementPriceDesc());
                ImageView imageView = holder.getConvertView().findViewById(R.id.imv_icon);
                if (!TextUtils.isEmpty(model.getIconUrl())) {
                    Glide.with(getContext()).load(URLUtils.getFullPath(model.getIconUrl())).apply(new RequestOptions()
                            .placeholder(R.mipmap.icon_account_vfota)
                            .error(R.mipmap.icon_account_vfota))
                            .into(imageView);
                }
//                ((TextView) holder.getConvertView().findViewById(R.id.tv_price)).setTypeface(FontTypeFace.getTypeface(getContext()));
//                ((TextView) holder.getConvertView().findViewById(R.id.tv_profit)).setTypeface(FontTypeFace.getTypeface(getContext()));
            }
        };


    }


    @Override
    public void onRefresh() {
        getPresenter().resetTime();
        getPresenter().reqOptionCoins();
        super.onRefresh();
    }


    @Override
    public boolean setFootAndHeadTrans() {
        return true;
    }

    private void showTypePopWindow() {
        if (!Pub.isListExists(getPresenter().gettypeList())) {
            return;
        }
        if (popupTypeWindow == null) {
            popupTypeWindow = new BubblePopupWindow(getContext());
            popupTypeWindow.setOnPopListener(new BubblePopupWindow.OnPopClickListener() {
                @Override
                public void onPopClick(FtKeyValue model, int position) {
                    tv_asset_check.setText(model.getKey());
                    getPresenter().setCurrencyPosition(position);
                }
            });
            popupTypeWindow.getAdapter().putList(getPresenter().gettypeList());
            popupTypeWindow.setValue(getPresenter().gettypeList().get(0).getValue());
        }
        popupTypeWindow.show(tv_asset_check);
    }

    /**
     * 设置查询时间
     *
     * @param start_time
     * @param end_time
     */
    public void setCheckDate(long start_time, long end_time) {
        if (start_time != 0 && end_time != 0) {
            getPresenter().setTime(start_time, end_time);
        }
    }
}
