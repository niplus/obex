package com.fota.android.common.bean.wallet;

import android.content.Context;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.TimeUtils;

import java.io.Serializable;

public class WalletHistoryBean implements Serializable {

    public final static int TYPE_RECHAGE = 1;
    public final static int TYPE_WITHDRAW = 2;

    private String id;

    private int transferType;
    private String fromAddress;
    private String toAddress;
    private int assetId;
    private String assetName;
    private String amount;
    private String txHash;
    private String fee;
    private int reason;
    private long gmtCreate;
    private long gmtModified;
    private int status;
    private long txTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormatAmout() {
        StringBuilder sb = new StringBuilder();
        if (TYPE_RECHAGE == transferType) {
            sb.append("+");
        }
        if (TYPE_WITHDRAW == transferType) {
            sb.append("-");
        }
        sb.append(amount);
//        sb.append(assetName);
        return sb.toString();
    }

    public String getFormatFee() {
        return fee + assetName;
    }

    public String getFormatTime() {
        return TimeUtils.getDateToString(gmtCreate);
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getTxHash() {
        if (Pub.isStringEmpty(txHash)) {
            return "--";
        }
        return txHash;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFee() {
        return fee;
    }

    public void setGmtCreate(long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtModified(long gmtModified) {
        this.gmtModified = gmtModified;
    }

    public long getGmtModified() {
        return gmtModified;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setTxTime(long txTime) {
        this.txTime = txTime;
    }

    public long getTxTime() {
        return txTime;
    }


    public boolean isUp() {
        return TYPE_RECHAGE == transferType;
    }

    /**
     * FTWithdrawStatusVerifying = 1,
     * FTWithdrawStatusVerifyFailed,
     * FTWithdrawStatusSend,
     * FTWithdrawStatusUnConfirm = 4,
     * FTWithdrawStatusConfirmed,
     * FTWithdrawStatusFailed,
     * FTWithdrawStatusCancelled,
     * FTWithdrawStatusUnknow = 9
     *
     * @return
     */
    public String getStatusFromat(Context context) {
        switch (status) {
            case 1:
                return context.getString(R.string.wallet_withdraw_waiting_valid);
            case 3:
                return context.getString(R.string.wallet_withdraw_waiting_send);
            case 4:
                return context.getString(R.string.wallet_withdraw_waiting_ensure);
            case 2:
                return context.getString(R.string.wallet_withraw_valid_failer);
            case 5:
                return context.getString(R.string.wallet_withdraw_complete);
            case 6:
                return context.getString(R.string.wallet_withdraw_failer);
            case 7:
                return context.getString(R.string.wallet_withdraw_cancel);
        }
        return "";
    }

    public String getStatusDetailFormat(Context context) {
        if (status != 2) {
            return "";
        }
        switch (reason) {
            case 0:
                return context.getString(R.string.wallet_detail_kyc);
            case 1:
                return context.getString(R.string.wallet_detail_address_error);
            case 2:
                return context.getString(R.string.wallet_detail_user_error);
            case 3:
                return context.getString(R.string.wallet_detail_other);
            default:
                return "";
        }
    }

    /**
     * 是否可取消
     *
     * @return
     */
    public boolean canCancel() {
        return status == 1;
    }

    public String getAddress() {
        if (TYPE_RECHAGE == transferType) {
            return getFromAddress();
        }
        if (TYPE_WITHDRAW == transferType) {
            return getToAddress();
        }
        return "";
    }

    public int getStatusColor(Context context) {
        switch (status) {
            //            case 1:
//                return "审核中";
//            case 2:
//                return "审核失败";
//            case 3:
//                return "待发送";
//            case 4:
//                return "未确认";
            case 5:
                return 0xFFC1C1C1;
            case 2:
            case 6:
                return 0xFFE52855;
            default:
                return Pub.getColor(context, R.attr.main_color);
        }
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }
}
