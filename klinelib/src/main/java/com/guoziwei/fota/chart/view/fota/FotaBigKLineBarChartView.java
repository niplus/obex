package com.guoziwei.fota.chart.view.fota;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonPub;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.guoziwei.fota.R;
import com.guoziwei.fota.chart.AppCombinedChart;
import com.guoziwei.fota.chart.FotaCombinedBarChart;
import com.guoziwei.fota.chart.FotaCombinedChart;
import com.guoziwei.fota.chart.FotaCoupleChartGestureListener;
import com.guoziwei.fota.chart.FotaInfoViewListener;
import com.guoziwei.fota.chart.FotaTimeBaseRenderChart;
import com.guoziwei.fota.chart.OnLoadEdgeListener;
import com.guoziwei.fota.chart.render.ColorContentYAxisRenderer;
import com.guoziwei.fota.chart.view.BaseChartView;
import com.guoziwei.fota.chart.view.ChartInfoView;
import com.guoziwei.fota.model.HisData;
import com.guoziwei.fota.util.DataUtils;
import com.guoziwei.fota.util.DoubleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * kline + timeline + volume + else indicate line
 * Created by jiang on 2018/08/04.
 */
public class FotaBigKLineBarChartView extends BaseChartView implements FotaCoupleChartGestureListener.OnAxisChangeListener {

    private int maxKline = 100;
    private int minKline = 10;
    private int initKline = 50;

    public void setNeedMoveToLast(boolean needMoveToLast) {
        this.needMoveToLast = needMoveToLast;
        this.isAdd = false;
    }

    //是否需要移动到最后节点
    private boolean needMoveToLast;
    //是否是ws推送，add新数据
    private boolean isAdd;

    public enum ChartType {
        FUTURE,
        USDT
    }

