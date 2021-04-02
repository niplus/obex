package com.fota.android.common.bean;

import java.io.Serializable;
import java.util.List;

public class Test implements Serializable {
    /**
     * assetId : 1
     * assetName : USDT
     * amount : 99192.00443580
     * lockedAmount : 810.00000000
     * totalAmount : 100002.00443580
     * valuation : 1.90546609
     * valuationUsd : 100002.0044358044
     * lockAccountAmount : 0.00000000
     * minWithdrawAmount : 100
     * minWithdrawPrecision : 2
     * minDepositLimitAmount : null
     * minDepositPrecision : 2
     * usdtMinWithdrawFee : 0.01
     * usdtMinWithdrawProportion : 0.0002
     * fixedFeeAmount : 1
     * coinIconUrl : https://tsex-images.s3-accelerate.amazonaws.com/ic_usdt.png
     * coinTradeId : null
     * withKYCVerifyWithdrawAmount : 99999999
     * dnetwork : ["OMNI","ERC20","TRC20"]
     * wnetwork : ["OMNI","ERC20","TRC20"]
     */

    private int assetId;
    private String assetName;
    private String amount;
    private String lockedAmount;
    private String totalAmount;
    private String valuation;
    private String valuationUsd;
    private String lockAccountAmount;
    private String minWithdrawAmount;
    private int minWithdrawPrecision;
    private Object minDepositLimitAmount;
    private int minDepositPrecision;
    private String usdtMinWithdrawFee;
    private String usdtMinWithdrawProportion;
    private String fixedFeeAmount;
    private String coinIconUrl;
    private Object coinTradeId;
    private String withKYCVerifyWithdrawAmount;
    private List<String> dnetwork;
    private List<String> wnetwork;
}
