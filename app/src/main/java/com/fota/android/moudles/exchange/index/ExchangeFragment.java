package com.fota.android.moudles.exchange.index;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.common.bean.BeanChangeFactory;
import com.fota.android.common.bean.exchange.CurrentPriceBean;
import com.fota.android.common.bean.exchange.ExchangeCurrency;
import com.fota.android.common.bean.home.EntrustBean;
import com.fota.android.common.listener.IUpdateExchangeFragment;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.DecimalDigitsInputFilter;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.BaseFragmentAdapter;
import com.fota.android.core.base.MvpFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.FragmentExchangeTradeLayoutBinding;
import com.fota.android.moudles.exchange.IExchangeFragment;
import com.fota.android.moudles.exchange.complete.ExchangeCompleteFragment;
import com.fota.android.moudles.exchange.money.ExchangeMoneyListFragment;
import com.fota.android.moudles.exchange.orders.ExchangeOrdersFragment;
import com.fota.android.moudles.futures.bean.ToTradeEvent;
import com.fota.android.moudles.market.FullScreenKlineActivity;
import com.fota.android.moudles.market.TradeMarketKlineActivity;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.fota.android.moudles.market.bean.HoldingEntity;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.utils.apputils.TradeUtils;
import com.fota.android.widget.DepthRefreshView;
import com.fota.android.widget.ViewPagerTitle;
import com.fota.android.widget.btbwidget.FotaTextWatch;
import com.fota.android.widget.popwin.PasswordDialog;
import com.fota.android.widget.popwin.SpinerPopWindow;
import com.fota.android.widget.popwin.WindowCloseListener;
import com.fota.android.widget.recyclerview.SmartRefreshLayoutUtils;
import com.guoziwei.fota.chart.OnLoadEdgeListener;
import com.guoziwei.fota.chart.view.BaseChartView;
import com.guoziwei.fota.chart.view.fota.FotaBigKLineBarChartView;
import com.guoziwei.fota.chart.view.fota.ImBeddedTimeLineBarChartView;
import com.guoziwei.fota.model.HisData;
import com.ndl.lib_common.utils.LiveDataBus;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fjw on 2018/3/27.
 * <p>
 * https://juejin.im/entry/585a2c7f8d6d810065cacf77
 * 兑换usdt界面
 */
