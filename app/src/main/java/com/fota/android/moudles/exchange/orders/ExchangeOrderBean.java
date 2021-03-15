package com.fota.android.moudles.exchange.orders;

import android.content.Context;

import com.fota.android.R;
import com.fota.android.common.bean.BaseAsset;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.TimeUtils;

public class ExchangeOrderBean extends BaseAsset {

    private String id;
    private long entrustTime;
    private int orderType;
    private String totalAmount;
    private String unfilledAmount;
    private String price;
    private String purchasePrice;
    private String fee;
    private String filledAmount;
    private int status;
    private int orderDirection;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUnfilledAmount() {
        return unfilledAmount;
    }

    public void setUnfilledAmount(String unfilledAmount) {
        this.unfilledAmount = unfilledAmount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 交易类型：1-卖，2-买
     *
     * @return
     */
    public boolean isBuy() {
        return 2 == orderDirection;
    }

    public String getFormatBuyOrSell(Context context) {
        if (isBuy()) {
            return getString(context, R.string.common_buy2_short);
        } else {
            return getString(context, R.string.common_sell2_short);
        }
    }

    private String getString(Context context, int common_buy2) {
        return CommonUtils.getResouceString(context, common_buy2);
    }

    public String getFormatAssetName() {
        return getAssetName();
    }

    public String getFormatTime() {
        return TimeUtils.getDateToString(entrustTime);
    }

    public String getFilledAmount() {
        return filledAmount;
    }

    public void setFilledAmount(String filledAmount) {
        this.filledAmount = filledAmount;
    }

    /**
     * 委托状态8-已报，9-部成，10-已成，3-部撤，4-已撤
     *
     * @return
     */
    public String getFormatStatue(Context context) {
        switch (status) {
            case 8:
                return getString(context, R.string.exchange_statue_hasreport);
            case 9:
                return getString(context, R.string.exchange_statues_some_isok);
            case 10:
                return getString(context, R.string.exchange_statue_all_ok);
            case 3:
                return getString(context, R.string.exchange_status_some_cancelled);
            case 4:
                return getString(context, R.string.exchange_status_cancelled);
        }
        return "";
    }

    public String getFormatType(Context context) {
        //orderType 1-限价单，2-市场单，3-强平
        switch (orderType) {
            case 1:
                return context.getString(R.string.contractweituo_type_limit);
            case 2:
                return context.getString(R.string.contractweituo_type_market);
            case 3:
                return context.getString(R.string.contractweituo_type_force);
        }
        return "";
    }
}
