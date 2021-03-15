package com.fota.android.common.bean.home;

import java.io.Serializable;

/**
 * 热门合约
 */
//"data": [
//        {
//        "name": "",
//        "gain": "",
//        "price": "",
//        "total": "",
//        "type": 1,
//        "id": ""
//        }
//        ]
public class HotContractBean implements Serializable {
    private String name;
    private String gain;
    private String price;
    private String total;
    private Integer type;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGain() {
        return gain;
    }

    public void setGain(String gain) {
        this.gain = gain;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "HotContractBean{" +
                "name='" + name + '\'' +
                ", gain='" + gain + '\'' +
                ", price='" + price + '\'' +
                ", total='" + total + '\'' +
                ", type=" + type +
                ", id='" + id + '\'' +
                '}';
    }
}
