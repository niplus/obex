package com.fota.android.moudles.market.bean;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * market 下的合约类型
 * 比如 btc 当周
 * 还可能有 btc现货指数
 */
public class FutureItemEntity implements Serializable, Comparable<FutureItemEntity> {

    public FutureItemEntity(String futureName) {
        this.futureName = futureName;
        this.datas = new ArrayList<>();
    }

    /**
     *
     */
    private String futureName;
    //id 1001 etc
    private int entityId;
    private boolean isFavorite;
    private boolean isHot;
    private String lastPrice;
    private String uscPrice;

    public String getUscPrice() {
        return uscPrice;
    }

    public void setUscPrice(String uscPrice) {
        this.uscPrice = uscPrice;
    }

    private String trend;
    //1 index 2 future 3 usdt
    private int entityType;
    private String volume;

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

    //合约专有 - 代表 当周 1还是当月2还是当季3
    private int contractType;
    //合约专有 - 代表btc 1 or eth 2
    //在指数的情况下，本字段为空，但是在获取指数接口1408reqType的时候用来存储"btc"值了
    private String assetName;
    private long collectTime;

    public String getVolume() {
        if(TextUtils.isEmpty(volume) && getEntityType() != 1) {
            return "- -";
        }
        return volume;
    }

    public String getVolumeOrigin() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    //groupTitle与showBottomMargin跟分组有关
    private String groupTitle;
    //top
    private boolean showTopMargin;
    //bottom
    private boolean bottom;

    public boolean isShowTopMargin() {
        return showTopMargin;
    }

    public void setShowTopMargin(boolean showTopMargin) {
        this.showTopMargin = showTopMargin;
    }

    public boolean isBottom() {
        return bottom;
    }

    public void setBottom(boolean bottom) {
        this.bottom = bottom;
    }
    /**
     * 100条 max
     */
    private List<ChartLineEntity> datas;

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public String getFutureName() {
        return futureName;
    }

    public void setFutureName(String futureName) {
        this.futureName = futureName;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public List<ChartLineEntity> getDatas() {
        return datas;
    }

    public void setDatas(List<ChartLineEntity> datas) {
        this.datas = datas;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    public String getLastPrice() {
        if(TextUtils.isEmpty(lastPrice)) {
            return "--";
        }
        return lastPrice;
    }

    public String getLastPriceOrigin() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getTrend() {
        if(TextUtils.isEmpty(trend)) {
            return "--%";
        }
        return trend;
    }

    public String getTrendOrigin() {
        return trend;
    }

    public void setTrend(String trend) {
        if(StringUtils.isEmpty(trend) || trend.contains("null")) {
            this.trend = "";
            return;
        }
        this.trend = trend;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    @Override
    public String toString() {
        return "FutureItemEntity{" +
                "futureName='" + futureName + '\'' +
                ", entityId=" + entityId +
                ", isFavorite=" + isFavorite +
                ", isHot=" + isHot +
                ", lastPrice='" + lastPrice + '\'' +
                ", uscPrice='" + uscPrice + '\'' +
                ", trend='" + trend + '\'' +
                ", entityType=" + entityType +
                ", volume='" + volume + '\'' +
                ", contractType=" + contractType +
                ", assetName='" + assetName + '\'' +
                ", collectTime=" + collectTime +
                ", groupTitle='" + groupTitle + '\'' +
                ", showTopMargin=" + showTopMargin +
                ", bottom=" + bottom +
                ", datas=" + datas +
                '}';
    }

    @Override
    public int compareTo(@NonNull FutureItemEntity o) {
        int result = 0;
        if(o == null) {
            return result;
        }
        if (o.getCollectTime() < collectTime) {
            result = -1;
        } else if (o.getCollectTime() > collectTime) {
            result = 1;
        }
        return result;
    }
}
