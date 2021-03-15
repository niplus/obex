package com.fota.android.moudles.market;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.StringUtils;
import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.common.bean.BeanChangeFactory;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.core.base.MvpActivity;
import com.fota.android.databinding.ActivityKlineScreenBinding;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.moudles.market.bean.HoldingEntity;
import com.fota.android.utils.StatusBarUtil;
import com.fota.android.widget.KlinePopupWindow;
import com.fota.android.widget.KlineTitleBarLayout;
import com.guoziwei.fota.chart.OnLoadEdgeListener;
import com.guoziwei.fota.chart.view.BaseChartView;
import com.guoziwei.fota.chart.view.fota.FotaBigKLineBarChartView;
import com.guoziwei.fota.model.HisData;

import java.util.ArrayList;
import java.util.List;

/**
 * 全屏K线
 * 行情页面点击具体币对应的合约类型，
 * 进入该合约的k线界面
 * Created by jiang on 2018/08/014.
 */

public class FullScreenKlineActivity extends MvpActivity<FullScreenKlinePresenter>
        implements FullScreenKlineViewInterface,
        BaseChartView.KlinePeriodInterface, KlineTitleBarLayout.OnRightImage3ClickListener {

    ActivityKlineScreenBinding mBinding;

    @Override
    protected FullScreenKlinePresenter createPresenter() {
        return new FullScreenKlinePresenter(this);
    }

    //标题
    private String futureStr;
    private int entityId;
    private int entityType;

    //已选择的kline 周期参数
    private int currentPeriodIndex = 0;

    private KlineTitleBarLayout mKlineTabBar;
    private KlinePopupWindow mKlinePopupWindow;

    /**
     * 合约数据
     */
    final List<HisData> futureData = new ArrayList<>(100);

    /**
     * 现货指数数据
     */
    final List<HisData> spotData = new ArrayList<>(100);
//    static final String[] types = {"1m", "15m", "1h", "1d", };
//    static final String[] dateFormats = {"HH:mm", "HH:mm", "HH:mm", "yyyy-MM-dd"};
    static final String[] types = {"1m", "5m", "15m", "30m", "1h", "4h", "6h", "1d", "1w"};
    static final String[] dateFormats = {"HH:mm", "HH:mm", "HH:mm", "HH:mm", "HH:mm", "HH:mm", "HH:mm", "yyyy-MM-dd", "yyyy-MM-dd"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_kline_screen);
        //原有框架有坑，调用addFragment之后会重新走getExtras
        //所以采用这个方法
        getBundles();

        mKlineTabBar = (KlineTitleBarLayout) mBinding.barMarketsKline;
        StatusBarUtil.setPaddingSmart(getContext(), mKlineTabBar);
        String title = StringUtils.isEmpty(futureStr) ? "" : futureStr;

        mBinding.barMarketsKline.setTitle(title.toUpperCase());
        mBinding.barMarketsKline.setmLeftClickListener(new KlineTitleBarLayout.OnLeftClickListener() {
            @Override
            public void onLeftClick(View v) {
                finish();
            }
        });

        initViews();
        initData();
    }

    private void getBundles() {
        futureStr = getParams("symbol");
        entityId = getParams("id");
        entityType = getParams("type");
        currentPeriodIndex = getParams("period");

        FutureItemEntity entity = new FutureItemEntity(futureStr);
        entity.setEntityId(entityId);
        entity.setEntityType(entityType);
        getPresenter().setBean(entity);
    }

    private void initViews() {
        mBinding.kline.setDateFormat(dateFormats[currentPeriodIndex]);
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
        initChoosePopWindow();
    }

    private void initHeadBar() {
        mKlineTabBar.setmRightImage3ClickListener(this);
        mKlineTabBar.setIvMinifyVisible(true);
        if(entityType == 2)
            setDelievery();
    }

    private void setDelievery() {
        String tips = "";
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        String days = holdingEntity.getFutureLimtDays();
        if (holdingEntity.getStatus() == 3) {
            tips = getString(R.string.exchange_order_trading);
        } else if(!TextUtils.isEmpty(days)) {
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

    private void initChoosePopWindow() {
        mBinding.kline.setPeriodInterface(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        if (entityType == 2) {
            mBinding.kline.setChartType(FotaBigKLineBarChartView.ChartType.FUTURE);
        } else {
            mBinding.kline.setChartType(FotaBigKLineBarChartView.ChartType.USDT);
        }
        //
        getPresenter().getFullScreenKlineDatas(entityType, entityId, types[currentPeriodIndex]);
        if (mBinding.kline != null) {
            mBinding.kline.setLoading(true);
        }
    }

    /**
     * refreshList socket addition update data
     */
    //socket service callback
    @Override
    public void onRefreshKlineData(boolean isAdd) {
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromKlinesByType(entityType);
        if (chartList == null || chartList.size() == 0) {
            return;
        }
        if (isAdd) {//add 直接重刷
            klineDataConvert(chartList);
            if (entityType == 2) {
                mBinding.kline.addData(futureData, spotData);
            } else {
                mBinding.kline.addData(futureData, null);
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


    private void klineDataConvert(List<ChartLineEntity> chartList) {
        futureData.clear();
        for (int i = 0; i < chartList.size(); i++) {
            ChartLineEntity m = chartList.get(i);
            HisData data = BeanChangeFactory.createNewHisData(m);
            futureData.add(data);
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
    public void onRefreshDeliveryOrHold() {
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        if (holdingEntity != null && holdingEntity.getHoldingPrice() != -1) {
            mBinding.kline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
        }
        setDelievery();

        entityId = holdingEntity.getId();
        futureStr = holdingEntity.getName();
        FutureItemEntity entity = new FutureItemEntity(futureStr);
        entity.setEntityId(entityId);
        entity.setEntityType(entityType);
        getPresenter().setBean(entity);
    }

    /**
     *
     */
    @Override
    public void resetFreshKlineData() {
        List<ChartLineEntity> chartList = FotaApplication.getInstance().getListFromKlinesByType(entityType);
        HoldingEntity holdingEntity = FotaApplication.getInstance().getHoldingEntity();
        if (chartList == null) {
            onNoDataCallBack();
            return;
        }
        klineDataConvert(chartList);
        mBinding.kline.setNeedMoveToLast(true);
        mBinding.kline.setmDigits(holdingEntity.getDecimal());
        if (entityType == 2) {
            mBinding.kline.initData(futureData, spotData);
        } else {
            mBinding.kline.initData(futureData);
        }
        if (holdingEntity != null && holdingEntity.getHoldingPrice() != -1) {
            mBinding.kline.setLimitLine(holdingEntity.getHoldingPrice(), holdingEntity.getHoldingDescription());
        }
    }

    @Override
    public void onNoDataCallBack() {
        mBinding.kline.initData(null, null);
    }

    @Override
    public void setOverShowLoading(boolean isShowLoading) {
        if (mBinding.kline != null) {
            mBinding.kline.setLoading(isShowLoading);
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
            getPresenter().getFullScreenKlineDatas(entityType, entityId, types[index]);
            if (mBinding.kline != null) {
                mBinding.kline.setLoading(true);
            }
        }
    }

    @Override
    public void onRightMinifyClick(View v) {
        finish();
    }
}
