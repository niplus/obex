package com.fota.android.moudles.market.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CardFavorParamBean implements Serializable {

    int type;
    List<CardItemParamBean> data;

    public CardFavorParamBean(int type) {
        this.type = type;
        this.data = new ArrayList<>();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<CardItemParamBean> getData() {
        return data;
    }

    public void setData(List<CardItemParamBean> data) {
        this.data = data;
    }

}
