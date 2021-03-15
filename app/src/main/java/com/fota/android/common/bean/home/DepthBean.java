package com.fota.android.common.bean.home;

import com.blankj.utilcode.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DepthBean implements Serializable {
    //sell
    private List<EntrustBean> asks;
    //buy
    private List<EntrustBean> bids;

    /**
     * usdk or future 的 id
     */
    private int id;

    /**
     * type 3 usdt 2 future
     */
    private int type;

    //改为string 了--改为string的list了
    //再改回去string
    private String pricision;

    private Float valuation;

    public Float getValuation() {
        return valuation;
    }

    public void setValuation(Float valuation) {
        this.valuation = valuation;
    }

    public List<EntrustBean> getAsks() {
        return asks;
    }

    public void setAsks(List<EntrustBean> asks) {
        this.asks = asks;
    }

    public List<EntrustBean> getBids() {
        return bids;
    }

    public void setBids(List<EntrustBean> bids) {
        this.bids = bids;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getPricisionList() {
        List<String> result = new ArrayList<>();
        if(StringUtils.isEmpty(pricision)) {
            result.add("0.1");
            result.add("1");
        } else {
            String[] temp = pricision.split(",");
            //给的反序
            for(int i=0;i<temp.length;i++) {
                result.add(temp[i]);
            }
        }

        return result;
//        return pricision;
    }

//    public Integer[] getPricisionArray() {
//        Integer[] result = new Integer[2];
//        if(StringUtils.isEmpty(pricision)) {
//            result[0] = 1;
//            result[1] = 2;
//        } else {
//            String[] temp = pricision.split(",");
//            //服务端返回了反的顺序
//            for(int i=temp.length-1,j=0;i>=0;i--,j++) {
//                Integer integer = Integer.parseInt(temp[i]);
//                result[j] = integer;
//            }
//        }
//
//        return result;
//    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String currentPricision = "";//当前的小数位

    public boolean isEquals(String comparePrecision) {
        if("-1".equals(comparePrecision)) {
            return currentPricision.equals(getPricisionList().get(0));
        } else {
            return currentPricision.equals(comparePrecision);
        }
    }
}
