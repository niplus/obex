package com.fota.android.moudles.mine.bean;

import android.content.Context;

import com.fota.android.R;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.TimeUtils;

import java.io.Serializable;
import java.util.List;

public class XianhuoChengjiaoBean implements Serializable {
    private int total;
    private int pageSize;
    private int pageNo;
    private List<XianhuoChengjiaoBeanItem> item;

    public static class XianhuoChengjiaoBeanItem implements Serializable {

        private String assetName;//标的物名称
        private int orderDirection;// 2-买，1-卖
        private long filledDate;//成交时间
        private String filledPrice;//成交价格
        private String filledAmount;//成交数量

        public String getAssetName() {
            return assetName;
        }

        public void setAssetName(String assetName) {
            this.assetName = assetName;
        }

        public int getOrderDirection() {
            return orderDirection;
        }

        public void setOrderDirection(int orderDirection) {
            this.orderDirection = orderDirection;
        }

        public long getFilledDate() {
            return filledDate;
        }

        public void setFilledDate(long filledDate) {
            this.filledDate = filledDate;
        }

        public String getFilledPrice() {
            return filledPrice;
        }

        public void setFilledPrice(String filledPrice) {
            this.filledPrice = filledPrice;
        }

        public String getFilledAmount() {
            return filledAmount;
        }

        public void setFilledAmount(String filledAmount) {
            this.filledAmount = filledAmount;
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
                return CommonUtils.getResouceString(context, R.string.common_buy2_short);
            } else {
                return CommonUtils.getResouceString(context, R.string.common_sell2_short);
            }
        }

        public String getFormatTime() {
            return TimeUtils.getDateToString(getFilledDate());
        }

        public String getFormatAssetName() {
            return getAssetName();
        }
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public List<XianhuoChengjiaoBeanItem> getItem() {
        return item;
    }

    public void setItem(List<XianhuoChengjiaoBeanItem> item) {
        this.item = item;
    }
}
