package com.fota.android.http;

import com.fota.android.commonlib.utils.Pub;
import com.fota.android.moudles.futures.FutureContractBean;

import java.io.Serializable;
import java.util.List;

public class ContractAssetBean implements Serializable {

    private String name;
    private String iconUrl;
    private int contractTradeAmountPrecision;
    private int contractTradePricePrecision;
    private int contractMaxMinPricePrecision;
    private int contractMaxValuePrecision;

    private String defaultAmount;



    public String getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(String defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    public int getContractMaxMinPricePrecision() {
        return contractMaxMinPricePrecision;
    }

    public void setContractMaxMinPricePrecision(int contractMaxMinPricePrecision) {
        this.contractMaxMinPricePrecision = contractMaxMinPricePrecision;
    }

    public int getContractMaxValuePrecision() {
        return contractMaxValuePrecision;
    }

    public void setContractMaxValuePrecision(int contractMaxValuePrecision) {
        this.contractMaxValuePrecision = contractMaxValuePrecision;
    }

    String contractPrecision;
    private List<FutureContractBean> content;

    public String getContractPrecision() {
        return contractPrecision;
    }

    public void setContractPrecision(String contractPrecision) {
        this.contractPrecision = contractPrecision;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FutureContractBean> getContent() {
        return content;
    }

    public void setContent(List<FutureContractBean> content) {
        this.content = content;
    }

    public int getContractTradeAmountPrecision() {
        return contractTradeAmountPrecision;
    }

    public void setContractTradeAmountPrecision(int contractTradeAmountPrecision) {
        this.contractTradeAmountPrecision = contractTradeAmountPrecision;
    }

    public int getContractTradePricePrecision() {
        return contractTradePricePrecision;
    }

    public void setContractTradePricePrecision(int contractTradePricePrecision) {
        this.contractTradePricePrecision = contractTradePricePrecision;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    //jiang
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ContractAssetBean) {
            String name = ((ContractAssetBean) obj).getName();
            if (Pub.isStringEmpty(this.name)) {
                return this.name == name;
            }
            if (this.name.equals(name)) {
                return true;
            }
            return false;
        }
        return super.equals(obj);
    }
}
