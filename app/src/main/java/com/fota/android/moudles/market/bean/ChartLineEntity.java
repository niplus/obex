package com.fota.android.moudles.market.bean;

import java.io.Serializable;

public class ChartLineEntity implements Serializable {
    private long time;
    private double open;
    private double close;
    private double high;
    private double low;
    private float volume;
    private int type;
    //add
//    private boolean up;
    //涨跌幅 其实并不需要
//    private String gain;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    //add 持仓量 该时间的所有玩家总持仓
    private double amount;
    //服务端返回，type = 2的时候，合约部分需要附带现货指数，k线才有

    public double getIndex() {
        return index;
    }

    public void setIndex(double index) {
        this.index = index;
    }

    private double index;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChartLineEntity{" +
                "time=" + time +
                ", open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", type=" + type +
                ", amount=" + amount +
                ", index=" + index +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ChartLineEntity)) {
            return false;
        }
        ChartLineEntity o = (ChartLineEntity)obj;

        if(obj == null) {
            return false;
        }

        return o.volume == volume && o.close == close && o.open == open && o.time == time;
    }
}