    private ChartType chartType;

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
        mChartVolume.setType(chartType == ChartType.FUTURE ? 1 : 0);
        setLegendLoading(true, chartType == ChartType.FUTURE ? 2 : 3);
        ((ComplexChartInfoView) mChartInfoView).setType(chartType == ChartType.FUTURE ? 2 : 3);
    }

    public void setFromTrade(boolean isFromTrade) {
        this.isFromTrade = isFromTrade;
        if(isFromTrade) {
            ((ComplexChartInfoView) mChartInfoView).setFromTrade(true);
        }
    }

    public static final int BAR_LINE = 0;

    public static final int K_LINE = 1;

    public static final int SPOT_LINE = 2;
    /**
     * hide line
     */
    public static final int INVISIABLE_LINE = 6;

    protected FotaCombinedChart mChartPrice;
    protected FotaCombinedBarChart mChartVolume;

    protected ChartInfoView mChartInfoView;
    protected Context mContext;

    /**
     * last price
     */
    private double mLastPrice;
    private double mLastVolume;

    /**
     * yesterday close price
     */
    private double mLastClose;
    private FotaInfoViewListener fotaInfoViewListener;
    private FotaCoupleChartGestureListener mCoupleChartGestureListener;

    public FotaBigKLineBarChartView(Context context) {
        this(context, null);
    }

    public FotaBigKLineBarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FotaBigKLineBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_kline_time_complex, this);
        mChartPrice = (FotaCombinedChart) findViewById(R.id.price_chart);
        mChartVolume = (FotaCombinedBarChart) findViewById(R.id.vol_chart);
        mChartInfoView = (ChartInfoView) findViewById(R.id.k_info);
        mChartInfoView.setChart(mChartPrice, mChartVolume);

        mChartPrice.setNoDataText(context.getString(R.string.loading));
        initChartPrice();
        initBottomChart(mChartVolume);
        mTimeBase = (FotaTimeBaseRenderChart) findViewById(R.id.time_chart);
        initBottomTimeBase(mTimeBase);
        setOffset(mChartPrice, mChartVolume);
        initChartListener();
    }

    protected void initBottomTimeBase(CombinedChart chart) {
        super.initBottomTimeBase(chart);

    }

    protected void initBottomChart(AppCombinedChart chart) {
        super.initBottomChart(chart);
        chart.setScaleXEnabled(false);
        chart.setScaleYEnabled(false);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.getAxisLeft().setEnabled(true);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisRight().setDrawAxisLine(false);
    }

    protected void initChartPrice() {
        mChartPrice.setScaleEnabled(true);
        mChartPrice.setDrawBorders(false);
        mChartPrice.setBorderWidth(1);
        mChartPrice.setDragEnabled(true);
        mChartPrice.setScaleYEnabled(false);
        mChartPrice.setHighlightPerTapEnabled(false);
        //jiang 0614
        mChartPrice.setAutoScaleMinMaxEnabled(true);
        mChartPrice.setDragDecelerationEnabled(true);
        mChartPrice.setDragDecelerationFrictionCoef(0.79f);
        mChartPrice.setTouchEnabled(true);
        //jiang
        mLastYSpotMarker = new FotaChartYSpotMarkerView(mContext, mDigits);
        mChartPrice.setmLastYSpotMarker(mLastYSpotMarker);
        mLastYFutureMarker = new FotaChartYFutureMarkerView(mContext, mDigits);
        mChartPrice.setmLastYFutureMarker(mLastYFutureMarker);
        Legend lineChartLegend = mChartPrice.getLegend();
        lineChartLegend.setEnabled(false);

        XAxis xAxisPrice = mChartPrice.getXAxis();
        xAxisPrice.setEnabled(false);

        YAxis axisLeftPrice = mChartPrice.getAxisLeft();
        axisLeftPrice.setEnabled(true);
        axisLeftPrice.setLabelCount(6, true);
        axisLeftPrice.setDrawLabels(true);
        axisLeftPrice.setDrawGridLines(true);
        axisLeftPrice.setGridColor(mGridColor);

        axisLeftPrice.setDrawAxisLine(false);
        axisLeftPrice.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftPrice.setTextColor(mAxisColor);
        axisLeftPrice.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DoubleUtil.getStringByDigits(value, mDigits);
            }
        });

        int[] colorArray = {mAxisColor, mAxisColor, mAxisColor, mAxisColor, mAxisColor};
        Transformer leftYTransformer = mChartPrice.getRendererLeftYAxis().getTransformer();
        ColorContentYAxisRenderer leftColorContentYAxisRenderer = new ColorContentYAxisRenderer(mChartPrice.getViewPortHandler(), mChartPrice.getAxisLeft(), leftYTransformer);
        leftColorContentYAxisRenderer.setLabelInContent(true);
        leftColorContentYAxisRenderer.setUseDefaultLabelXOffset(false);
        leftColorContentYAxisRenderer.setLabelColor(colorArray);
        //jiang 0928
        leftColorContentYAxisRenderer.setmXMarker(mvx);
        mChartPrice.setRendererLeftYAxis(leftColorContentYAxisRenderer);


        YAxis axisRightPrice = mChartPrice.getAxisRight();
        axisRightPrice.setEnabled(true);
        axisRightPrice.setLabelCount(6, true);
        axisRightPrice.setDrawLabels(true);
        axisRightPrice.setDrawGridLines(false);
        axisRightPrice.setDrawAxisLine(false);
        axisRightPrice.setTextColor(mAxisColor);
        axisRightPrice.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        axisRightPrice.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                double rate = (value - mLastClose) / mLastClose * 100;
//                if (Double.isNaN(rate) || Double.isInfinite(rate)) {
//                    return "";
//                }
//                String s = String.format(Locale.getDefault(), "%.2f%%",
//                        rate);
//                if (TextUtils.equals("-0.00%", s)) {
//                    return "0.00%";
//                }
//                return s;
                return DoubleUtil.getStringByDigits(value, mDigits);
            }
        });

