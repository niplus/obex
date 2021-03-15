package com.fota.android.common.bean.wallet;

import com.fota.android.core.base.ft.FtKeyValue;

import java.io.Serializable;

public class CoinBean implements Serializable, FtKeyValue {

    public CoinBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    String id;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getKey() {
        return name;
    }

    @Override
    public String getValue() {
        return id;
    }
}
