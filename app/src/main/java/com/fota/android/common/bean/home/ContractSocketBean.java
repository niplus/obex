package com.fota.android.common.bean.home;

import java.io.Serializable;
import java.util.List;

public class ContractSocketBean implements Serializable {
    private List<HotContractBean> hotContract;
    public List<HotContractBean> getHotContract() {
        return hotContract;
    }

    public void setHotContract(List<HotContractBean> hotContract) {
        this.hotContract = hotContract;
    }

    @Override
    public String toString() {
        return "ContractSocketBean{" +
                "hotContract=" + hotContract +
                '}';
    }
}