//        设置标签Y渲染器
        Transformer rightYTransformer = mChartPrice.getRendererRightYAxis().getTransformer();
        ColorContentYAxisRenderer rightColorContentYAxisRenderer = new ColorContentYAxisRenderer(mChartPrice.getViewPortHandler(), mChartPrice.getAxisRight(), rightYTransformer);
        rightColorContentYAxisRenderer.setLabelInContent(true);
        rightColorContentYAxisRenderer.setUseDefaultLabelXOffset(false);
        rightColorContentYAxisRenderer.setLabelColor(colorArray);
        mChartPrice.setRendererRightYAxis(rightColorContentYAxisRenderer);
    }

    private void initChartListener() {
        mCoupleChartGestureListener = new FotaCoupleChartGestureListener(this, mChartInfoView, mChartPrice, mChartVolume, mTimeBase);
        mChartPrice.setOnChartGestureListener(mCoupleChartGestureListener);
        fotaInfoViewListener = new FotaInfoViewListener(mContext, mLastClose, mData, mChartPrice, mChartInfoView, mChartVolume);
        mChartPrice.setOnChartValueSelectedListener(fotaInfoViewListener);
        mChartPrice.setOnTouchListener(fotaInfoViewListener);
        mChartVolume.setOnChartGestureListener(new FotaCoupleChartGestureListener(this, mChartInfoView, mChartVolume, mChartPrice, mTimeBase));
    }

    public void initData(List<HisData> hisDatas) {
        mChartPrice.setmDigits(mDigits);
        ((ComplexChartInfoView)mChartInfoView).setDigits(mDigits);
        if (hisDatas == null || hisDatas.size() <= 0) {
            mChartPrice.clear();
            mChartVolume.clear();
            return;
        }
        mData.clear();
        mData.addAll(DataUtils.calculateHisData(hisDatas));
        mChartPrice.setRealCount(mData.size());

        ArrayList<CandleEntry> lineCJEntries = new ArrayList<>();
        ArrayList<Entry> paddingEntries = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            HisData hisData = mData.get(i);
            lineCJEntries.add(new CandleEntry(i, (float) hisData.getHigh(), (float) hisData.getLow(), (float) hisData.getOpen(), (float) hisData.getClose()));
        }

        if (!mData.isEmpty() && mData.size() < initKline) {
            for (int i = mData.size(); i < initKline; i++) {
                paddingEntries.add(new Entry(i, (float) mData.get(mData.size() - 1).getClose()));
            }
        }

        CandleData candleData = new CandleData(setKLine(K_LINE, lineCJEntries));
        CombinedData combinedData = new CombinedData();
        combinedData.setData(candleData);
        LineData lineData = new LineData(setLine(INVISIABLE_LINE, paddingEntries));
        combinedData.setData(lineData);
        mChartPrice.setData(combinedData);
        //jiang
//        mChartPrice.getData().setDrawValues(false);
        //jiang 0808 must after add data
        mChartPrice.setDrawLastCircle(true);

        initChartVolumeData();
        fixKlinePosition(combinedData);
    }

    private void initChartVolumeData() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> paddingEntries = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            HisData t = mData.get(i);
            barEntries.add(new BarEntry(i, (float) t.getVol(), t));
        }
        if (!mData.isEmpty() && mData.size() < initKline) {
            for (int i = mData.size(); i < initKline; i++) {
                paddingEntries.add(new BarEntry(i, 0));
            }
        }

        BarData barData = new BarData(setBar(barEntries, BAR_LINE), setBar(paddingEntries, INVISIABLE_LINE));
        barData.setBarWidth(0.75f);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        mChartVolume.setData(combinedData);
        mTimeBase.setData(combinedData);
    }

    private void fixKlinePosition(CombinedData combinedData) {
        mChartPrice.getXAxis().setAxisMaximum(combinedData.getXMax() + 0.5f);
        mChartVolume.getXAxis().setAxisMaximum(combinedData.getXMax() + 0.5f);
        mTimeBase.getXAxis().setAxisMaximum(combinedData.getXMax() + 0.5f);

        mChartPrice.setVisibleXRange(initKline, minKline);
        mChartVolume.setVisibleXRange(initKline, minKline);
        mTimeBase.setVisibleXRange(initKline, minKline);

        if(!isAdd) {
            mChartPrice.resetFotaZoom();
            mChartVolume.resetFotaZoom();
            mTimeBase.resetFotaZoom();
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    mChartPrice.invalidate();
                }
            });
        }
        mChartPrice.notifyDataSetChanged();
        mChartVolume.notifyDataSetChanged();
        mTimeBase.notifyDataSetChanged();

