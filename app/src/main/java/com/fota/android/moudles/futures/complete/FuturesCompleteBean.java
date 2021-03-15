package com.fota.android.moudles.futures.complete;

import android.content.Context;

import com.fota.android.R;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.TimeUtils;

import java.io.Serializable;

public class FuturesCompleteBean implements Serializable {

    private String filledPrice;
    private String fee;
    private long filledDate;
    private int contractId;
    private String contractName;
    private int orderDirection;
    //private String orderValue;
    private int orderType;
    private String matchValue;


    public void setFilledPrice(String filledPrice) {
        this.filledPrice = filledPrice;
    }

    public String getFilledPrice() {
        return filledPrice;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFee() {
        return fee;
    }

    public void setFilledDate(long filledDate) {
        this.filledDate = filledDate;
    }

    public long getFilledDate() {
        return filledDate;
    }


    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractName() {
        return contractName;
    }

    public void setOrderDirection(int orderDirection) {
        this.orderDirection = orderDirection;
    }

    public int getOrderDirection() {
        return orderDirection;
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
            return CommonUtils.getResouceString(context, R.string.tradehis_duo_short);
        } else {
            return CommonUtils.getResouceString(context, R.string.tradehis_kong_short);
        }
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
            case 5:
                return context.getString(R.string.future_complete_decrease_leverage);
        }
        return "";
    }


    public String getFormatTime() {
        return TimeUtils.getDateToString(getFilledDate());
    }

    public String getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(String matchValue) {
        this.matchValue = matchValue;
    }
}
