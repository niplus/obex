package com.fota.android.moudles.market;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.fota.android.BuildConfig;
import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.app.SocketKey;
import com.fota.android.common.bean.home.BannerBean;
import com.fota.android.common.bean.home.NoticeBean;
import com.fota.android.commonlib.base.INetWork;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.ErrorCodeUtil;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.BtbMap;
import com.fota.android.http.Http;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.moudles.market.bean.MarketCardItemBean;
import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;
import com.fota.android.socket.params.SocketCardParam;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cn.udesk.emotion.LQREmotionKit.getContext;

/**
 * Created by jiang on 2018/08/02.
 */

public class MarketPresenter extends BasePresenter<MarketViewInterface> {
    private INetWork iNetWork;
    private boolean isInit = true;
    // 上一次 market 请求是错误的
//    private boolean lastNetError = false;

    private final static String TRADE_BANNER_JSON = "trade_banner_json";
    private String bannerString = "";
    private String noticeString = "";

    public void setInit(boolean init) {
        isInit = init;
    }

    public boolean isInit() {
        return isInit;
    }

    public MarketPresenter(MarketViewInterface view, INetWork iNetWork) {
        super(view);
        this.iNetWork = iNetWork;
    }

    @Override
    public void detachView() {
        super.detachView();
        client.removeChannel(SocketKey.HangQingKaPianReqType, this);
    }

    @Override
    public void onHide() {
        client.removeChannel(SocketKey.HangQingKaPianReqType, this);
    }

    /**
     * 获取 所有
     */
    public void getMarketCard() {
        final long currentTimeMillis = System.currentTimeMillis();
        final String endTime = currentTimeMillis + "";
        //15min
        String startTime = (currentTimeMillis - 48 * 15 * 60 * 1000) + "";

        Http.getMarketService().getMarketCards(createPageParam("1", startTime, endTime, 2))
                .compose(new CommonTransformer<List<MarketCardItemBean>>())
                .subscribe(new CommonSubscriber<List<MarketCardItemBean>>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(List<MarketCardItemBean> list) {
                        if (getView() != null) {
                            List<FutureItemEntity> result = dealFutureItemEntities(list);
                            if (isInit) {
                                getView().setMarketList(result);
                            } else {
                                List<FutureItemEntity> markets = FotaApplication.getInstance().getMarketsCardsList();
                                //记录本地的收藏时间 不能以服务器的为准
                                HashMap<String, Long> collectMap = new HashMap<>();
                                for (FutureItemEntity each2 : markets) {
                                    if (each2.isFavorite()) {
                                        String key = "";
                                        if (each2.getEntityType() == 2) {
                                            key = each2.getAssetName() + "-" + each2.getContractType();
                                        } else {
                                            key = each2.getEntityType() + "-" + each2.getEntityId();
                                        }
                                        collectMap.put(key, each2.getCollectTime());
                                    }
                                }
                                markets.clear();
                                for (FutureItemEntity each1 : result) {
                                    markets.add(each1);
                                    String key = "";
                                    if (each1.getEntityType() == 2) {
                                        key = each1.getAssetName() + "-" + each1.getContractType();
                                    } else {
                                        key = each1.getEntityType() + "-" + each1.getEntityId();
                                    }
                                    if (collectMap.containsKey(key)) {
                                        each1.setCollectTime(collectMap.get(key));
                                    }
                                }
                                getView().marketRefresh(false);
                            }
                            getView().onCompleteLoading();
                            isInit = false;
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        if (getView() != null) {
                            //clear
//                            List markets = FotaApplication.getInstance().getMarketsCardsList();
//                            markets.clear();

                            getView().onErrorDeal(e);
                            getView().onCompleteLoading();
                        }
                    }
                });
    }

    /**
     * @param list
     * @return
     */
    @NonNull
    private List<FutureItemEntity> dealFutureItemEntities(List<MarketCardItemBean> list) {
        WebSocketEntity<SocketCardParam> socketEntity = new WebSocketEntity<>();
        //订阅 全部的卡片推送
        socketEntity.setParam(new SocketCardParam(1, "2"));
        socketEntity.setReqType(SocketKey.HangQingKaPianReqType);
        client.addChannel(socketEntity, MarketPresenter.this);
        List<FutureItemEntity> result = new ArrayList<>();
        for (MarketCardItemBean each : list) {
            FutureItemEntity future = new FutureItemEntity(each.getName());
            future.setLastPrice(each.getLastPrice());
            future.setTrend(each.getGain());
            future.setContractType(each.getContractType());
            future.setAssetName(each.getAssetName());
            future.setUscPrice(each.getUscPrice());
            if (!StringUtils.isEmpty(each.getTotalVolume())) {
                future.setVolume(each.getTotalVolume());
            }

            future.setFavorite(each.isCollect());
            if (each.isCollect() && isInit) {
                future.setCollectTime(each.getCollectTime());
            }
            future.setHot(each.isFire());
            future.setEntityId(each.getId());
            future.setEntityType(each.getType());
            if (each.getLine() != null) {
                List<ChartLineEntity> line = each.getLine();
                for (int i = 0; i < line.size(); i++) {
                    ChartLineEntity tmp = line.get(i);
                    if (tmp == null)
                        continue;
                    // 有指数，但是没有对应节点的future-reverse-reset
                    if (tmp.getOpen() == 0 && tmp.getClose() == 0 && tmp.getVolume() == 0) {
                        if (i == 0 || line.get(i - 1) == null)
                            continue;
                        else {
                            ChartLineEntity entityAdd = new ChartLineEntity();
                            double value;
                            if (future.getDatas().size() > 0) {
                                int length = future.getDatas().size();
                                value = future.getDatas().get(length - 1).getClose();
                            } else {
                                value = line.get(i - 1).getClose();
                            }
                            entityAdd.setOpen(value);
                            entityAdd.setClose(value);
                            entityAdd.setHigh(value);
                            entityAdd.setLow(value);
                            entityAdd.setTime(tmp.getTime());
                            future.getDatas().add(entityAdd);
                        }
                    } else
                        future.getDatas().add(tmp);
                }
            }
//            if(future.getDatas().size() > 0) {
            result.add(future);
//            }
        }
        return result;
    }

