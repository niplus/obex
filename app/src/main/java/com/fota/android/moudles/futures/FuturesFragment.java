package com.fota.android.moudles.futures;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.FotaApplication;
import com.fota.android.common.bean.BeanChangeFactory;
import com.fota.android.common.bean.exchange.CurrentPriceBean;
import com.fota.android.common.bean.home.EntrustBean;
import com.fota.android.common.listener.IFuturesUpdateFragment;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.DecimalDigitsInputFilter;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BaseFragmentAdapter;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.moudles.exchange.index.ExchangeFragment;
import com.fota.android.moudles.futures.complete.FuturesCompleteFragment;
import com.fota.android.moudles.futures.money.FuturesMoneyBean;
import com.fota.android.moudles.futures.money.FuturesMoneyListFragment;
import com.fota.android.moudles.futures.order.FuturesOrdersFragment;
import com.fota.android.moudles.market.FullScreenKlineActivity;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.fota.android.moudles.market.bean.HoldingEntity;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.utils.MoneyUtilsKt;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.utils.apputils.TradeUtils;
import com.fota.android.widget.btbwidget.FotaTextWatch;
import com.fota.android.widget.dialog.LeverDialog;
import com.fota.android.widget.popwin.FutureTopWindow;
import com.fota.android.widget.popwin.PasswordDialog;
import com.fota.android.widget.popwin.SpinerPopWindow3;
import com.fota.android.widget.popwin.WindowCloseListener;
import com.guoziwei.fota.chart.view.fota.FotaBigKLineBarChartView;
import com.guoziwei.fota.chart.view.fota.ImBeddedTimeLineBarChartView;
import com.guoziwei.fota.model.HisData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class FuturesFragment extends ExchangeFragment implements IFuturesUpdateFragment, FutureTradeView {

    private String spotPrice;
    private FutureTopInfoBean topInfo;
    private FutureTopWindow popupTopWindow;
    private BtbMap preciseMargin;

    private boolean isLeverChange = false;
    /**
     *
     * 【线上】【合约交易】保证金需求目前显示为实时刷新，应为下拉刷新
     */
    private boolean onRefreshDepthReqed;

    public static FuturesFragment newInstance(String assetName, FutureContractBean model, boolean isBuy) {
        Bundle args = new Bundle();
        args.putString(BundleKeys.KEY, assetName);
        args.putSerializable(BundleKeys.MODEL, model);
        args.putBoolean("isBuy", isBuy);
        FuturesFragment fragment = new FuturesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //chart relative
    /**
     * 现货指数数据
     * Spot -- T 15m
     */
    final List<HisData> spot15Data = new ArrayList<>(100);
    /**
     * 现货指数数据
     * Spot--K
     */
    final List<HisData> spotData = new ArrayList<>(100);

    @Override
    public void updateInstance(String assetName, FutureContractBean model, boolean isBuy) {
        this.isBuy = isBuy;
        getPresenter().setBasePrecision("-1");
        clearEditText();
        refreshBuy();
        getPresenter().setFromHq(assetName, model);
        onRefresh();
    }

    @Override
    protected FuturesPresenter createPresenter() {
        return new FuturesPresenter(this);
    }

    @Override
    public FuturesPresenter getPresenter() {
        return (FuturesPresenter) super.getPresenter();
    }

    @Override
    protected boolean isFutures() {
        return true;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        mHeadBinding.kline.setChartType(FotaBigKLineBarChartView.ChartType.FUTURE);
        mHeadBinding.tline.setChartType(ImBeddedTimeLineBarChartView.ChartType.FUTURE);
//        mHeadBinding.tline.setChartType(ImBeddedTimeLineBarChartView.ChartType.USDT);
//        mHeadBinding.tline.setChartType(FotaBigTimeLineBarChartView.ChartType.SPOT);

        int color = AppConfigs.isWhiteTheme() ? getThemeColor(R.attr.font_color) : getThemeColor(R.attr.font_color3);
        mHeadBinding.futuresTvDate.setTextColor(color);

        UIUtil.setRectangleBorderBg(mHeadBinding.futuresTvLever, color);
        mHeadBinding.futuresTvLever.setTextColor(color);

        mHeadBinding.fprogress.setLever(false);
        mHeadBinding.fprogress.setLeverChangeListener(new Function1<Integer, Unit>() {
            @Override
            public Unit invoke(Integer rate) {

                if (!FotaApplication.getLoginSrtatus()){
                    return null;
                }
                String price;
                if (isLimit){
                    price = mHeadBinding.price2.getText().toString();
                }else {
                    price = getCurrentPrice();
                }


                String moneyUnit = MoneyUtilsKt.divide(price, getLevel(), getPricePrecision());
                String amountTotal = MoneyUtilsKt.divide(topInfo.getAvailable(), moneyUnit, getAmountPrecision());
                String amount = MoneyUtilsKt.mul(amountTotal, rate / 100f + "");
                mHeadBinding.amount2.setText(new BigDecimal(amount).setScale(getAmountPrecision(), BigDecimal.ROUND_HALF_UP).toPlainString());
                return null;
            }
        });
        //GradientDrawableUtils.setBoardColor(mHeadBinding.futuresTvDate, color);
    }

    @Override
    protected void initViewPager() {
        //jiang
        mHeadBinding.rightContainer.setUnit(2);

        fragments = new ArrayList<>();
        fragments.add(new FuturesMoneyListFragment());
        fragments.add(new FuturesOrdersFragment());
        fragments.add(new FuturesCompleteFragment());
        List<String> title = new ArrayList<>();
        title.add(getXmlString(R.string.exchange_money));
        title.add(getXmlString(R.string.exchange_order));
        title.add(getXmlString(R.string.exchange_complete));
        BaseFragmentAdapter baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager(),
                fragments, title
        );
        mHeadBinding.viewPager.setAdapter(baseFragmentAdapter);
        mHeadBinding.viewPagerTitle.initTitles(title);
        mHeadBinding.viewPagerTitle.bindViewpager(mHeadBinding.viewPager);
        mHeadBinding.viewPager.setOffscreenPageLimit(2);
    }

    @Override
    protected void initListenter() {
        //super.initListenter();
        priceTextchange = new FotaTextWatch() {
            @Override
            protected void onTextChanged(String s) {
                validMaxValue();

                if (isLeverChange) {
                    isLeverChange = false;
                    return;
                }



                if (!FotaApplication.getLoginSrtatus()){
                    return;
                }
                String price;
                if (isLimit){
                    price = mHeadBinding.price2.getText().toString();
                }else {
                    price = getCurrentPrice();
                }


                String moneyUnit = MoneyUtilsKt.divide(price, getLevel(), getPricePrecision());
                String amountTotal = MoneyUtilsKt.divide(topInfo.getAvailable(), moneyUnit, getAmountPrecision());

                String rate = MoneyUtilsKt.divide(mHeadBinding.amount2.getText().toString(), amountTotal, 2);
                mHeadBinding.fprogress.setProgress(Math.round(Float.valueOf(rate) * 100));
            }
        };

        mHeadBinding.price2.addTextChangedListener(priceTextchange);
        mHeadBinding.amount2.addTextChangedListener(priceTextchange);
    }

//    @Override
//    protected void validMaxInfo() {
//        validMaxMinPrice();
//        validMaxValue();
//    }

    /**
     * 和价格挂钩
     * 和可用保证金挂钩
     */
    private void validMaxValue() {
        boolean priceOk = true;
        if (isLimit) {
            priceOk = Pub.GetDouble(mHeadBinding.price2.getText().toString()) > 0;
        }
        boolean valueOk = Pub.GetDouble(mHeadBinding.amount2.getText().toString()) > 0;
        if (UserLoginUtil.havaUser() && priceOk && valueOk) {
            insertFormInfo();
            //获取保证金需要
            getPresenter().preciseMargin();
        } else {
            mHeadBinding.maxAmount2.setText("--");
            mHeadBinding.preciseMargin.setText("--");
        }

    }


    @Override
    protected void changeWidth() {
        mHeadBinding.priceType2.post(new Runnable() {
            @Override
            public void run() {

                int textWith = (int) UIUtil.getTextWidth(getContext(), getXmlString(R.string.exchange_his_price), 14);

                int width = UIUtil.dip2px(getContext(), 12) + textWith;

                ViewGroup.LayoutParams lpPrice = mHeadBinding.priceType2.getLayoutParams();
                lpPrice.width = width;
                mHeadBinding.priceType.setLayoutParams(lpPrice);

                ViewGroup.LayoutParams lpMoney = mHeadBinding.amount2Tip.getLayoutParams();
                lpMoney.width = width;
                mHeadBinding.amount2Tip.setLayoutParams(lpMoney);
            }
        });
    }

    /**
     * 最高限卖价
     * 和买卖方向挂钩
     * 和现货指数挂钩
     */
    private void validMaxMinPrice() {
        double priceDiv = isBuy ? Pub.GetDouble(spotPrice) * 1.05 : Pub.GetDouble(spotPrice) * 0.95;
        mHeadBinding.maxUsdt2Tip.setText(isBuy ? getXmlString(R.string.exchange_heigh_price) : getXmlString(R.string.exchange_low_price));
        // Buy DOWN  Sell FLOOR
        mHeadBinding.maxUsdt2.setText(Pub.getPriceFormat(priceDiv, getContractMaxMinPricePrecision()
                , isBuy ? RoundingMode.DOWN : RoundingMode.UP));
    }

    private double getPrice() {
        double price = Pub.GetDouble(mHeadBinding.price2.getText().toString());
        if (!isLimit) {
            return Pub.GetDouble(mHeadBinding.rightContainer.getCurrentPrice());
        }
        return price;
    }

    @Override
    public double getMaxMoney() {
        if (topInfo == null) {
            return 0;
        }
        return isBuy ? Pub.GetDouble(topInfo.getMaxAskAmount()) : Pub.GetDouble(topInfo.getMaxBidAmount());
    }

    @Override
    public void refreshCurrency() {
        if (getPresenter().getSelectContact() == null) {
            return;
        }
        mHeadBinding.money2Unit.setText(getPresenter().getSelectContact().getAssetName());
        mHeadBinding.amount2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(getAmountPrecision())});
        mHeadBinding.price2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(getPricePrecision())});

        //jiang
        mHeadBinding.rightContainer.resetTicker();
        mHeadBinding.rightContainer.setType(2, getPresenter().getSelectContact().getContractName());
        mHeadBinding.futuresCurrency.setText(getPresenter().getSelectContact().getContractName());
        mHeadBinding.futuresTvLever.setText("X" + getPresenter().getSelectContact().getLever());
        if (Pub.isListExists(fragments)) {
            if (fragments.get(0) instanceof FuturesMoneyListFragment) {
                ((FuturesMoneyListFragment) (fragments.get(0))).adapterNotifyDataSetChanged();
            }
        }
        Glide.with(getContext()).load(getPresenter().getSelectParent().getIconUrl())
                .into(mHeadBinding.futureIcon);

        if (Pub.isStringEmpty(mHeadBinding.amount2.getText().toString())) {
            mHeadBinding.amount2.setText(getDefaultAmount());
        }

    }

    @Override
    public void onRefreshTicker(CurrentPriceBean price) {
        super.onRefreshTicker(price);
        currentPrice = price.getPrice();
        //受市场价影响
        if (!isLimit) {
            //暂时不需要设置
            //mHeadBinding.price2.setText(mHeadBinding.price.getText().toString());
        }
        if (mHeadBinding.imbedSpot.getVisibility() != View.VISIBLE) {
            mHeadBinding.imbedSpot.setVisibility(View.VISIBLE);
        }
    }

    private String currentPrice = "0.00";

    public String getCurrentPrice() {
        return currentPrice;
    }

    @Override
    public String getAssetId() {
        if (getPresenter() == null || getPresenter().getSelectContact() == null) {
            return "";
        }
        return getPresenter().getSelectContact().contractId;
    }

    @Override
    public void onRefreshTimelineData() {
        resetFreshTimelineData();
    }

    @Override
    public void resetFreshTimelineData() {
        setDelievery();
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromTimesByType(2);
        List<ChartLineEntity> spotList = FotaApplication.getInstance().getListFromTimesByType(1);
//        freshChartView(chartList, spotList);
        //合约去掉现货指数
        freshChartView(chartList, null);
        //当前现货指数
        if (spotList != null && spotList.size() > 0) {
            int length = spotList.size();
            double price = spotList.get(length - 1).getClose();
            spotPrice = price + "";
            String tempSpotPrice = "";
            HoldingEntity entity = FotaApplication.getInstance().getHoldingEntity();
            tempSpotPrice = Pub.getPriceStringForLengthRound(price, entity.getDecimal());
            //更新下文本的内容
            UIUtil.setText(mHeadBinding.imbedTickerSpot, tempSpotPrice);
        }
        validMaxMinPrice();
    }

    private void freshChartView(List<ChartLineEntity> chartList, List<ChartLineEntity> spotList) {
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
        spotData.clear();
        if (spotList != null) {
            for (int i = 0; i < spotList.size(); i++) {
                ChartLineEntity m = spotList.get(i);
                HisData data = BeanChangeFactory.createNewHisData(m);
                spotData.add(data);
            }
        }
        mHeadBinding.imbedNarrowChart.initData(time15Data, spotData);
        HoldingEntity entity = FotaApplication.getInstance().getHoldingEntity();
        mHeadBinding.tline.setmDigits(entity.getDecimal());
        mHeadBinding.tline.initData(time15Data, spotData);
        if (entity.getHoldingPrice() != -1) {
            mHeadBinding.tline.setLimitLine(entity.getHoldingPrice(), entity.getHoldingDescription());
        }
        if (time15Data != null && time15Data.size() > 0) {
            double hour24Close = time15Data.get(0).getClose();
            mHeadBinding.tline.setLastClose(hour24Close);
        }
    }

    @Override
    public void onRefreshKlineData(boolean isAdd) {
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromKlinesByType(2);
        if (chartList == null || chartList.size() == 0) {
            return;
        }
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        if (holdingEntity != null && holdingEntity.getHoldingPrice() != -1) {
            mHeadBinding.kline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
        }
        if (isAdd) {//add 直接重刷
            klineDataConvert(chartList);
//            mHeadBinding.kline.addData(klineData, spotData);
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

            List<ChartLineEntity> spotList = FotaApplication.getInstance().getListFromKlinesByType(1);
            if (spotList != null && spotList.size() > 0) {
                int spotSize = spotList.size();
                ChartLineEntity m1 = spotList.get(spotSize - 1);
                HisData data1 = BeanChangeFactory.createNewHisData(m1);
                lastSpot = (float) data1.getClose();
            }

            mHeadBinding.kline.refreshData(data, volume, lastSpot);
        }
    }

    @Override
    public void resetFreshKlineData() {
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromKlinesByType(2);
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        if (chartList == null) {
            onNoDataCallBack(1);
            return;
        }
        klineDataConvert(chartList);
        mHeadBinding.kline.setNeedMoveToLast(true);
        mHeadBinding.kline.setmDigits(holdingEntity.getDecimal());
//        mHeadBinding.kline.initData(klineData, spotData);
        mHeadBinding.kline.initData(klineData, null);

        if (holdingEntity != null && holdingEntity.getHoldingPrice() != -1) {
            mHeadBinding.kline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
        }
        if (time15Data != null && time15Data.size() > 0) {
            double hour24Close = time15Data.get(0).getClose();
            mHeadBinding.kline.setLastClose(hour24Close);
        }
    }


    @Override
    public void notifyFromPresenter(int action) {
        switch (action) {
            case ORDER_SUCCESS:
                mHeadBinding.amount2.setText(getDefaultAmount());
                break;
            default:
                super.event(action);
                break;
        }
    }

    @Override
    protected void klineDataConvert(List<ChartLineEntity> chartList) {
        super.klineDataConvert(chartList);

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

    @Override
    public void fullScreen() {
        if (getPresenter() == null || getPresenter().getSelectContact() == null) {
            return;
        }
        Intent intent = new Intent(mActivity, FullScreenKlineActivity.class);
        Bundle args = new Bundle();
        args.putString("symbol", getPresenter().getSelectContact().getContractName());
        int id = Pub.GetInt(getPresenter().getSelectContact().getContractId());
        args.putInt("id", id);
        args.putInt("type", 2);
        args.putInt("period", currentPeriodIndex);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void onRefreshDeliveryOrHold() {
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        if (holdingEntity != null && holdingEntity.getHoldingPrice() != -1) {
            mHeadBinding.kline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
            mHeadBinding.tline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
        }

        setDelievery();
        //由3 交割中 -- 2正常，need onRefresh ，然后复原
        if (holdingEntity.isStatusChange()) {
            onRefresh();
            holdingEntity.setStatusChange(false);
        }

//        entityId = holdingEntity.getId();
//        futureStr = holdingEntity.getName();
//        FutureItemEntity entity = new FutureItemEntity(futureStr);
//        entity.setEntityId(entityId);
//        entity.setEntityType(entityType);
//        getPresenter().setBean(entity);
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
        mHeadBinding.futuresTvDate.setText(tips);
    }

    LeverDialog leverDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.futures_tv_lever:


                if (!FotaApplication.getLoginSrtatus()){
                    FtRounts.toLogin(requireContext());
                    return;
                }
                leverDialog = new LeverDialog(requireContext());
                leverDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPresenter().setLever(Integer.valueOf(getAssetId()), getAssetName(), leverDialog.getLever());
                    }
                });
                leverDialog.show();
                leverDialog.setLever(Integer.valueOf(getLevel()));
                break;
            case R.id.img_type_change2:
                isKline = !isKline;
                if (isKline) {
                    mHeadBinding.imgTypeChange2.setImageResource(Pub.getThemeResource(getContext(), R.attr.chart_time_line));
                } else {
                    mHeadBinding.imgTypeChange2.setImageResource(Pub.getThemeResource(getContext(), R.attr.chart_kline));
                }
                changeKtline();
                break;
            case R.id.amount2_tip:
                mHeadBinding.amount2.requestFocus();
                KeyBoardUtils.openKeybord(mHeadBinding.amount2, getContext());
                break;
            case R.id.futures_top_info:
                showTopPop();
                break;
            case R.id.futures_change_currency:
            case R.id.futures_currency:
                showPopWindow();
                break;
            case R.id.select_buy2:
                isBuy = true;
                refreshBuy();
                break;
            case R.id.select_sell2:
                isBuy = false;
                refreshBuy();
                break;
            case R.id.btn_buy_sell2:
                if (!UserLoginUtil.havaUser()) {
                    UserLoginUtil.checkLogin(getContext());
                    return;
                }
                if (isLimit) {
                    if (Pub.GetDouble(mHeadBinding.price2.getText().toString()) <= 0) {
                        showToast(R.string.exchange_toast_inputprice);
                        return;
                    }
                }
                if (Pub.GetDouble(mHeadBinding.amount2.getText().toString()) <= 0) {
                    showToast(R.string.future_empty_value);
                    return;
                }
                verifyPassword();
                break;
            case R.id.price_type2:
            case R.id.price_other_2:
                isLimit = !isLimit;
                refreshPriceType();
                break;
            case R.id.iv_calc:
                Intent calcIntent = new Intent(requireContext(), FuturesCalcActivity.class);
                calcIntent.putExtra("coinName", getPresenter().getSelectContact().getAssetName());
                calcIntent.putExtra("amountPercision", getAmountPrecision());
                startActivity(calcIntent);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showTopPop() {
        mHeadBinding.futuresTopInfoArrow.reverse();
        if (popupTopWindow == null) {
            popupTopWindow = new FutureTopWindow(getContext());
        }
        popupTopWindow.setCloseListener(new WindowCloseListener() {
            @Override
            public void dismiss() {
                mHeadBinding.futuresTopInfoArrow.reset();
            }
        });
        popupTopWindow.showAsDropDown(
                mHeadBinding.futuresTopInfo, topInfo
        );
    }

    @Override
    public void onRefreshDepth(List<EntrustBean> buys, List<EntrustBean> sells, List<String> precisions, Float dollarEvaluation) {
        super.onRefreshDepth(buys, sells, precisions, dollarEvaluation);
        //受市场价影响
//        if (!isLimit) {
//            mHeadBinding.price2.setText(mHeadBinding.price.getText().toString());
//        }
        if (!onRefreshDepthReqed) {
            validMaxValue();
            onRefreshDepthReqed = true;
        }
    }


    /**
     * 0-对手价，1-指定价
     * 刷新市价和现价
     */
    protected void refreshPriceType() {
        UIUtil.setVisibility(mHeadBinding.priceOther2, !isLimit);
        UIUtil.setVisibility(mHeadBinding.price2, isLimit);
        UIUtil.setVisibility(mHeadBinding.price2Unit, isLimit);
        UIUtil.setText(mHeadBinding.priceTypeTv2, isLimit ? getString(R.string.exchange_limit_price) : getString(R.string.exchange_his_price));
        validMaxValue();
    }

    @Override
    protected void tradeToPresenter(String fundCode) {
        //1-限价单, 2-市价单
        insertFormInfo();
        getPresenter().submit(fundCode);
    }

    private void insertFormInfo() {
        getPresenter().getModel().setPriceType(isLimit ? 1 : 2);
        if (isLimit) {
            getPresenter().getModel().setPrice(mHeadBinding.price2.getText().toString());
        } else {
            getPresenter().getModel().setPrice(null);
        }
        getPresenter().getModel().setEntrustValue(mHeadBinding.amount2.getText().toString());
    }


    @Override
    protected void refreshBuy() {

        GradientDrawableUtils.setBgAlpha(mHeadBinding.selectBuy2, 30);
        GradientDrawableUtils.setBgAlpha(mHeadBinding.selectSell2, 30);

        GradientDrawableUtils.setBgColor(mHeadBinding.selectBuy2, isBuy ? AppConfigs.getUpColor() : Pub.getColor(getContext(), R.attr.bg_color));

        mHeadBinding.selectBuy2.setTextColor(isBuy ? AppConfigs.getUpColor() : Pub.getColor(getContext(), R.attr.font_color4));

        GradientDrawableUtils.setBgColor(mHeadBinding.selectSell2, !isBuy ? AppConfigs.getDownColor() : Pub.getColor(getContext(), R.attr.bg_color));
        mHeadBinding.selectSell2.setTextColor(!isBuy ? AppConfigs.getDownColor() : Pub.getColor(getContext(), R.attr.font_color4));

        UIUtil.setRoundCornerBg(mHeadBinding.btnBuySell2, AppConfigs.getColor(isBuy));

        getPresenter().getModel().setIsBuy(isBuy);

        mHeadBinding.priceOther2.setText(isBuy ? getXmlString(R.string.exchange_optimal)
                : getXmlString(R.string.exchange_optimal));

        mHeadBinding.btnBuySell2.setText(isBuy ? getXmlString(R.string.exchange_buy_wishheigh)
                : getXmlString(R.string.exchange_sell_wishlow));



        validMaxMinPrice();

        //validMaxValue(); 直接设置setPreciseMargin(preciseMargin);

        setPreciseMargin(preciseMargin);
    }

    /**
     * 获取当前价格的小数位数
     *
     * @return
     */
    public int getPricePrecision() {
        if (getPresenter().getSelectParent() != null) {
            return getPresenter().getSelectParent().getContractTradePricePrecision();
        }
        return 2;
    }

    /**
     * 获取当前价格的小数位数
     *
     * @return
     */
    public String getDefaultAmount() {
        if (getPresenter().getSelectParent() != null) {
            return getPresenter().getSelectParent().getDefaultAmount();
        }
        return "";
    }

    /**
     * 获取当前数量的小数位数
     *
     * @return
     */
    public int getAmountPrecision() {
        if (getPresenter().getSelectParent() != null) {
            return getPresenter().getSelectParent().getContractTradeAmountPrecision();
        }
        return 2;
    }

    /**
     * 最低现买价
     *
     * @return
     */
    public int getContractMaxMinPricePrecision() {
        if (getPresenter().getSelectParent() != null) {
            return getPresenter().getSelectParent().getContractMaxMinPricePrecision();
        }
        return 1;
    }

    /**
     * 获取当前数量的小数位数
     *
     * @return
     */
    public int getContractMaxValuePrecision() {
        return 3;
    }

    @Override
    protected void showPopWindow() {
        mHeadBinding.futuresChangeCurrencyArrow.reverse();
        if (!Pub.isListExists(getPresenter().getAllList())) {
            return;
        }
        SpinerPopWindow3 popupWindow = new SpinerPopWindow3(getContext());
        popupWindow.setOnPopListener(new SpinerPopWindow3.OnPopClickListener() {

            @Override
            public void onPopClick(FtKeyValue ftKeyValue, int position) {
                FutureContractBean model = (FutureContractBean) ftKeyValue;
                KeyBoardUtils.closeKeybord(getContext());
                clearEditText();
                getPresenter().setSelectContact(model.getParent(), model);
            }
        });

        popupWindow.setCloseListener(new WindowCloseListener() {
            @Override
            public void dismiss() {
                mHeadBinding.futuresChangeCurrencyArrow.reset();
            }
        });
        popupWindow.showAsDropDown(
                mHeadBinding.futuresTitle,
                getPresenter().getAllList(),
                getPresenter().getSelectParent(),
                getPresenter().getSelectContact()
        );
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        onRefreshDepthReqed = false;
        if (!UserLoginUtil.havaUser()) {
            //setTopInfo();
            topInfo = null;
            UIUtil.setText(mHeadBinding.preciseMargin, "--");
        }
        setTopInfo();
        getPresenter().getContactList();
//        if (Pub.isListExists(fragments)) {
//            for (Fragment fragment : fragments
//                    ) {
//                if (fragment instanceof BaseFragment) {
//                    ((BaseFragment) fragment).onRefresh();
//                }
//            }
//        }
    }

    @Override
    public void setContractAccount(FutureTopInfoBean map) {
        //保证金率
        this.topInfo = map;
        setTopInfo();
        if (popupTopWindow != null) {
            popupTopWindow.setData(map);
        }
        //validMaxValue();
    }

    private void setTopInfo() {
        if (topInfo == null) {
            mHeadBinding.futuresRights.setText("--");
            mHeadBinding.futuresFdYk.setText("--");
            return;
        }
        mHeadBinding.futuresRights.setText(topInfo.getTotal());
        mHeadBinding.futuresFdYk.setText(topInfo.getFloatProfit());
    }

    public void removeMoney(final FuturesMoneyBean model) {
        TradeUtils.getInstance().validPassword(getContext(), mRequestCode, new TradeUtils.ExchangePasswordListener() {

            @Override
            public void noPassword() {
                getPresenter().removeMoney(model, UserLoginUtil.getCapital());
            }

            @Override
            public void showPasswordDialog() {
                if (getHoldingActivity().isFinishing()) {
                    return;
                }
                PasswordDialog dialog = new PasswordDialog(getContext());
                dialog.setListener(new PasswordDialog.OnSureClickListener() {
                    @Override
                    public void onClick(String fundCode) {
                        TradeUtils.getInstance().changePasswordToToken(getContext(), fundCode,

                                new TradeUtils.ChangePassWordListener() {
                                    @Override
                                    public void setPasswordToken(String token) {
                                        getPresenter().removeMoney(model, token);
                                    }
                                });
                    }
                });
                dialog.show();
            }
        });


    }

    @Override
    public void setContractDelivery(BtbMap map) {

    }

    @Override
    public void setPreciseMargin(BtbMap map) {
        this.preciseMargin = map;
        if (map == null) {
            UIUtil.setText(mHeadBinding.preciseMargin, "--");
            return;
        }
        if (isBuy) {
            UIUtil.setText(mHeadBinding.preciseMargin, map.get("bid"), "--");
        } else {
            UIUtil.setText(mHeadBinding.preciseMargin, map.get("ask"), "--");
        }
    }

    @Override
    public void onLeverChange() {

        isLeverChange = true;
        leverDialog.dismiss();
        String price;
        if (isLimit){
            price = mHeadBinding.price2.getText().toString();
        }else {
            price = getCurrentPrice();
        }


        String moneyUnit = MoneyUtilsKt.divide(price, leverDialog.getLever()+"", getPricePrecision());
        String amountTotal = MoneyUtilsKt.divide(topInfo.getAvailable(), moneyUnit, getAmountPrecision());
        String amount = MoneyUtilsKt.mul(amountTotal, (float) mHeadBinding.fprogress.getLever() / 100f + "");

//        Log.i("nidongliang", "unit: " + moneyUnit + "aamount: " + amountTotal + "")
        mHeadBinding.amount2.setText(new BigDecimal(amount).setScale(getAmountPrecision(), BigDecimal.ROUND_HALF_UP).toPlainString());

    }

    @Override
    protected void clearEditText() {
        mHeadBinding.price2.setText("");
        mHeadBinding.amount2.setText("");
        mHeadBinding.exchangeChangeCurrencyArrow.requestFocus();
//        KeyBoardUtils.closeKeybord(mHeadBinding.price2, getContext());
//        KeyBoardUtils.closeKeybord(mHeadBinding.amount2, getContext());
    }

    @Override
    public void onClickItem(EntrustBean bean) {
        isLimit = true;
        refreshPriceType();
        validMaxValue();
        mHeadBinding.price2.setText(bean.getPrice());
    }

    @Override
    public void onTickClick(CurrentPriceBean currentPrice) {
        isLimit = true;
        refreshPriceType();
        //validMaxValue();
        mHeadBinding.price2.setText(currentPrice.getPrice());
    }

    public String getLevel() {
        if (getPresenter().getSelectContact() != null) {
            return getPresenter().getSelectContact().getLever();
        }
        return "10";
    }
}
