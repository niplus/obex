package com.fota.android.moudles.futures;

import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.http.ContractAssetBean;

import java.io.Serializable;

public class FutureContractBean implements Serializable, FtKeyValue {

    String contractId;
    String contractName;
    int lastDays;
    String lever;
    int contractType;
    String assetName;
    int status;
    String symbol;


    ContractAssetBean parent;

    public void setParent(ContractAssetBean parent) {
        this.parent = parent;
    }

    public ContractAssetBean getParent() {
        return parent;
    }

    public int getContractType() {
        return contractType;
    }

    public void setContractType(int contractType) {
        this.contractType = contractType;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    private boolean isTrading() {
        return status == 3;
    }

    public void setLastDays(int lastDays) {
        this.lastDays = lastDays;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLever() {
        if (lever == null) {
            return "10";
        }
        return lever;
    }

    public void setLever(String lever) {
        this.lever = lever;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    @Override
    public String getKey() {
        return contractName;
    }

    @Override
    public String getValue() {
        return contractId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof FutureContractBean) {
            String id = ((FutureContractBean) obj).getContractId();
            if (Pub.isStringEmpty(id)) {
                return false;
            }
            if (id.equals(contractId)) {
                return true;
            }
            return false;
        }
        return super.equals(obj);
    }

}
