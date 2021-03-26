package com.fota.android.moudles.market;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fota.android.R;
import com.fota.android.app.Constants;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.common.bean.BeanChangeFactory;
import com.fota.android.common.bean.exchange.CurrentPriceBean;
import com.fota.android.common.bean.exchange.ExchangeCurrency;
import com.fota.android.common.bean.home.DealBean;
import com.fota.android.common.bean.home.EntrustBean;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.commonlib.utils.URLUtils;
import com.fota.android.core.base.MvpActivity;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.databinding.ActivityMarketsChartsBinding;
import com.fota.android.moudles.common.DealCanNullAdapter;
import com.fota.android.moudles.futures.FutureContractBean;
import com.fota.android.moudles.main.MainActivity;
import com.fota.android.moudles.main.bean.BundleForTradeEntity;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.fota.android.moudles.market.bean.CoinIndexOutBean;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.moudles.market.bean.HoldingEntity;
import com.fota.android.moudles.market.bean.MarketIndexBean;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.StatusBarUtil;
import com.fota.android.widget.DepthRefreshView;
import com.fota.android.widget.KlineTitleBarLayout;
import com.fota.android.widget.NoDoubleClickListener;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;
import com.guoziwei.fota.chart.OnLoadEdgeListener;
import com.guoziwei.fota.chart.view.BaseChartView;
import com.guoziwei.fota.chart.view.fota.FotaBigKLineBarChartView;
import com.guoziwei.fota.chart.view.fota.FotaBigTimeLineBarChartView;
import com.guoziwei.fota.model.HisData;

import java.util.ArrayList;
import java.util.List;

/**
 * 行情页面点击具体币对应的合约类型，
 * 进入该合约的k线界面，包括下方的成交和挂单
 * Created by jiang on 2018/08/02.
 */