public class ExchangeFragment extends MvpFragment<ExchangePresenter>
        implements ExchangeTradeView, View.OnClickListener, BaseChartView.KlineScreenInterface,
        DepthRefreshView.DepthRefreshViewListener, IUpdateExchangeFragment, IExchangeFragment {

    int PERCENT_CHILD_COUNT = 4;

    protected boolean isLimit = false;

    public final static int EVENT_MONEY_CHANGED = 0;
    public final static int ORDER_SUCCESS = 1;
    public final static int PASSWORD_TOKEN_ERROR = 2;
    //jiang ticker 与 盘口 的price保留小数位，可以根据盘口的左右切换
//    protected int tickerDepthDecimal = -1;

    protected List<Fragment> fragments;

    Handler handler = new Handler();

    SpinerPopWindow popupWindow;

    protected FragmentExchangeTradeLayoutBinding mHeadBinding;
    /**
     * 当前是否是购买行为
     */
    protected boolean isBuy = true;

    private FotaTextWatch amoutTextchange;
    private FotaTextWatch moneyTextchange;
    protected FotaTextWatch priceTextchange;
    private int percentIndex = -1;

    /**
     * 需要外带币种的
     *
     * @param currency
     * @return
     */
    public static ExchangeFragment newInstance(ExchangeCurrency currency, boolean isBuy) {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.MODEL, currency);
        args.putBoolean("isBuy", isBuy);
        ExchangeFragment fragment = new ExchangeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void updateInstance(ExchangeCurrency currency, boolean isBuy) {
        getPresenter().setFromHq(currency.getAssetId());
        popupWindow = null;
        this.isBuy = isBuy;
        getPresenter().setBasePrecision("-1");
        clearEditText();
        onRefresh();
        refreshBuy();
    }

    //chart relative
    /**
     * 合约数据or usdt兑换
     * T 15m
     */
    final protected List<HisData> time15Data = new ArrayList<>(100);
    //已选择的kline 周期参数
    protected int currentPeriodIndex = 1;
    static final String[] dateFormats = {"HH:mm", "HH:mm", "HH:mm", "HH:mm", "HH:mm", "HH:mm", "HH:mm", "MM-dd", "yyyy-MM-dd"};
    /**
     * 合约数据or usdt兑换
     * K
     */
    protected final List<HisData> klineData = new ArrayList<>(100);
    protected boolean isKline;


    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHeadBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_exchange_trade_layout, container, false);
        mHeadBinding.setView(this);
        onRefresh();
        return mHeadBinding.getRoot();
    }

    @Override
    protected ExchangePresenter createPresenter() {
        return new ExchangePresenter(this);
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        if (bundle != null && bundle.containsKey("isBuy")) {
            isBuy = bundle.getBoolean("isBuy");
        }

        LiveDataBus.INSTANCE.getBus("trade").observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                ToTradeEvent entity = (ToTradeEvent) o;
                if (entity.getFutureItemEntity().getEntityType() == 3){
                    if (getPresenter().getUsdtList() != null){
                        for (ExchangeCurrency exchangeCurrency: getPresenter().getUsdtList()){
                            if (exchangeCurrency.getAssetName().equals(entity.getFutureItemEntity().getFutureName())){
                                getPresenter().setSelectItem(exchangeCurrency);
                                break;
                            }
                        }
                    }
                }

                isBuy = entity.isBuy();
                refreshBuy();
            }
        });
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        onRefresh();
        setIsFutures();
        refreshBuy();
        refreshPriceType();
        initViewPager();
        initCoodingLayout();
        initListenter();
        SmartRefreshLayoutUtils.initHeader(mHeadBinding.refreshLayout, getContext());
        mHeadBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                ExchangeFragment.this.onRefresh();
            }
        });
        mHeadBinding.rightContainer.setListener(this);
        mHeadBinding.imbedUpDown.setUp(true);

        UIUtil.setRoundCornerBorderBg(mHeadBinding.exchangeLayoutTop,
                AppConfigs.isWhiteTheme() ? 0x1A000000 : 0x1AFFFFFF);

        UIUtil.setRoundCornerBorderBg(mHeadBinding.futureLayoutTop,
                AppConfigs.isWhiteTheme() ? 0x1A000000 : 0x1AFFFFFF);

        UIUtil.setRoundBg(mHeadBinding.exchangeOrderNotice, 0xFF3d79ec);

        mHeadBinding.kline.setFromTrade(true);
        mHeadBinding.kline.setChartType(FotaBigKLineBarChartView.ChartType.USDT);
        mHeadBinding.tline.setChartType(ImBeddedTimeLineBarChartView.ChartType.USDT);
        mHeadBinding.kline.setScreenInterface(this);
        addPercentContainer();
        UIUtil.setRoundCornerBorderBg(mHeadBinding.percentContainer, getAttrColor(R.attr.font_color4), 4);
    }

    /**
     * @param index
     */
    @SuppressLint("WrongConstant")
    private void setPercent(int index) {
        percentIndex = index;
        int corner = UIUtil.dip2px(getContext(), 4);
        //未登录 并且点中点话 去登录
        if (!UserLoginUtil.havaUser() && index >= 0) {
            UserLoginUtil.checkLogin(getContext());
            return;
        }
        if (mHeadBinding.percentContainer.getChildCount() == 0) {
            return;
        }
        for (int i = 0; i < PERCENT_CHILD_COUNT; i++) {
            float[] floats = getRadii(corner, i);
            GradientDrawable gd = new GradientDrawable();//创建drawable
            gd.setColor(getAttrColor(index == i ? R.attr.font_color4 : R.attr.bg_color2));
            ((TextView) mHeadBinding.percentContainer.getChildAt(i * 2)).setTextColor(
                    getAttrColor(index == i ? R.attr.font_color : R.attr.font_color4));
            gd.setGradientType(GradientDrawable.OVAL);
            gd.setCornerRadii(floats);
            UIUtil.setBackgroundDrawable(mHeadBinding.percentContainer.getChildAt(i * 2), gd);
        }
        if (index >= 0) {
            double maxValue = Pub.GetDouble(mHeadBinding.maxAmountValue.getText().toString()) * (index + 1) / PERCENT_CHILD_COUNT;
            //这里不能调用setCountText 1死循环 2逻辑不对
            String count = Pub.getPriceFormat(maxValue, getAmountPrecision());
            mHeadBinding.amount.setTextWithOutChanged(count);
            changeMoneyByAmount(count);
        }
    }

    private void changeMoneyByAmount(String count) {
        double price = getPrice();
        if (price > 0) {
            double all = Pub.multiply(count, price + "");
            mHeadBinding.money.setTextWithOutChanged(
                    !"0".equals(Pub.zoomZero(Pub.getPriceFormat(all, getMoneyPrecision()))) ?
                            Pub.zoomZero(Pub.getPriceFormat(all, getMoneyPrecision()))
                            : "");
        }
    }

    /**
     * 获取白分比conner
     *
     * @param corner
     * @param i
     * @return
     */
    private float[] getRadii(int corner, int i) {
        return new float[]{
                i == 0 ? corner : 0, i == 0 ? corner : 0,
                i == PERCENT_CHILD_COUNT - 1 ? corner : 0, i == PERCENT_CHILD_COUNT - 1 ? corner : 0,
                i == PERCENT_CHILD_COUNT - 1 ? corner : 0, i == PERCENT_CHILD_COUNT - 1 ? corner : 0,
                i == 0 ? corner : 0, i == 0 ? corner : 0};
    }

    /**
     * 添加白分比线
     */
    private void addPercentContainer() {
        //tv LayoutParams
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        //line LayoutParams
        LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
        mHeadBinding.percentContainer.removeAllViews();
        for (int i = 0; i < PERCENT_CHILD_COUNT; i++) {
            if (i > 0) {
                addLine(lpView);
            }
            addTvInPercent(lp, i);
        }
    }

    /**
     * 添加百分比textview
     *
     * @param lp
     * @param i
     */
    private void addTvInPercent(LinearLayout.LayoutParams lp, int i) {
        int onePercent = 100 / PERCENT_CHILD_COUNT;
        TextView tvPercent = new TextView(getContext());
        int percent = onePercent * i + onePercent;
        tvPercent.setText(percent + "%");
        tvPercent.setGravity(Gravity.CENTER);
        tvPercent.setLayoutParams(lp);
        mHeadBinding.percentContainer.addView(tvPercent);
        final int finalI = i;
        tvPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPercent(finalI);
            }
        });
    }

    /**
     * 添加线
     *
     * @param lpView
     */
    private void addLine(LinearLayout.LayoutParams lpView) {
        View view = new View(getContext());
        int color = R.attr.font_color4;
        view.setBackgroundColor(getAttrColor(color));
        view.setLayoutParams(lpView);
        mHeadBinding.percentContainer.addView(view);
    }

    private int getAttrColor(int color) {
        return Pub.getColor(getContext(), color);
    }


    /**
     * 设置拦截事件
     */
    private void initCoodingLayout() {
        mHeadBinding.viewPagerTitle.setListener(new ViewPagerTitle.SelectItemListener() {
            @Override
            public void click(int position) {
                if (Pub.isListExists(fragments)) {
                    mHeadBinding.contentLayout.setHasData(
                            ((BaseFragment) fragments.get(position)).hasData);

                    boolean allHasData = false;
                    for (Fragment fragment : fragments) {
                        allHasData = allHasData | ((BaseFragment) fragment).hasData;
                    }
                    mHeadBinding.contentLayout.setHasDataAll(allHasData);
                }
            }
        });
    }

    private void setIsFutures() {
        UIUtil.setVisibility(mHeadBinding.exchangeTitle, !isFutures());
        UIUtil.setVisibility(mHeadBinding.futuresTitle, isFutures());
        UIUtil.setVisibility(mHeadBinding.exchangeTopInfo, !isFutures());
        UIUtil.setVisibility(mHeadBinding.futuresTopInfo, isFutures());

        UIUtil.setVisibility(mHeadBinding.exchangeLayout, !isFutures());
        UIUtil.setVisibility(mHeadBinding.futureLayout, isFutures());

//        StatusBarUtil.setPaddingSmart(getContext(), isFutures() ? mHeadBinding.futuresTitle : mHeadBinding.exchangeTitle);

    }

    protected boolean isFutures() {
        return false;
    }


