package com.fota.android.moudles.mine.bean;

import java.io.Serializable;

/**
 * 期权可下注币种
 */
public class OptionCoinBean implements Serializable {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
