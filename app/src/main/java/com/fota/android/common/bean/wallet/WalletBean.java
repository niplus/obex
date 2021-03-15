package com.fota.android.common.bean.wallet;


import java.io.Serializable;
import java.util.List;

/**
 * Created by stone on 2018/3/31.
 */

public class WalletBean implements Serializable {


    String totalValuation;
    String totalUsdValuation;

    List<WalletItem> item;

    public String getTotalValuation() {
        return totalValuation;
    }

    public void setTotalValuation(String totalValuation) {
        this.totalValuation = totalValuation;
    }

    public String getTotalUsdValuation() {
        return totalUsdValuation;
    }

    public void setTotalUsdValuation(String totalUsdValuation) {
        this.totalUsdValuation = totalUsdValuation;
    }

    public List<WalletItem> getItem()  {
        return item;
    }

    public void setItem(List<WalletItem> item) {
        this.item = item;
    }
}
