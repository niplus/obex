package com.fota.android.common.bean;

import com.fota.android.common.bean.home.EntrustBean;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.guoziwei.fota.model.HisData;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BeanChangeFactory {

    /**
     * @param addtional    进来的数据
     * @param dynamicDatas 容器
     * @param tradeMap     暂存器
     * @param bigEndian    是否大的在前
     *                     Map<Double, Double>  key price value total
     */
    public static void combine(List<EntrustBean> addtional, List<EntrustBean> dynamicDatas, HashMap<Double, EntrustBean>
            tradeMap, final boolean bigEndian) {
        refreshMapFromList(addtional, tradeMap);
        dynamicDatas.clear();
        //confimDelete addchange sort
        for (Iterator<Map.Entry<Double, EntrustBean>> it = tradeMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Double, EntrustBean> item = it.next();
            //1.0E-20
            EntrustBean entrustBean = item.getValue();
            if (entrustBean.getDoubleAmount() == 0.0 || Pub.doubleIsEqualZero(entrustBean.getDoubleAmount()))
                it.remove();
            else if (item.getKey() == 0.0) {
                it.remove();
            } else {
                //may have bug
                //                each.setPrice(item.getKey());
                //                each.setDoulePrice(item.getKey(), priceDigital);
                //                each.setTotalSize(item.getValue());
                //                each.setDoubleTotalSize(item.getValue(), totalDigital);
                dynamicDatas.add(entrustBean);
            }
        }
        Collections.sort(dynamicDatas, new Comparator<EntrustBean>() {
            @Override
            public int compare(EntrustBean o1, EntrustBean o2) {
                int result = 0;
                if (o1.getDoulePrice() < o2.getDoulePrice()) {
                    if (bigEndian)
                        result = 1;
                    else
                        result = -1;
                } else if (o1.getDoulePrice() > o2.getDoulePrice()) {
                    if (bigEndian)
                        result = -1;
                    else
                        result = 1;
                }
                return result;
            }
        });

        tradeMap.clear();
        refreshMapFromList(dynamicDatas, tradeMap);
    }

    private static void refreshMapFromList(List<EntrustBean> datas, HashMap<Double, EntrustBean> tradeMap) {
        for (EntrustBean each : datas) {
            tradeMap.put(each.getDoulePrice(), each);
        }
    }

    public static List<EntrustBean> getEntrustBeans(List<EntrustBean> limits, int max) {
        List<EntrustBean> results = null;
        if (limits == null) {
            return results;
        }
        List<EntrustBean> dynamicSellDatas = limits;
        int length = dynamicSellDatas.size();
        if (length >= max) {
            results = dynamicSellDatas.subList(0, max);
        } else {
            results = dynamicSellDatas;
        }
        return results;
    }

    public static List<EntrustBean> getSellEntrustBeans(List<EntrustBean> limits, int max) {
        List<EntrustBean> results = null;
        if (limits == null) {
            return results;
        }
        List<EntrustBean> dynamicSellDatas = limits;
        int length = dynamicSellDatas.size();
        if (length >= max) {
            results = dynamicSellDatas.subList(length - max, length);
        } else {
            results = dynamicSellDatas;
        }
        return results;
    }

//    public static int findMinSafeNum(MarketTimeLineBean bean) {
//        int result = 0;
//        if (bean.getTime() == null) {
//            return result;
//        }
//        result = bean.getTime().size();
//        if (bean.getPrice() != null && result > bean.getPrice().size()) {
//            result = bean.getPrice().size();
//        }
//        if (bean.getSpotIndex() != null && result > bean.getSpotIndex().size()) {
//            result = bean.getSpotIndex().size();
//        }
//
//        return result;
//    }

    /**
     * @param datas 需要填入的数据，future or usdt or index
     * @param spots 需要填入的数据，future情况下，这个用来存储index
     * @param type  type == 2 是future，才需要填充spot
     * @param line  获取的数据
     */
    public static void iterateKDataList(List<ChartLineEntity> datas, List<ChartLineEntity> spots, int type, List<ChartLineEntity> line) {
        if (line != null) {
            for (int i = 0; i < line.size(); i++) {
                ChartLineEntity entity = line.get(i);
                if (entity == null)
                    continue;
                if (type == 2 || type == 3) {//index 有值
                    ChartLineEntity entitySpot = new ChartLineEntity();
                    entitySpot.setClose(entity.getIndex());
                    entitySpot.setTime(entity.getTime());
                    spots.add(entitySpot);
                    // 有指数，但是没有对应节点的future-reverse-reset
                    if (entity.getOpen() == 0 && entity.getClose() == 0 && entity.getVolume() == 0) {
                        if (i == 0 || line.get(i - 1) == null)
                            continue;
                        else {
                            double value;
                            if (datas.size() > 0) {
                                int length = datas.size();
                                value = datas.get(length - 1).getClose();
                            } else {
                                value = line.get(i - 1).getClose();
                            }
                            entity.setOpen(value);
                            entity.setClose(value);
                            entity.setHigh(value);
                            entity.setLow(value);
                        }
                    }
                } else if (type == 1) {//spot 值在time和index里
                    ChartLineEntity entitySpot = new ChartLineEntity();
                    entitySpot.setClose(entity.getIndex());
                    entitySpot.setTime(entity.getTime());
                    spots.add(entitySpot);
                }
                datas.add(entity);
            }
        }
    }

    public static void iterateSocketDataList(List<ChartLineEntity> datas, List<ChartLineEntity> spots, int type, List<ChartLineEntity> line) {
        if (line != null) {
            for (int i = 0; i < line.size(); i++) {
                ChartLineEntity entity = line.get(i);
                if (entity == null)
                    continue;
                if (type != 3) {//index 有值
                    ChartLineEntity entitySpot = new ChartLineEntity();
                    entitySpot.setClose(entity.getIndex());
                    entitySpot.setTime(entity.getTime());
                    spots.add(entitySpot);
                }
                datas.add(entity);
            }
        }
    }

    public static HisData createNewHisData(ChartLineEntity m) {
        HisData data = new HisData();

        if (m == null) {
            return data;
        }
        data.setClose(m.getClose());
        data.setOpen(m.getOpen());
        data.setHigh(m.getHigh());
        data.setLow(m.getLow());
        data.setVol(m.getVolume());
        data.setHolding(m.getAmount());

        data.setDate(m.getTime());
        return data;
    }

}
