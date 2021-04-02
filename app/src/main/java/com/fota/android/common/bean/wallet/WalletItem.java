package com.fota.android.common.bean.wallet;


import com.fota.android.common.bean.BaseAsset;
import com.fota.android.commonlib.utils.Pub;

import java.util.List;

/**
 * Created by stone on 2018/3/31.
 */

public class WalletItem extends BaseAsset {

    public WalletItem(String assetId, String assetName) {
        super(assetId, assetName);
    }

    private String lockedAmount;
    private String amount;
    private String valuation;
    private String lockAccountAmount;
    private String minWithdrawAmount;
    private String minWithdrawPrecision;
    private String coinIconUrl;
    private String totalAmount;//总资产
    private String network;
    private boolean isNetWork;
    private List<String> dnetwork;
    private List<String> wnetwork;


    public WalletItem(WalletItem item) {
        setAssetName(item.getAssetName());
        setAssetId(item.getAssetId());
        lockedAmount = item.lockedAmount;
        amount = item.amount;
        valuation = item.valuation;
        lockAccountAmount = item.lockAccountAmount;
        minWithdrawAmount = item.minWithdrawAmount;
        minWithdrawPrecision = item.minWithdrawPrecision;
        coinIconUrl = item.coinIconUrl;
        totalAmount = item.totalAmount;//总资产
        network = item.network;
        usdtMinWithdrawFee = item.usdtMinWithdrawFee;
        usdtMinWithdrawProportion = item.usdtMinWithdrawProportion;
        fixedFeeAmount = item.fixedFeeAmount;
    }

    public String getNetWork() {
        return network;
    }

    public void setNetWork(String network) {
        this.network = network;
    }

    public boolean isNetWork() {
        return isNetWork;
    }

    /**
     * 提币最低手续费
     */
    String usdtMinWithdrawFee;
    /**
     * 提币超出最低按比例计算的比例
     */
    String usdtMinWithdrawProportion;

    // 固定手续费
    String fixedFeeAmount;

    public String getFixedFeeAmount() {
        return fixedFeeAmount;
    }

    public void setFixedFeeAmount(String fixedFeeAmount) {
        this.fixedFeeAmount = fixedFeeAmount;
    }

    public String getUsdtMinWithdrawFee() {
        return usdtMinWithdrawFee;
    }

    public void setUsdtMinWithdrawFee(String usdtMinWithdrawFee) {
        this.usdtMinWithdrawFee = usdtMinWithdrawFee;
    }

    public String getUsdtMinWithdrawProportion() {
        return usdtMinWithdrawProportion;
    }

    public void setUsdtMinWithdrawProportion(String usdtMinWithdrawProportion) {
        this.usdtMinWithdrawProportion = usdtMinWithdrawProportion;
    }

    public String getLockAccountAmount() {
        return lockAccountAmount;
    }

    public void setLockAccountAmount(String lockAccountAmount) {
        this.lockAccountAmount = lockAccountAmount;
    }

    public void setLockedAmount(String lockedAmount) {
        this.lockedAmount = lockedAmount;
    }

    public String getLockedAmount() {
        return lockedAmount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setValuation(String valuation) {
        this.valuation = valuation;
    }

    public String getValuation() {
        return valuation;
    }

    public String getValuationFormat() {
        if (Pub.isStringEmpty(valuation)) {
            return "";
        }
        return "≈ " + valuation + " BTC";
    }

    public String getAllAmount() {
        return totalAmount;
    }

    public String getMinWithdrawAmount() {
        return minWithdrawAmount;
    }

    public void setMinWithdrawAmount(String minWithdrawAmount) {
        this.minWithdrawAmount = minWithdrawAmount;
    }

    public String getMinWithdrawPrecision() {
        if (minWithdrawPrecision == null) {
            return "2";
        }
        return minWithdrawPrecision;
    }

    public void setMinWithdrawPrecision(String minWithdrawPrecision) {
        this.minWithdrawPrecision = minWithdrawPrecision;
    }

    public String getCoinIconUrl() {
        return coinIconUrl;
    }

    public void setCoinIconUrl(String coinIconUrl) {
        this.coinIconUrl = coinIconUrl;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<String> getDnetwork() {
        return dnetwork;
    }

    public void setDnetwork(List<String> dnetwork) {
        this.dnetwork = dnetwork;
    }

    public List<String> getWnetwork() {
        return wnetwork;
    }

    public void setWnetwork(List<String> wnetwork) {
        this.wnetwork = wnetwork;
    }

    public void OMNI() {
        this.isNetWork = true;
        this.setNetWork("OMNI");
    }

    public void ETH() {
        this.isNetWork = true;
        this.setNetWork("ETH");
    }

    @Override
    public String getValue() {
        if (isNetWork) {
            return getKey();
        } else {
            return super.getValue();
        }

    }

    @Override
    public String getKey() {
        if (isNetWork) {
            return "USDT" + "(" + getNetWork() + ")";
        }
        return getAssetName();
    }

}