//        mChartPrice.zoom(maxKline * 1f / initKline, 0, 0, 0);
//        mChartVolume.zoom(maxKline * 1f / initKline, 0, 0, 0);
        moveToLast(mChartPrice);
        moveToLast(mChartVolume);
        moveToLast(mTimeBase);

        setDescription(mChartVolume, "");
        //jiang 20180506
        setDescription(mChartPrice, "");
        mChartPrice.invalidate();
        mChartVolume.invalidate();
        mTimeBase.invalidate();

        mChartPrice.setVisibleXRangeMaximum(maxKline);
        mChartVolume.setVisibleXRangeMaximum(maxKline);
        mTimeBase.setVisibleXRangeMaximum(maxKline);
    }

    public void initData(List<HisData> datas, List<HisData> indexDatas) {
        initData(datas);

        initSpotData(indexDatas);
    }

    private void initSpotData(List<HisData> datas) {
        if (datas == null || datas.size() <= 0) {
            return;
        }
        //spot 首尾是0 不画
        if(isHeadTailNull(datas)) {
            return;
        }
        ArrayList<Entry> indexEntries = new ArrayList<>();

        for (int i = 0; i < datas.size(); i++) {
            HisData hisData = datas.get(i);
            indexEntries.add(new Entry(i, (float) hisData.getClose()));
        }

        LineData lineData = mChartPrice.getLineData();
        if (lineData == null) return;
        lineData.addDataSet(setLine(SPOT_LINE, indexEntries));
        double lastSpotPrice = datas.get(datas.size() - 1).getClose();
        double lastFuturePrice = mData.get(mData.size() - 1).getClose();
        mChartPrice.setSpotMaxThanFuture(lastSpotPrice > lastFuturePrice);
    }

    private BarDataSet setBar(ArrayList<BarEntry> barEntries, int type) {
        BarDataSet barDataSet = new BarDataSet(barEntries, "vol");
        barDataSet.setHighLightAlpha(120);
        barDataSet.setHighLightColor(CommonPub.getColor(getContext(), R.attr.k_highlight_color));
        barDataSet.setDrawValues(false);
        barDataSet.setVisible(type != INVISIABLE_LINE);
        barDataSet.setHighlightEnabled(true);
        barDataSet.setColors(mIncreasingColor, mDecreasingColor);
        int red = getResources().getColor(R.color.red_color_tran);
        int green = getResources().getColor(R.color.green_color_trans);
        int downColor = AppConfigs.getRedup() ? green : red;
        int upColor = AppConfigs.getRedup() ? red : green;
        barDataSet.setColors(upColor, downColor);
        return barDataSet;
    }


    @NonNull
    private LineDataSet setLine(int type, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSet = new LineDataSet(lineEntries, type == 2 ? "spot" : "invisible");
        lineDataSet.setDrawValues(false);
        lineDataSet.setHighlightEnabled(false);
        if (type == SPOT_LINE) {
            lineDataSet.setDrawFilled(true);
            lineDataSet.setColor(getResources().getColor(R.color.spot_marker_color));
            lineDataSet.setCircleColor(ContextCompat.getColor(mContext, R.color.spot_marker_color));
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_spot_dark);
            lineDataSet.setFillDrawable(drawable);
            lineDataSet.setCircleRadius(1.5f);
            lineDataSet.setCircleColorHole(CommonPub.getColor(getContext(), R.attr.color_k_fota_bg));
        } else {
            lineDataSet.setVisible(false);
            lineDataSet.setHighlightEnabled(false);
        }
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(1f);

        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(true);

        return lineDataSet;
    }

    @NonNull
    public CandleDataSet setKLine(int type, ArrayList<CandleEntry> lineEntries) {
        CandleDataSet set = new CandleDataSet(lineEntries, "KLine" + type);
        set.setDrawIcons(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setShadowColor(Color.DKGRAY);
        set.setShadowWidth(0.75f);
        //jiang
        set.setDecreasingColor(mDecreasingColor);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setShadowColorSameAsCandle(true);
        set.setCircleColorHole(CommonPub.getColor(mContext, R.attr.color_k_fota_bg));
        set.setIncreasingColor(mIncreasingColor);
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        //jiang
        set.setNeutralColor(mIncreasingColor);
        set.setDrawValues(true);
        set.setValueTextSize(10);
        set.setValueTextColor(CommonPub.getColor(getContext(), R.attr.k_highlight_color));
        set.setHighlightEnabled(true);
        set.enableDashedHighlightLine(5f, 5f, 0f);
        set.setHighLightColor(CommonPub.getColor(getContext(), R.attr.k_highlight_color));
        return set;
    }

    protected void moveToLast(CombinedChart chart) {
        if(needMoveToLast) {
            if (mData.size() > initKline) {
                chart.moveViewToX(mData.size() - initKline);
            }
        }
    }

    /**
     * according to the price to refresh the last data of the chart
     */
    public void refreshData(HisData price, float volume, float spotPrice) {
        CombinedData data = mChartPrice.getCombinedData();
        if (data == null) return;
        if (price == null || price.getClose() <= 0 || price.getClose() == mLastPrice) {

        } else {
            mLastPrice = price.getClose();
            CandleData candleData = data.getCandleData();
            if (candleData != null) {
                ICandleDataSet set = candleData.getDataSetByIndex(0);
                if (set.removeLast()) {
                    HisData hisData = mData.get(mData.size() - 1);
                    hisData.setClose(price.getClose());
//                    hisData.setHigh(Math.max(hisData.getHigh(), price));
                    hisData.setHigh(price.getHigh());
//                    hisData.setLow(Math.min(hisData.getLow(), price));
                    hisData.setLow(price.getLow());
                    hisData.setOpen(price.getOpen());
                    set.addEntry(new CandleEntry(set.getEntryCount(), (float) hisData.getHigh(), (float) hisData.getLow(), (float) hisData.getOpen(), (float) price.getClose()));
                }
            }
            mChartPrice.notifyDataSetChanged();
            mChartPrice.invalidate();
        }

        CombinedData volumeData = mChartVolume.getCombinedData();
        if (volumeData == null) return;
        if (volume <= 0 || volume == mLastVolume) {

        } else {
            mLastVolume = volume;
            BarData barData = volumeData.getBarData();
            if (barData != null) {
                IBarDataSet set = barData.getDataSetByIndex(0);
                if (set.removeLast()) {
                    HisData hisData = mData.get(mData.size() - 1);
                    hisData.setVol(volume);
                    set.addEntry(new BarEntry(set.getEntryCount(), volume, hisData));
                }
            }
            mChartVolume.notifyDataSetChanged();
            mChartVolume.invalidate();
        }

        LineData lineData = data.getLineData();
        if (lineData != null) {
            // 附加的指数线，两种情况 有 or 无
            ILineDataSet set = lineData.getDataSetByIndex(1);
            if(spotPrice > 0 && set != null) {
                if (set.removeLast()) {
                    set.addEntry(new Entry(set.getEntryCount(), spotPrice));
                    mChartPrice.setSpotMaxThanFuture(spotPrice > mLastPrice);
                    mChartPrice.notifyDataSetChanged();
                    mChartPrice.invalidate();
                }
            }
        }

    }


    public void addData(List<HisData> datas, List<HisData> indexDatas) {
        isAdd = true;
        needMoveToLast = false;
        initData(datas, indexDatas);
    }

    public void setLastClose(double lastClose) {
        mLastClose = lastClose;
        if (fotaInfoViewListener != null) {
            fotaInfoViewListener.setmLastClose(lastClose);
            mChartVolume.setOnChartValueSelectedListener(fotaInfoViewListener);
        }
    }

    public void setLimitLine(double value, String label) {
        setLimitLine(mChartPrice, value, label);
    }

    @Override
    public void onAxisChange(BarLineChartBase chart) {
        float lowestVisibleX = chart.getLowestVisibleX();
        if (lowestVisibleX <= chart.getXAxis().getAxisMinimum()) return;
        int maxX = (int) chart.getHighestVisibleX();
        int x = Math.min(maxX, mData.size() - 1);
        HisData hisData = mData.get(x < 0 ? 0 : x);
        //jiang 20180506
        setDescription(mChartPrice, "");
        setDescription(mChartVolume, "");
    }

    public void setOnEdgeListener(OnLoadEdgeListener l) {
        if (mCoupleChartGestureListener != null) {
            mCoupleChartGestureListener.setOnEdgeListener(l);
        }
    }
}
