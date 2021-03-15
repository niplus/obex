package com.fota.android.moudles.mine.bean;

import com.fota.android.commonlib.utils.TimeUtils;
import com.fota.option.common.StringConstant;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 期权记录
 */
public class OptionHisBean implements Serializable {
    private int total;
    private int pageSize;
    private int pageNo;
    private List<OptionHisBeanItem> item;

    public static class OptionHisBeanItem implements Serializable {
        private String asset;
        private double price;//投资
        private double profit;//收益
        private long tradingTime;//日期
        private int currency;

        private String iconUrl;
        private String currencyName;

        public int getCurrency() {
            return currency;
        }

        public void setCurrency(int currency) {
            this.currency = currency;
        }

        public String getCurrencyName() {
            String result = "";
            if(currency != StringConstant.ACCOUNT_VFOTA_ID) {
                result = "(" + currencyName + ")";
            }
            return result;
        }

        public String getIconUrl() {
            return iconUrl == null ? "" : iconUrl;
        }

//        public int getIconID() {
//            return StringConstant.getIconID(currency);
//        }
//
//        public String getUnit() {
//            String unit = StringConstant.getUnit(currency);
//            String result = "";
//            if(currency != StringConstant.ACCOUNT_VFOTA_ID) {
//                result = "(" + unit + ")";
//            }
//            return result;
//        }

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        private String formatDouble(double d) {
//            d = d < 0 ? -1 * d : d;

            DecimalFormat dcmFmt = new DecimalFormat("0.00000000");
            // 取消科学计数法
            dcmFmt.setGroupingUsed(false);
            //返回结果
            return dcmFmt.format(d);
        }

        public String getDate() {
            if (tradingTime > 0) {
                return TimeUtils.getDateToString(tradingTime);
            } else {
                return "";
            }
        }

        private String priceDesc;
        private String profitDesc;
        private String equityDesc;
        public String getPrice() {
//            return formatDouble(price);
            return priceDesc;
        }

        public String getProfit() {
//        formatDouble(profit);
//            return formatDouble(profit);
            return profitDesc;
        }

        //新增 字段
        private String directionDesc;//交易方向
        private String optionTypeDesc;//期权品种

        private String spotIndexDesc;//现货指数
        private String strikePriceDesc;//行权价格
        private String settlementPriceDesc;//结算价格

        /**
         * @return
         * 所谓 价值金额
         */
        public String getCalAmountPrice() {
//            return formatDouble(profit+price);
            return equityDesc;
        }

        public String getDirectionDesc() {
            return directionDesc;
        }

        public void setDirectionDesc(String directionDesc) {
            this.directionDesc = directionDesc;
        }

        public String getOptionTypeDesc() {
            return optionTypeDesc;
        }

        public void setOptionTypeDesc(String optionTypeDesc) {
            this.optionTypeDesc = optionTypeDesc;
        }

        public String getSpotIndex() {
            return spotIndexDesc;
        }

        public void setSpotIndex(String spotIndex) {
            this.spotIndexDesc = spotIndex;
        }

        public String getStrikePrice() {
            return strikePriceDesc;
        }

        public void setStrikePrice(String strikePrice) {
            this.strikePriceDesc = strikePrice;
        }

        public String getSettlementPriceDesc() {
            return settlementPriceDesc;
        }

        public void setSettlementPriceDesc(String settlementPriceDesc) {
            this.settlementPriceDesc = settlementPriceDesc;
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

    public List<OptionHisBeanItem> getItem() {
        return item;
    }

    public void setItem(List<OptionHisBeanItem> item) {
        this.item = item;
    }
}