public class TradeMarketKlineActivity extends MvpActivity<TradeMarketKlinePresenter>
        implements TradeMarketKlineViewInterface,
        BaseChartView.KlinePeriodInterface, KlineTitleBarLayout.OnRightImage2ClickListener,
        KlineTitleBarLayout.OnRightImage1ClickListener, DepthRefreshView.DepthRefreshViewListener {

    ActivityMarketsChartsBinding mBinding;

    @Override
    protected TradeMarketKlinePresenter createPresenter() {
        return new TradeMarketKlinePresenter(this);
    }

    //标题
    private String futureStr;
    private int entityId;
    private int entityType;
    //
    private String lastDays;

    //已选择的kline 周期参数
    private int currentPeriodIndex = 2;

    private KlineTitleBarLayout mKlineTabBar;
    private boolean isKline;

    private DepthRefreshView mDepthTickerView;

    /**
     * 合约数据or usdt兑换
     * K
     */
    final List<HisData> klineData = new ArrayList<>(100);

    /**
     * 现货指数数据
     * Spot--K
     */
    final List<HisData> spotData = new ArrayList<>(100);
//    static final String[] types = {"1m", "15m", "1h", "1d", };
//    static final String[] dateFormats = {"HH:mm", "HH:mm", "HH:mm", "yyyy-MM-dd"};
    static final String[] types = {"1m", "5m", "15m", "30m", "1h", "4h", "6h", "1d", "1w"};
    static final String[] dateFormats = {"HH:mm", "HH:mm", "HH:mm", "HH:mm", "HH:mm", "HH:mm", "HH:mm", "MM-dd", "yyyy-MM-dd"};
    private DealCanNullAdapter<DealBean, ViewHolder> adapter;
    private EasyAdapter<CoinIndexOutBean, ViewHolder> adapterSpot;
    RecyclerView listDealSpot;
    /**
     * 合约数据or usdt兑换
     * T 15m
     */
    final List<HisData> time15Data = new ArrayList<>(100);

    /**
     * 现货指数数据
     * Spot -- T 15m
     */
    final List<HisData> spot15Data = new ArrayList<>(100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_markets_charts);
        //原有框架有坑，调用addFragment之后会重新走getExtras
        //所以采用这个方法
        getBundles();

        mKlineTabBar = (KlineTitleBarLayout) mBinding.barMarketsKline;
        StatusBarUtil.setPaddingSmart(getContext(), mKlineTabBar);
        String title = StringUtils.isEmpty(futureStr) ? "" : futureStr;

        mBinding.barMarketsKline.setTitle(title);
        mBinding.barMarketsKline.setmLeftClickListener(new KlineTitleBarLayout.OnLeftClickListener() {
            @Override
            public void onLeftClick(View v) {
                finish();
            }
        });

        initViews();
    }

    private void getBundles() {
        futureStr = getParams("symbol");
        entityId = getParams("id");
        entityType = getParams("type");
        FutureItemEntity entity = new FutureItemEntity(futureStr);
        if (entityType == 1) {
            entity.setAssetName(futureStr);
            futureStr += getResources().getString(R.string.market_index);
        }

        entity.setEntityId(entityId);
        entity.setEntityType(entityType);
        getPresenter().setBean(entity);
    }

    private void initViews() {
//        mBinding.tline.setDateFormat(dateFormats[1]);
        mBinding.kline.setDateFormat(dateFormats[1]);
        mBinding.kline.setOnEdgeListener(new OnLoadEdgeListener() {
            @Override
            public void onEdgeTouch(boolean leftEdge, boolean rightEdge) {
                if (leftEdge) {
                    showToast(R.string.market_k_nomore);
                }
            }

            @Override
            public void onScale(boolean isMax, boolean isMin) {
                if (isMin) {
                    showToast(R.string.market_k_min);
                } else if (isMax) {
                    showToast(R.string.market_k_max);
                }
            }
        });

        initHeadBar();
        mDepthTickerView = mBinding.depthRightContainer;
        mDepthTickerView.setType(entityType, futureStr);
        if (entityType == 2) {
            mDepthTickerView.setUnit(2);
        }
        mDepthTickerView.setListener(this);
        initChoosePopWindow();
        initTradeButtonClick();

        mBinding.listDeal.post(new Runnable() {
            @Override
            public void run() {
                initDealPartView();
            }
        });
    }

    private void initDealPartView() {
        int nums = computeDealRows();
        getPresenter().setDealNums(nums);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false) {

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mBinding.listDeal.setLayoutManager(linearLayoutManager);
        adapter = new DealCanNullAdapter<DealBean, ViewHolder>(getContext(), R.layout.item_deal_normal, nums) {
            @Override
            public void convert(ViewHolder holder, DealBean model, int position) {
                convertDealItemView(holder, model, position);
            }
        };
        mBinding.listDeal.setAdapter(adapter);

        if (entityType == 1) {
            LinearLayoutManager linearLayoutManagerSpot = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false) {

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            mBinding.indexContainer.setVisibility(View.VISIBLE);
            mBinding.dealLeftContainer.setVisibility(View.GONE);
            mBinding.depthRightContainer.setVisibility(View.GONE);
            listDealSpot = mBinding.indexContainer.findViewById(R.id.list_index);
            listDealSpot.setLayoutManager(linearLayoutManagerSpot);
            TextView more = mBinding.indexContainer.findViewById(R.id.txt_click_for_more);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FtRounts.toWebView(getContext(), getString(R.string.mine_help), AppConfigs.isChinaLanguage() ? Constants.URL_SPOT_INDEX_HELP_CN : Constants.URL_SPOT_INDEX_HELP_EN);
                }
            });
            adapterSpot = new EasyAdapter<CoinIndexOutBean, ViewHolder>(getContext(), R.layout.item_spot_normal) {
                @Override
                public void convert(ViewHolder holder, CoinIndexOutBean model, int position) {
                    View rootView = holder.getConvertView();
                    if (rootView != null) {
                        if (position % 2 != 0) {
                            rootView.setBackgroundColor(Pub.getColor(getContext(), R.attr.bg_color2));
                        } else {
                            rootView.setBackgroundColor(Pub.getColor(getContext(), R.attr.bg_color));
                        }
                    }
                    ImageView imageView = holder.getConvertView().findViewById(R.id.img_icon_out);
                    if (model == null) {
                        holder.setText(R.id.txt_out_paltform, "--");
                        holder.setText(R.id.txt_out_price, "--");
                        return;
                    }
                    if (!TextUtils.isEmpty(model.getIconUrl())) {
                        Glide.with(getContext()).load(URLUtils.getFullPath(model.getIconUrl())).apply(new RequestOptions()
                                .placeholder(R.mipmap.icon_logo)
                                .error(R.mipmap.icon_logo))
                                .into(imageView);
                    }
                    holder.setText(R.id.txt_out_paltform, AppConfigs.isChinaLanguage() ? model.getCnName() : model.getEnName());
                    holder.setText(R.id.txt_out_price, model.getPrice() == null ? "--" : model.getPrice());
                }
            };
            listDealSpot.setAdapter(adapterSpot);
        } else if (entityType == 2) {
            mBinding.txtVolume.setText(getString(R.string.market_amount_unit2));
        }
        //init -- 占位
        adapter.putList(null);
    }

    private int computeDealRows() {
        int height = mBinding.listDeal.getHeight();
        int each = UIUtil.dip2px(getContext(), 16);
        return height / each;
    }

    private void convertDealItemView(ViewHolder holder, DealBean model, int position) {
        if (model == null) {
            holder.setText(R.id.txt_time, Constants.NONE);
            holder.setText(R.id.txt_price, Constants.NONE);
            holder.setText(R.id.txt_volume, Constants.NONE);
            holder.setTextColor(R.id.txt_price, AppConfigs.getUpColor());
            holder.setTextColor(R.id.txt_volume, AppConfigs.getUpColor());
            return;
        }

        holder.setText(R.id.txt_time, model.getTime());
        holder.setText(R.id.txt_price, StringUtils.isEmpty(model.getPrice()) ? Constants.NONE : model.getPrice());
        holder.setText(R.id.txt_volume, StringUtils.isEmpty(model.getAmount()) ? Constants.NONE : model.getAmount());
        holder.setTextColor(R.id.txt_price, model.getMatchType() == 1 ? AppConfigs.getUpColor() : AppConfigs.getDownColor());
        holder.setTextColor(R.id.txt_volume, model.getMatchType() == 1 ? AppConfigs.getUpColor() : AppConfigs.getDownColor());
    }


    @Override
    public void onClickItem(EntrustBean bean) {
    }

    @Override
    public void onTickClick(CurrentPriceBean currentPrice) {
    }

    @Override
    public void onRefreshDigital(String remove, String add) {
        getPresenter().changeDigitalChannel(remove, add);
    }

    private void initHeadBar() {
        mKlineTabBar.setmRightImage2ClickListener(this);
        mKlineTabBar.setmRightImage1ClickListener(this);
        if (entityType == 1) {
            mKlineTabBar.setIvRightTypeVisible(false);
        }
    }

    private void initChoosePopWindow() {
        mBinding.kline.setPeriodInterface(this);
    }

    /**
     * 买入卖出的按钮点击事件
     */
    private void initTradeButtonClick() {
        if (entityType == 1) {
            mBinding.btnBuy.setVisibility(View.GONE);
            mBinding.btnSell.setVisibility(View.GONE);
            return;
        } else if (entityType == 2) {
            mBinding.btnBuy.setText(getString(R.string.exchange_buy_wishheigh));
            mBinding.btnSell.setText(getString(R.string.exchange_sell_wishlow));
        }
        UIUtil.setBtnShapeBgColor(mBinding.btnBuy, AppConfigs.getUpColor());
        UIUtil.setBtnShapeBgColor(mBinding.btnSell, AppConfigs.getDownColor());
        mBinding.btnBuy.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                //entityType 正好跟index相同
                toMain(entityType, true);
            }
        });

        mBinding.btnSell.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                //entityType 正好跟index相同
                toMain(entityType, false);
            }
        });
    }

    public void toMain(int index, boolean isBuy) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();

        BundleForTradeEntity trade = new BundleForTradeEntity();
        if (index == 3) {
            //这里是跳兑换的
            String temp = futureStr;
            String[] pair = futureStr.split("/");
            temp = pair[0];
            ExchangeCurrency exchangeCurrency = new ExchangeCurrency();
            exchangeCurrency.setAssetId(String.valueOf(entityId));
            exchangeCurrency.setAssetName(temp);
            trade.setBundleForExchange(exchangeCurrency);
            trade.setBuy(isBuy);
            bundle.putSerializable("trade", trade);
            intent.putExtras(bundle);
            finish();
            SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.ExchangeFragment, bundle);
        } else {
            //这里是跳交易的
            FutureContractBean future = new FutureContractBean();
            future.setContractId(entityId + "");
            future.setContractName(futureStr);
            trade.setBundleForFuture(future);
            trade.setFutureCoinName(Pub.fetchConiName(futureStr));
            trade.setBuy(isBuy);
            bundle.putSerializable("trade", trade);
            intent.putExtras(bundle);
            finish();
            SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.FuturesFragment, bundle);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        if (entityType == 2) {
            mBinding.kline.setChartType(FotaBigKLineBarChartView.ChartType.FUTURE);
            mBinding.tline.setChartType(FotaBigTimeLineBarChartView.ChartType.FUTURE);
        } else {

            mBinding.kline.setChartType(FotaBigKLineBarChartView.ChartType.USDT);
            mBinding.tline.setChartType(entityType == 1 ? FotaBigTimeLineBarChartView.ChartType.SPOT : FotaBigTimeLineBarChartView.ChartType.USDT);
            if (entityType == 3) {
                if (!TextUtils.isEmpty(futureStr)) {
                    String temp = futureStr;
                    if (futureStr.contains("/")) {
                        int length = futureStr.indexOf("/");
                        temp = futureStr.substring(0, length);
                    }
                    mBinding.tline.setBiaodi(temp);
                }
            }
        }
        //
        getPresenter().getTimeLineDatas(entityType, entityId, "1m");
        setKlineLoading(mBinding.tline, true);

        if (entityType != 1) {
            getPresenter().getDeal();
        } else {
            getPresenter().getAdditonalSpot();
        }

        //
        //getKlineDatas
        if (entityType != 1) {
            getPresenter().getDepthFive(entityType, entityId, "-1");
            mBinding.dealLeftContainer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPresenter().getKlineDatas(entityType, entityId, types[currentPeriodIndex]);
                }
            }, 100);
            getPresenter().getNowTicker(entityType, entityId);
        }
    }

    /**
     * refreshList socket addition update data
     */
    //socket service callback
    @Override
    public void onRefreshTimelineData() {
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromTimesByType(entityType);
        time15Data.clear();
        if (chartList == null) {
            return;
        }
        for (int i = 0; i < chartList.size(); i++) {
            ChartLineEntity m = chartList.get(i);
            if (m == null) {
                continue;
            }
            HisData data = BeanChangeFactory.createNewHisData(m);
            time15Data.add(data);
        }

        if (entityType == 2) {
            List<ChartLineEntity> spotList = FotaApplication.getInstance().getListFromTimesByType(1);
            spot15Data.clear();
            for (int i = 0; i < spotList.size(); i++) {
                ChartLineEntity m = spotList.get(i);
                HisData data = BeanChangeFactory.createNewHisData(m);
                spot15Data.add(data);
            }
        }
        postRefreshTimeChart();
    }

    //socket service callback
    @Override
    public void onRefreshTicker(final CurrentPriceBean refreshTicker) {
        if (mBinding.depthRightContainer == null) {
            return;
        }
        mBinding.depthRightContainer.setTickInfo(refreshTicker);
    }

    //socket service callback

    /**
     * @param buys
     * @param sells socket service callback
     * @param dollarEvaluation
     */
    @Override
    public void onRefreshDepth(List<EntrustBean> buys, List<EntrustBean> sells, List<String> precisions, Float dollarEvaluation) {
        if (mDepthTickerView != null) {
            if (precisions != null) {
                mBinding.depthRightContainer.setSmallLargePrecision(precisions);
            }
//            mBinding.depthRightContainer.setDollarEvaluation(dollarEvaluation);
            mDepthTickerView.refreshDepth(buys, sells);
        }
    }

    //socket service callback
    @Override
    public void onRefreshDeal(List<DealBean> dealList) {
        if (adapter != null) {
            adapter.putList(dealList);
        }
    }

    /**
     * @param indexBean 刷新左侧 spot deal数据
     */
    @Override
    public void onRefreshSpot(MarketIndexBean indexBean) {
        TextView tvAvePrice = mBinding.indexContainer.findViewById(R.id.tv_ave_price);
        if (tvAvePrice != null) {
            tvAvePrice.setText(indexBean.getAveragePrice());
        }
        TextView tvRate = mBinding.indexContainer.findViewById(R.id.tv_exchange_rate);
        if (tvRate != null) {
            tvRate.setText(indexBean.getUsdtRate());
        }
        TextView tvSpotIndex = mBinding.indexContainer.findViewById(R.id.tv_spot_index);
        if (tvSpotIndex != null) {
            tvSpotIndex.setText(indexBean.getUsdtSpotIndex());
        }
        if (adapterSpot != null && indexBean.getCoinList() != null) {
            adapterSpot.putList(indexBean.getCoinList());
            int count = indexBean.getCoinList().size();
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) listDealSpot.getLayoutParams();
            lp.height = count * UIUtil.dip2px(getContext(), 25);
            listDealSpot.setLayoutParams(lp);
        }
    }

    /**
     * list get requeset reset data
     * entityType 都会走此分支
     */
    @Override
    public void resetFreshTimelineData() {
        if (entityType == 2) {
            setDelievery();
        }
        if (isKline) {
            return;
        }

        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromTimesByType(entityType);
        if (chartList == null) {
            onNoDataCallBack(0);
            return;
        }
        time15Data.clear();
        for (int i = 0; i < chartList.size(); i++) {
            ChartLineEntity m = chartList.get(i);
            if (m == null) {
                continue;
            }
            HisData data = BeanChangeFactory.createNewHisData(m);
            time15Data.add(data);
        }

        if (entityType == 2) {
            List<ChartLineEntity> spotList = FotaApplication.getInstance().getListFromTimesByType(1);
            spot15Data.clear();
            if (spotList != null) {
                for (int i = 0; i < spotList.size(); i++) {
                    ChartLineEntity m = spotList.get(i);
                    HisData data = BeanChangeFactory.createNewHisData(m);
                    spot15Data.add(data);
                }
            }
        }
        postRefreshTimeChart();
    }

    private void postRefreshTimeChart() {
        if (entityType != 1) {
            mKlineTabBar.setIvRightTypeVisible(true);
        }
        HoldingEntity entity = FotaApplication.getInstance().getHoldingEntity();
        mBinding.tline.setmDigits(entity.getDecimal());
        double holdingPrice = entity.getHoldingPrice();
        String holdingStr = entity.getHoldingDescription();
        if (entityType == 2) {
//            mBinding.tline.initData(time15Data, spot15Data);
            //合约去掉现货指数
            mBinding.tline.initData(time15Data, null);
        } else {
            mBinding.tline.initData(time15Data);
        }
        if (time15Data != null && time15Data.size() > 0) {
            double hour24Close = time15Data.get(0).getClose();
            if (holdingPrice > 0) {
                mBinding.tline.setLimitLine(holdingPrice, holdingStr);
            }
            mBinding.tline.setLastClose(hour24Close);
        }
    }

    @Override
    public void resetFreshKlineData() {
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromKlinesByType(entityType);
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        if (chartList == null) {
            onNoDataCallBack(1);
            return;
        }
        klineDataConvert(chartList);
        mBinding.kline.setNeedMoveToLast(true);
        mBinding.kline.setmDigits(holdingEntity.getDecimal());
        if (entityType == 2) {
//            mBinding.kline.initData(klineData, spotData);
            mBinding.kline.initData(klineData, null);
        } else {
            mBinding.kline.initData(klineData);
        }
        if (holdingEntity != null && holdingEntity.getHoldingPrice() != -1) {
            mBinding.kline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
        }
        if (time15Data != null && time15Data.size() > 0) {
            double hour24Close = time15Data.get(0).getClose();
            mBinding.kline.setLastClose(hour24Close);
        }
    }

    @Override
    public void setOverShowLoading(int charType, boolean isShowLoading) {
        if (charType == 0) {
            setKlineLoading(mBinding.tline, isShowLoading);
        } else {
            setKlineLoading(mBinding.kline, isShowLoading);
        }
    }

    @Override
    public void onNoDataCallBack(int charType) {
        if (charType == 0) {
            mBinding.tline.initData(null, null);
        } else {
            mBinding.kline.initData(null, null);
        }
    }

    private void setKlineLoading(BaseChartView view, boolean isShowLoading) {
        if (view != null) {
            view.setLoading(isShowLoading);
        }
    }

    @Override
    public void onRefreshDeliveryOrHold() {
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        if (holdingEntity != null && holdingEntity.getHoldingPrice() != -1) {
            mBinding.kline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
            mBinding.tline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
        }
        setDelievery();

        entityId = holdingEntity.getId();
        futureStr = holdingEntity.getName();
        FutureItemEntity entity = new FutureItemEntity(futureStr);
        entity.setEntityId(entityId);
        entity.setEntityType(entityType);
        getPresenter().setBean(entity);

        String title = StringUtils.isEmpty(futureStr) ? "" : futureStr;
        mBinding.barMarketsKline.setTitle(title);
    }

    private void setDelievery() {
        String tips = "";
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        String days = holdingEntity.getFutureLimtDays();
        if (holdingEntity.getStatus() == 3) {
            tips = getString(R.string.exchange_order_trading);
        } else if (!TextUtils.isEmpty(days)) {
            tips = String.format(getString(R.string.market_deadline), days);
            int textResource = R.string.days;
            switch (holdingEntity.getDeliveryType()) {
                case 1:
                    textResource = R.string.days;
                    break;
                case 2:
                    textResource = R.string.hours;
                    break;
                case 3:
                    textResource = R.string.minutes;
                    break;
                case 4:
                    textResource = R.string.seconds;
                    break;
            }
            tips += CommonUtils.getResouceString(getContext(), textResource);
        }
        mBinding.barMarketsKline.setFutureTip(tips);
    }

    private void klineDataConvert(List<ChartLineEntity> chartList) {
        klineData.clear();
        for (int i = 0; i < chartList.size(); i++) {
            ChartLineEntity m = chartList.get(i);
            HisData data = BeanChangeFactory.createNewHisData(m);
            klineData.add(data);
        }

        if (entityType == 2) {
            List<ChartLineEntity> spotList = FotaApplication.getInstance().getListFromKlinesByType(1);
            spotData.clear();
            if (spotList != null) {
                for (int i = 0; i < spotList.size(); i++) {
                    ChartLineEntity m = spotList.get(i);
                    HisData data = BeanChangeFactory.createNewHisData(m);
                    spotData.add(data);
                }
            }
        }
    }

    @Override
    public void onRefreshKlineData(boolean isAdd) {
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromKlinesByType(entityType);
        if (chartList == null || chartList.size() == 0) {
            return;
        }
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        if (holdingEntity != null && holdingEntity.getHoldingPrice() != -1) {
            mBinding.kline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
        }
        if (isAdd) {//add 直接重刷
            klineDataConvert(chartList);
            if (entityType == 2) {
//                mBinding.kline.addData(klineData, spotData);
                mBinding.kline.addData(klineData, null);
            } else {
                mBinding.kline.addData(klineData, null);
            }
            if (time15Data != null && time15Data.size() > 0) {
                double hour24Close = time15Data.get(0).getClose();
                mBinding.kline.setLastClose(hour24Close);
            }
        } else {
            float lastData = -1;
            float lastSpot = -1;
            float volume = 0;
            int length = chartList.size();
            ChartLineEntity m = chartList.get(length - 1);
            HisData data = BeanChangeFactory.createNewHisData(m);
            lastData = (float) data.getClose();
            volume = (float) data.getVol();

            if (entityType == 2) {
                List<ChartLineEntity> spotList = FotaApplication.getInstance().getListFromKlinesByType(1);
                if (spotList != null && spotList.size() > 0) {
                    int spotSize = spotList.size();
                    ChartLineEntity m1 = spotList.get(spotSize - 1);
                    HisData data1 = BeanChangeFactory.createNewHisData(m1);
                    lastSpot = (float) data1.getClose();
                }
            }
            mBinding.kline.refreshData(data, volume, lastSpot);
        }
    }

    /**
     * index K线周期选择回调
     */
    @Override
    public void periodClick(int index) {
        currentPeriodIndex = index;
//        periodTextView.setText(types[index]);
        mBinding.kline.setDateFormat(dateFormats[index]);

        if (entityType != 1) {
            getPresenter().getKlineDatas(entityType, entityId, types[index]);
            setKlineLoading(mBinding.kline, true);
        }
    }

    /**
     * 切换K线 分时图
     *
     * @param v
     */
    @Override
    public void onRightTypeClick(View v) {
        isKline = !isKline;
        if (isKline) {
            mKlineTabBar.setIvRightManifyVisible(true);

            if (mBinding.kline != null && mBinding.tline != null) {
                mBinding.kline.setVisibility(View.VISIBLE);
                mBinding.tline.setVisibility(View.GONE);
            }
        } else {
            mKlineTabBar.setIvRightManifyVisible(false);

            if (mBinding.kline != null && mBinding.tline != null) {
                mBinding.kline.setVisibility(View.GONE);
                mBinding.tline.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRightScaleClick(View v) {
        Intent intent = new Intent(this, FullScreenKlineActivity.class);
        Bundle args = new Bundle();
        args.putString("symbol", futureStr);
        args.putInt("id", entityId);
        args.putInt("type", entityType);
        args.putInt("period", currentPeriodIndex);
//        args.putString("deadline", lastDays);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        initData();
    }
}
