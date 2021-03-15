package com.fota.android.moudles.futures.order;

import android.content.Context;

import com.fota.android.R;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.TimeUtils;

import java.io.Serializable;

public class FuturesOrderBean implements Serializable {

    private long id;
    private long entrustTime;


    private int contractId;
    private String contractName;
    private int orderDirection;
    private int orderType;


    private String entrustValue;

    private String unfilledAmount;
    private String price;
    private String purchasePrice;
    private String filledValue;
    private String fee;
    private int status;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getContractName() {
        return contractName;
    }


    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFee() {
        return fee;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }


    public String getFilledValue() {
        return filledValue;
    }

    public void setFilledValue(String filledValue) {
        this.filledValue = filledValue;
    }

    public String getFormatTime() {
        return TimeUtils.getDateToString(getEntrustTime());
    }


    public long getEntrustTime() {
        return entrustTime;
    }

    public void setEntrustTime(long entrustTime) {
        this.entrustTime = entrustTime;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public int getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(int orderDirection) {
        this.orderDirection = orderDirection;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getUnfilledAmount() {
        return unfilledAmount;
    }

    public void setUnfilledAmount(String unfilledAmount) {
        this.unfilledAmount = unfilledAmount;
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
        if (context == null) {
            return "";
        }
        if (isBuy()) {
            return CommonUtils.getResouceString(context, R.string.tradehis_duo_short);
        } else {
            return CommonUtils.getResouceString(context, R.string.tradehis_kong_short);

        }
    }

    /**
     * 委托状态8-已报，9-部成，10-已成，3-部撤，4-已撤
     *
     * @return
     */
    public String getFormatStatue(Context context) {
        if (context == null) {
            return "";
        }
        if (context.getResources() == null) {
            return "";
        }
        switch (status) {
            case 8:
                return context.getString(R.string.exchange_statue_hasreport);
            case 9:
                return context.getString(R.string.exchange_statues_some_isok);
            case 10:
                return context.getString(R.string.exchange_statue_all_ok);
            case 3:
                return context.getString(R.string.exchange_status_some_cancelled);
            case 4:
                return context.getString(R.string.exchange_status_cancelled);
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

    public String getEntrustValue() {
        return entrustValue;
    }

    public void setEntrustValue(String entrustValue) {
        this.entrustValue = entrustValue;
    }
}