//    public boolean isEditMoney;
//
//    private boolean isEditAmount;
//
//    private boolean isEditPrice;


    protected void initListenter() {
        //jiang
        mHeadBinding.kline.setDateFormat(dateFormats[currentPeriodIndex]);
        mHeadBinding.kline.setOnEdgeListener(new OnLoadEdgeListener() {
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

        amoutTextchange = new FotaTextWatch() {
            @Override
            protected void onTextChanged(String s) {
                //我在输入数量  金额随我变动
                changeMoneyByAmount(s);
                setPercent(-1);
            }
        };

        moneyTextchange = new FotaTextWatch() {

            @Override
            protected void onTextChanged(String s) {
                double all = Pub.GetDouble(s);
                //我在输入金额   数量随我变动
                double price = getPrice();
                double count = 0;
                if (price > 0) {
                    count = all / price;
                    setCountTextWithOutChanged(count > 0 ? Pub.zoomZero(Pub.getPriceFormat(count, getAmountPrecision()))
                            : "");
                }
                setPercent(-1);
            }
        };


        priceTextchange = new FotaTextWatch() {

            @Override
            protected void onTextChanged(String s) {
                double all = Pub.GetDouble(mHeadBinding.money.getText().toString());
                double amount = Pub.GetDouble(mHeadBinding.amount.getText().toString());
                double price = getPrice();
                if (amount > 0) {
                    all = Pub.multiply(getPrice() + "", mHeadBinding.amount.getText().toString());
                    mHeadBinding.money.setTextWithOutChanged(!"0".endsWith(Pub.zoomZero(Pub.getPriceFormat(all, getMoneyPrecision()))) ?
                            Pub.zoomZero(Pub.getPriceFormat(all, getMoneyPrecision()))
                            : "");
                } else {
                    if (price > 0) {
                        amount = all / price;
                        String text = amount > 0 ?
                                Pub.zoomZero(Pub.getPriceFormat(amount, getAmountPrecision()))
                                : "";
                        setCountTextWithOutChanged(text);
                    }
                }
                setPercent(-1);
                validMaxInfo();
            }
        };


        mHeadBinding.amount.addTextChangedListener(amoutTextchange);

        mHeadBinding.money.addTextChangedListener(moneyTextchange);

        mHeadBinding.price.addTextChangedListener(priceTextchange);

    }

    /**
     * 设置数量点值
     *
     * @param text
     */
    private void setCountText(String text) {
        //数量变动得把下方数据 清零
        setPercent(-1);
        mHeadBinding.amount.setText(text);
    }

    /**
     * 设置数量点值
     *
     * @param text
     */
    private void setCountTextWithOutChanged(String text) {
        //数量变动得把下方数据 清零
        setPercent(-1);
        mHeadBinding.amount.setTextWithOutChanged(text);
    }


    @Override
    public void onResume() {
        super.onResume();
        changeWidth();
        SmartRefreshLayoutUtils.refreshHeadLanguage(mHeadBinding.refreshLayout, getContext());
//        mvpPresenter.addAllChannel();
        onRefresh();

//        getPresenter().updateCurrency();
    }

    @Override
    public void onPause() {
        super.onPause();
        mvpPresenter.removeAllChannel();
    }

    protected void changeWidth() {
        mHeadBinding.priceType.post(new Runnable() {
            @Override
            public void run() {

                int textWith = (int) UIUtil.getTextWidth(getContext(), getXmlString(R.string.exchange_his_price), 14);

                int width = UIUtil.dip2px(getContext(), 12) + textWith;

                ViewGroup.LayoutParams lpPrice = mHeadBinding.priceType.getLayoutParams();
                lpPrice.width = width;
                mHeadBinding.priceType.setLayoutParams(lpPrice);

                ViewGroup.LayoutParams lpAmount = mHeadBinding.amountTip.getLayoutParams();
                lpAmount.width = width;
                mHeadBinding.amountTip.setLayoutParams(lpAmount);

                ViewGroup.LayoutParams lpMoney = mHeadBinding.moneyTip.getLayoutParams();
                lpMoney.width = width;
                mHeadBinding.moneyTip.setLayoutParams(lpMoney);
            }
        });
    }

    /**
     * 获取当前参考价格
     *
     * @return
     */
    private double getPrice() {
        double price = Pub.GetDouble(mHeadBinding.price.getText().toString());
        if (!isLimit) {
            return Pub.GetDouble(mHeadBinding.rightContainer.getCurrentPrice());
        }
        return price;
    }

    protected void validMaxInfo() {
        double price = getPrice();
        mHeadBinding.maxAmount.setText(isBuy
                ? getXmlString(R.string.exchange_can_buy) : getXmlString(R.string.exchange_can_sell));
        String maxValue = "--";
        if (UserLoginUtil.havaUser()) {
            if (isBuy) {
                if (price > 0) {
                    double maxCount = getMaxMoney() / price;
                    maxValue = Pub.getPriceFormat(maxCount, getAmountPrecision());
                }
            } else {
                maxValue = Pub.getPriceFormat(getMaxMoney(), getAmountPrecision());
            }
        }
        mHeadBinding.maxAmountValue.setText(maxValue);
    }

    protected void initViewPager() {
        fragments = new ArrayList<>();
        fragments.add(new ExchangeOrdersFragment());
        fragments.add(new ExchangeCompleteFragment());
        fragments.add(new ExchangeMoneyListFragment());
        List<String> title = new ArrayList<>();
        title.add(getXmlString(R.string.exchange_order));
        title.add(getXmlString(R.string.exchange_complete));
        title.add(getXmlString(R.string.exchange_wallet));
        BaseFragmentAdapter baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager(),
                fragments, title
        );
        mHeadBinding.viewPager.setAdapter(baseFragmentAdapter);
        mHeadBinding.viewPagerTitle.initTitles(title);
        mHeadBinding.viewPagerTitle.bindViewpager(mHeadBinding.viewPager);
        mHeadBinding.viewPager.setOffscreenPageLimit(2);
    }


    @Override
    public void onRefreshTicker(CurrentPriceBean price) {
        mHeadBinding.rightContainer.setTickInfo(price);
        if (price == null) {
            return;
        }
        boolean isUp = !price.getDailyReturn().contains("-");
//        mHeadBinding.imbedTickerPrice.setText(price.getPriceFloor(tickerDepthDecimal));
        mHeadBinding.imbedTickerPrice.setText(price.getPrice());
        UIUtil.setTextColor(mHeadBinding.imbedTickerPrice, AppConfigs.getColor(isUp));
        mHeadBinding.imbedUpDown.setUp(isUp);
        mHeadBinding.imbedTickerTrend.setText(price.getDailyReturn());
        UIUtil.setTextColor(mHeadBinding.imbedTickerTrend, AppConfigs.getColor(isUp));
        //受市场价影响
        updateMoneyFromTicker();
    }

    /**
     * 当五档和限价返回档时候
     * 如果是金额为空 补充金额 通过充输入金额的方法提示
     * 金额不为空 只需要补充最大可买金额即可
     */
    private void updateMoneyFromTicker() {
//        if (Pub.isStringEmpty(mHeadBinding.money.getText().toString())) {
//            mHeadBinding.price.setText(mHeadBinding.price.getText().toString());
//        } else {
//            validMaxInfo();
//        }
        validMaxInfo();
    }

    @Override
    public void onRefreshDepth(List<EntrustBean> buys, List<EntrustBean> sells, List<String> precisions, Float dollarEvaluation) {
        if (mHeadBinding.rightContainer != null) {
            if (precisions != null) {
                mHeadBinding.rightContainer.setSmallLargePrecision(precisions);
            }
            //mHeadBinding.rightContainer.setDollarEvaluation(dollarEvaluation);
            mHeadBinding.rightContainer.refreshDepth(buys, sells);
        }
        updateMoneyFromTicker();
    }

    /**
     */
    @Override
    public void resetFreshTimelineData() {
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromTimesByType(3);
        String temp = getAssetName();
        if (getAssetName().contains("/")) {
            int length = getAssetName().indexOf("/");
            temp = getAssetName().substring(0, length);
        }
        mHeadBinding.tline.setBiaodi(temp);
        freshChartView(chartList);
    }

    private void freshChartView(List<ChartLineEntity> chartList) {
        if (chartList == null) {
            mHeadBinding.tline.initData(null, null);
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
        mHeadBinding.imbedNarrowChart.initData(time15Data);
        HoldingEntity entity = FotaApplication.getInstance().getHoldingEntity();
        mHeadBinding.tline.setmDigits(entity.getDecimal());
        mHeadBinding.tline.initData(time15Data);
        if (time15Data != null && time15Data.size() > 0) {
            double hour24Close = time15Data.get(0).getClose();
            mHeadBinding.tline.setLastClose(hour24Close);
        }
    }

    @Override
    public void onNoDataCallBack(int charType) {
        if (charType == 0) {
            mHeadBinding.tline.initData(null, null);
        } else {
            mHeadBinding.kline.initData(null, null);
        }
    }

    @Override
    public void setOverShowLoading(int charType, boolean isShowLoading) {
        if (charType == 0) {
            setKlineLoading(mHeadBinding.tline, isShowLoading);
        } else {
            setKlineLoading(mHeadBinding.kline, isShowLoading);
        }
    }

    protected void setKlineLoading(BaseChartView view, boolean isShowLoading) {
        if (view != null) {
            view.setLoading(isShowLoading);
        }
    }

    @Override
    public void resetFreshKlineData() {
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromKlinesByType(3);
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        if (chartList == null) {
            onNoDataCallBack(1);
            return;
        }
        klineDataConvert(chartList);
        mHeadBinding.kline.setNeedMoveToLast(true);
        mHeadBinding.kline.setmDigits(holdingEntity.getDecimal());

        mHeadBinding.kline.initData(klineData);

        if (holdingEntity != null && holdingEntity.getHoldingPrice() != -1) {
            mHeadBinding.kline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
        }
        if (time15Data != null && time15Data.size() > 0) {
            double hour24Close = time15Data.get(0).getClose();
            mHeadBinding.kline.setLastClose(hour24Close);
        }
    }

    @Override
    public void onRefreshKlineData(boolean isAdd) {
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromKlinesByType(3);
        if (chartList == null || chartList.size() == 0) {
            return;
        }
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        if (holdingEntity != null && holdingEntity.getHoldingPrice() != -1) {
            mHeadBinding.kline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
        }
        if (isAdd) {//add 直接重刷
            klineDataConvert(chartList);
            mHeadBinding.kline.addData(klineData, null);

            if (time15Data != null && time15Data.size() > 0) {
                double hour24Close = time15Data.get(0).getClose();
                mHeadBinding.kline.setLastClose(hour24Close);
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

            mHeadBinding.kline.refreshData(data, volume, lastSpot);
        }
    }

    @Override
    public void onRefreshDeliveryOrHold() {

    }

    protected void klineDataConvert(List<ChartLineEntity> chartList) {
        klineData.clear();
        for (int i = 0; i < chartList.size(); i++) {
            ChartLineEntity m = chartList.get(i);
            HisData data = BeanChangeFactory.createNewHisData(m);
            klineData.add(data);
        }
    }

    @Override
    public void fullScreen() {
        if (getPresenter() == null || getPresenter().getSelectAssetItem() == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), FullScreenKlineActivity.class);
        Bundle args = new Bundle();
        args.putString("symbol", getPresenter().selectItem.getKey());
        int id = Pub.GetInt(getPresenter().getSelectAssetItem().getAssetId());
        args.putInt("id", id);
        args.putInt("type", 3);
        args.putInt("period", currentPeriodIndex);
        args.putString("deadline", "");
        intent.putExtras(args);
        startActivity(intent);
    }


    /**
     * kline
     */
    @Override
    public void onRefreshTimelineData() {
        resetFreshTimelineData();
    }

    @Override
    public void onRefresh() {
        super.onRefresh();

        if (getView() == null) return;
        if (!UserLoginUtil.havaUser()) {

        }
        getPresenter().addTopInfoMessageLisenter();
        getPresenter().setCurrentPeriodIndex(currentPeriodIndex);
        getPresenter().reqUsdtList();
        if (Pub.isListExists(fragments)) {
            for (Fragment fragment : fragments
                    ) {
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onRefresh();
                }
            }
        }

        if (getPresenter().selectItem != null)
            getPresenter().getTimeLineDatas(getPresenter().getType(), Pub.GetInt(getPresenter().selectItem.getAssetId()), "1m");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.money_tip:
                mHeadBinding.money.requestFocus();
                KeyBoardUtils.openKeybord(mHeadBinding.money, getContext());
                //
                break;
            case R.id.amount_tip:
                mHeadBinding.amount.requestFocus();
                KeyBoardUtils.openKeybord(mHeadBinding.amount, getContext());
                //
                break;
            case R.id.select_buy:
                isBuy = true;
                refreshBuy();
                break;
            case R.id.select_sell:
                isBuy = false;
                refreshBuy();
                break;
            case R.id.btn_buy_sell:
                if (!UserLoginUtil.havaUser()) {
                    UserLoginUtil.checkLogin(getContext());
                    return;
                }
                if (isLimit) {
                    if (Pub.GetDouble(mHeadBinding.price.getText().toString()) <= 0) {
                        showToast(R.string.exchange_toast_inputprice);
                        return;
                    }
                }
                if (Pub.GetDouble(mHeadBinding.amount.getText().toString()) <= 0) {
                    showToast(getXmlString(R.string.exchange_account_hint));
                    return;
                }
//                verifyPassword();

                tradeToPresenter(UserLoginUtil.getCapital());
                break;
            case R.id.exchange_change_currency:
            case R.id.exchange_currency:
                mHeadBinding.exchangeChangeCurrencyArrow.reverse();
                showPopWindow();
                break;
            case R.id.price_type:
            case R.id.price_other:
                isLimit = !isLimit;
                refreshPriceType();
                break;
            case R.id.transfer2:
            case R.id.transfer:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.UsdtContractTransferFragment);
                break;
            case R.id.img_type_change:
//                isKline = !isKline;
//                if (isKline) {
//                    mHeadBinding.imgTypeChange.setImageResource(Pub.getThemeResource(getContext(), R.attr.chart_time_line));
//                } else {
//                    mHeadBinding.imgTypeChange.setImageResource(Pub.getThemeResource(getContext(), R.attr.chart_kline));
//                }
//                changeKtline();
                Intent intent = new Intent(requireActivity(), TradeMarketKlineActivity.class);
                Bundle args = new Bundle();
                args.putString("symbol", getPresenter().selectItem.getAssetName());
                args.putInt("id",  Integer.valueOf(getPresenter().selectItem.getAssetId()));
                args.putInt("type", 3);
                intent.putExtras(args);
                startActivity(intent);
                break;

        }
    }

    protected void changeKtline() {
        if (isKline) {
            if (mHeadBinding.kline != null && mHeadBinding.tline != null) {
                mHeadBinding.kline.setVisibility(View.VISIBLE);
                mHeadBinding.tline.setVisibility(View.GONE);
            }
        } else {
            if (mHeadBinding.kline != null && mHeadBinding.tline != null) {
                mHeadBinding.kline.setVisibility(View.GONE);
                mHeadBinding.tline.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected boolean viewGroupFocused() {
        return true;
    }

    /**
     * 弹出密码框
     */
    protected void showDialog() {
        if (getHoldingActivity().isFinishing()) {
            return;
        }
        PasswordDialog dialog = new PasswordDialog(getContext());
        dialog.setListener(new PasswordDialog.OnSureClickListener() {
            @Override
            public void onClick(String fundCode) {
                tradeToPresenter(fundCode);
            }
        });
        dialog.show();
    }

    /**
     * 执行交易
     *
     * @param fundCode
     */
    protected void tradeToPresenter(String fundCode) {
        //1-限价单, 2-市价单
        getPresenter().getModel().setPriceType(isLimit ? 1 : 2);
        if (isLimit) {
            getPresenter().getModel().setPrice(mHeadBinding.price.getText().toString());
        } else {
            getPresenter().getModel().setPrice(null);
        }
        getPresenter().getModel().setTotalAmount(mHeadBinding.amount.getText().toString());
        getPresenter().submit(fundCode);
    }

    /**
     * 执行交易
     *
     * @param fundCode
     */
//    protected void changePasswordToToken(String fundCode) {
//        TradeUtils.getInstance().changePasswordToToken(getContext(), fundCode,
//                new TradeUtils.ChangePassWordListener() {
//                    @Override
//                    public void setPasswordToken(String token) {
//                        tradeToPresenter(UserLoginUtil.getCapital());
//                    }
//                });
//    }


    /**
     * 验证密码
     */
    protected void verifyPassword() {
        TradeUtils.getInstance().validPassword(getContext(), mRequestCode, new TradeUtils.ExchangePasswordListener() {

            @Override
            public void noPassword() {
                tradeToPresenter(UserLoginUtil.getCapital());
            }

            @Override
            public void showPasswordDialog() {
                showDialog();
            }
        });
    }


    protected void showPopWindow() {
        if (!Pub.isListExists(getPresenter().getUsdtList())) {
            return;
        }
        if (getHoldingActivity().isFinishing()) {
            return;
        }
        if (popupWindow == null) {
            popupWindow = new SpinerPopWindow(getContext());
            popupWindow.setOnPopListener(new SpinerPopWindow.OnPopClickListener() {
                @Override
                public void onPopClick(FtKeyValue model, int position) {
                    clearEditText();
                    getPresenter().setSelectItem((ExchangeCurrency) model);
                }
            });

            popupWindow.setCloseListener(new WindowCloseListener() {
                @Override
                public void dismiss() {
                    mHeadBinding.exchangeChangeCurrencyArrow.reset();
                }
            });
        }

        popupWindow.getAdapter().putList(getPresenter().getUsdtList());
        if (getPresenter().getSelectAssetItem() != null) {
            popupWindow.setValue(getPresenter().getSelectAssetItem().getValue());
        }
        popupWindow.showAsDropDown(mHeadBinding.exchangeTitle);
    }

    /**
     * 0-对手价，1-指定价
     * 刷新市价和现价
     */
    protected void refreshPriceType() {
        mHeadBinding.price.setText(mHeadBinding.price.getText().toString());
        UIUtil.setVisibility(mHeadBinding.price, isLimit);
        UIUtil.setVisibility(mHeadBinding.priceOther, !isLimit);

        UIUtil.setVisibility(mHeadBinding.moneyUnit, isLimit);

        UIUtil.setVisibility(mHeadBinding.money, isLimit);
        UIUtil.setVisibility(mHeadBinding.moneyOther, !isLimit);
        UIUtil.setText(mHeadBinding.priceTypeTv, isLimit ? getXmlString(R.string.exchange_limit_price) : getXmlString(R.string.exchange_his_price));
    }


    /**
     * 刷新与买卖有关的状态
     */
    protected void refreshBuy() {

        GradientDrawableUtils.setBgAlpha(mHeadBinding.selectBuy, 30);
        GradientDrawableUtils.setBgAlpha(mHeadBinding.selectSell, 30);


        GradientDrawableUtils.setBgColor(mHeadBinding.selectBuy, isBuy ? AppConfigs.getUpColor() : getAttrColor(R.attr.bg_color));

        mHeadBinding.selectBuy.setTextColor(isBuy ? AppConfigs.getUpColor() : getAttrColor(R.attr.font_color4));

        GradientDrawableUtils.setBgColor(mHeadBinding.selectSell, !isBuy ? AppConfigs.getDownColor() : getAttrColor(R.attr.bg_color));
        mHeadBinding.selectSell.setTextColor(!isBuy ? AppConfigs.getDownColor() : getAttrColor(R.attr.font_color4));

        UIUtil.setRoundCornerBg(mHeadBinding.btnBuySell, AppConfigs.getColor(isBuy));

        mHeadBinding.btnBuySell.setText(isBuy ? getXmlString(R.string.common_buy)
                : getXmlString(R.string.common_sell));

        getPresenter().getModel().setIsBuy(isBuy);

        mHeadBinding.price.setText(mHeadBinding.price.getText().toString());

        //UIUtil.setVisibility(mHeadBinding.priceOther, !isLimit);
        mHeadBinding.priceOther.setText(isBuy ? getXmlString(R.string.exchange_optimal)
                : getXmlString(R.string.exchange_optimal));

        setPercent(percentIndex);
    }

    @Override
    public void showTopInfo(String name) {
        mHeadBinding.exchangeOrderNotice.setText(name);
        mHeadBinding.exchangeOrderNotice.setVisibility(View.VISIBLE);
        handler.removeCallbacks(closeInfo);
        handler.postDelayed(closeInfo, 2000);
    }

    @Override
    public void refreshComplete() {
        mHeadBinding.refreshLayout.finishRefresh();
    }

    @Override
    public void cancelSuccess() {

    }

    private Runnable closeInfo = new Runnable() {

        @Override
        public void run() {
            getHoldingActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHeadBinding.exchangeOrderNotice.setVisibility(View.GONE);
                }
            });
        }
    };

    @Override
    public void refreshCurrency() {
        refreshAssetView();
    }


    /**
     * 铲ge
     * 刷新界面相关数据
     */
    private void refreshAssetView() {
        if (getSelectAssetItem() != null) {
            mHeadBinding.exchangeCurrency.setText(getSelectAssetItem().getKey());
        }
        mHeadBinding.amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(getAmountPrecision())});
        mHeadBinding.price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(getPricePrecision())});
        mHeadBinding.money.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(getMoneyPrecision())});

        mHeadBinding.firstWalletUnit.setText(getXmlString(R.string.common_usable) + " " + getAssetName() + "：");

        mHeadBinding.secondWalletUnit.setText(getXmlString(R.string.common_usable) + " " + getSecondName() + "：");

        mHeadBinding.amountUnit.setText(getAssetName());

        mHeadBinding.moneyUnit.setText(getSecondName());

        //mHeadBinding.exchangeIcon
        Glide.with(getContext()).load(getSelectAssetItem().getCoinIconUrl())
                .into(mHeadBinding.exchangeIcon);

        //jiang
        mHeadBinding.rightContainer.resetTicker();
        mHeadBinding.rightContainer.setType(3, getSelectAssetItem().getAssetName());

        validMaxInfo();

        setWalletAmount();

        if (Pub.isStringEmpty(mHeadBinding.amount.getText().toString())) {
            setCountText(getDefaultAmount());
        }

    }

    private void setWalletAmount() {
        mHeadBinding.firstWallet.setText(getAssetAmount());
        mHeadBinding.secondWallet.setText(getUsdtAmount());
    }

    public double getMaxMoney() {
        return isBuy ? Pub.GetDouble(getUsdtAmount()) : Pub.GetDouble(getAssetAmount());
    }

    /**
     * 获取btc数据
     *
     * @return
     */
    String getAssetAmount() {
        if (Pub.getListSize(fragments) > 2) {
            if (fragments.get(2) instanceof ExchangeMoneyListFragment) {
                return ((ExchangeMoneyListFragment) fragments.get(2)).geItemAmount(getAssetName());
            }
        }
        return "--";
    }

    /**
     * 获取usdt数据
     *
     * @return
     */
    String getUsdtAmount() {
        if (Pub.getListSize(fragments) > 2) {
            if (fragments.get(2) instanceof ExchangeMoneyListFragment) {
                return ((ExchangeMoneyListFragment) fragments.get(2)).geItemAmount(getSecondName());
            }
        }
        return "--";
    }


    public String getAssetId() {
        if (getSelectAssetItem() != null) {
            return getSelectAssetItem().getAssetId() + "";
        }
        return null;
    }

    /**
     * 获取当前价格的小数位数
     *
     * @return
     */
    public int getPricePrecision() {
        if (getSelectAssetItem() != null) {
            return getSelectAssetItem().getUsdtTradePricePrecision();
        }
        return 2;
    }


    /**
     * 获取当前数量的小数位数
     *
     * @return
     */
    public String getDefaultAmount() {
        if (getSelectAssetItem() != null) {
            return getSelectAssetItem().getDefaultAmount();
        }
        return "";
    }

    /**
     * 获取当前数量的小数位数
     *
     * @return
     */
    public int getAmountPrecision() {
        if (getSelectAssetItem() != null) {
            return getSelectAssetItem().getUsdtTradeAmountPrecision();
        }
        return 2;
    }

    /**
     * 获取当前数量的小数位数
     *
     * @return
     */
    public int getMoneyPrecision() {
        if (getSelectAssetItem() != null) {
            return getSelectAssetItem().getUsdtTradeValuePrecision();
        }
        return 2;
    }


    private ExchangeCurrency getSelectAssetItem() {
        return getPresenter().getSelectAssetItem();
    }

    public String getAssetName() {
        if (getSelectAssetItem() != null) {
            return getSelectAssetItem().getFirstName();
        }
        return "";
    }

    public String getSecondName() {
        if (getSelectAssetItem() != null) {
            return getSelectAssetItem().getSecondName();
        }
        return "";
    }

    public void event(int i) {
        switch (i) {
            case EVENT_MONEY_CHANGED:
                //由于资产请求数据较慢  所以这里需要资产请求完再刷一次数据 比较合理
                setWalletAmount();
                break;
            case ORDER_SUCCESS:

                break;
        }
    }

    @Override
    public void notifyFromPresenter(int action) {
        switch (action) {
            case ORDER_SUCCESS:
                //由于资产请求数据较慢  所以这里需要资产请求完再刷一次数据 比较合理
                //by luofu  返回默认值
                setCountText(getDefaultAmount());
                break;
        }
    }


    protected void clearEditText() {
        mHeadBinding.price.setText("");
        setCountText("");
        mHeadBinding.futuresChangeCurrencyArrow.requestFocus();
//        KeyBoardUtils.closeKeybord(mHeadBinding.price, getContext());
//        KeyBoardUtils.closeKeybord(mHeadBinding.amount, getContext());
//        KeyBoardUtils.closeKeybord(mHeadBinding.money, getContext());
    }


    @Override
    public void onClickItem(EntrustBean bean) {
        isLimit = true;
        refreshPriceType();
        validMaxInfo();
        mHeadBinding.price.setText(bean.getPrice());
    }

    @Override
    public void onTickClick(CurrentPriceBean currentPrice) {
        isLimit = true;
        refreshPriceType();
        validMaxInfo();
        mHeadBinding.price.setText(currentPrice.getPrice());
    }

    @Override
    public void onRefreshDigital(String remove, String add) {
        getPresenter().changeDigitalChannel(remove, add);
//        tickerDepthDecimal = add;
        String price = mHeadBinding.imbedTickerPrice.getText().toString();
//        if (tickerDepthDecimal != -1) {
//            String price2 = Pub.getPriceStringForLengthRound(Pub.GetDouble(price), tickerDepthDecimal);
//            mHeadBinding.imbedTickerPrice.setText(price2);
//        }
    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_capital_quickcheck_suc:
                if (!TextUtils.equals(mRequestCode, event.getParam(String.class))) {
                    return;
                }
                tradeToPresenter(UserLoginUtil.getCapital());
                break;
            case R.id.event_capital_quickcheck_fail:
                if (!TextUtils.equals(mRequestCode, event.getParam(String.class))) {
                    return;
                }
                showDialog();
                break;
        }
    }


    @Override
    public void setHasData(boolean hasData, Fragment fragment) {
        if (Pub.isListExists(fragments)) {
            int index = fragments.indexOf(fragment);
            if (index == mHeadBinding.viewPagerTitle.getIndex()) {
                mHeadBinding.contentLayout.setHasData(hasData);
            }
        }
    }
}