    /**
     * type=1&startTime=1533649916415&endTime=1533653516415&resolution=1
     *
     * @param resolution 折线维度单位： 1-1分钟 ；15-15分钟； 60-1小时; D-1天
     * @param startTime
     * @param endTime
     * @param type       0-自选 1~8-全部~USDT（所有） 9-热门
     * @return
     */
    @NonNull
    private BtbMap createPageParam(String type, String startTime, String endTime, int resolution) {
        BtbMap paramsMap = new BtbMap();
        paramsMap.put("resolution", resolution + "");
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);
        paramsMap.put("type", type);
        return paramsMap;
    }

    @Override
    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        if (getView() != null) {//todo jiang 保持持续刷新
            getView().marketRefresh(true);
        }
    }


    //banner notice start

    /**
     * 存入banner列表到Json
     *
     * @param list
     */
    private void setDoing(final List<BannerBean> list) {
        //线程执行即可
        new Thread(new Runnable() {
            @Override
            public void run() {
                String listJson = new Gson().toJson(list);
                SPUtils.getInstance().put(TRADE_BANNER_JSON, listJson);
            }
        }).start();
    }

    /**
     * 从sp获取列表 banner
     */
    private List<BannerBean> getDoing() {
        String listJson = SPUtils.getInstance().getString(TRADE_BANNER_JSON);
        if (TextUtils.isEmpty(listJson)) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(listJson, new TypeToken<List<BannerBean>>() {
        }.getType());
    }

    /**
     * 获取缓存的活动
     */
    public void getDiskDoing() {
        List<BannerBean> entity = getDoing();
        if (entity != null && getView() != null) {
            getView().setDoingEntity(entity);
        }
    }


    /**
     * banner 数据
     */
    public void getBanner() {
        Http.getHttpService().banner()
                .compose(new CommonTransformer<List<BannerBean>>())
                .subscribe(new CommonSubscriber<List<BannerBean>>(getView()) {
                    @Override
                    public void onNext(List<BannerBean> bannerBean) {
                        if (getView() == null) {
                            return;
                        }
                        if (bannerString.equals(bannerBean.toString()))
                            return;
                        bannerString = bannerBean.toString();
                        LogUtils.a("home", "dbanner " + bannerBean.toString());
                        getView().setDoingEntity(bannerBean);
                        setDoing(bannerBean);
                    }

                    @Override
                    protected void onError(ApiException e) {
                        if (getView() != null && showLoading()) {
                            getView().stopProgressDialog();
                        }

                        showErrorBannerNotice(e);
                        LogUtils.a("home", "dbanner fail" + e);
                    }

                    @Override
                    protected boolean showLoading() {
                        return false;
                    }
                });
    }

    public void getNotice() {
        Http.getHttpService().getNotic()
                .compose(new CommonTransformer<List<NoticeBean>>())
                .subscribe(new CommonSubscriber<List<NoticeBean>>(getView()) {
                    @Override
                    public void onNext(List<NoticeBean> noticeBean) {
                        LogUtils.a("home", "notice result " + noticeBean.toString());
                        if (getView() == null) {
                            return;
                        }
                        if (Pub.isListExists(noticeBean)) {
                            if (noticeString.equals(noticeBean.toString()))
                                return;
                            noticeString = noticeBean.toString();
                            getView().setNotice(noticeBean);

                        } else {
                            getView().setNotice(null);
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        if (getView() != null && showLoading()) {
                            getView().stopProgressDialog();
                        }
                        showErrorBannerNotice(e);
                        LogUtils.a("home", "notice result fail" + e);
                    }

                    @Override
                    protected boolean showLoading() {
                        return false;
                    }

                });
    }

    public void showErrorBannerNotice(ApiException e) {
        if (getView() == null) {
            return;
        }

        String errorMsg = ErrorCodeUtil.getInstance().getCodeMsg(e.code, e.message);
        if (!TextUtils.isEmpty(errorMsg)) {
            e.message = errorMsg;
        } else {
            if (BuildConfig.DEBUG) {
                e.message = e.message + "  fortest";
            }
        }
        if (Pub.isStringEmpty(e.message)) {
            e.message = CommonUtils.getResouceString(getContext(), R.string.common_data_error);
        }
        getView().showToast(e.message);
    }
}
