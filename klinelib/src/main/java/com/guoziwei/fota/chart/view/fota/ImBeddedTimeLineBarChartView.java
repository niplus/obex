package com.guoziwei.fota.chart.view.fota;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonPub;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
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
import com.guoziwei.fota.chart.FotaTimeBaseRenderChart;
import com.guoziwei.fota.chart.render.ColorContentYAxisRenderer;
import com.guoziwei.fota.chart.view.BaseChartView;
import com.guoziwei.fota.chart.view.ChartInfoView;
import com.guoziwei.fota.chart.view.LineChartXMarkerView;
import com.guoziwei.fota.model.HisData;
import com.guoziwei.fota.util.DataUtils;
import com.guoziwei.fota.util.DoubleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * kline + timeline + volume + else indicate line
 * Created by jiang on 2018/08/18.
 */
public class ImBeddedTimeLineBarChartView extends BaseChartView implements FotaCoupleChartGestureListener.OnAxisChangeListener {
    public int MAX_COUNT = 96;
    public int MIN_COUNT = 10;
    public int INIT_COUNT = 96;

    public static final int BAR_LINE = 0;

    public static final int TRADE_LINE = 1;

    public static final int SPOT_LINE = 2;

    public enum ChartType {
        FUTURE,
        USDT
    }

    private ChartType chartType;

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
        mChartVolume.setType(chartType == ChartType.FUTURE ? 2 : 3);
        setLegendLoading(false, chartType == ChartType.FUTURE ? 2 : 3);
    }

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

    /**
     * yesterday close price
     */
    private double mLastClose;

    public ImBeddedTimeLineBarChartView(Context context) {
        this(context, null);
    }

    public ImBeddedTimeLineBarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImBeddedTimeLineBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_imbed_kline_time, this);
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

    protected void initBottomChart(AppCombinedChart chart) {
        super.initBottomChart(chart);
        XAxis xAxisVolume = chart.getXAxis();
        xAxisVolume.setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setScaleXEnabled(false);
        chart.setVisibleXRangeMaximum(MAX_COUNT);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(true);
        chart.getAxisRight().setDrawAxisLine(false);
    }

    protected void initChartPrice() {
        mChartPrice.setScaleEnabled(true);
        mChartPrice.setDrawBorders(false);
        mChartPrice.setBorderWidth(1);
        mChartPrice.setDragEnabled(true);
        mChartPrice.setScaleYEnabled(false);
        mChartPrice.setVisibleXRangeMaximum(MAX_COUNT);
        //jiang 0614
        mChartPrice.setAutoScaleMinMaxEnabled(true);
        mChartPrice.setDragDecelerationEnabled(false);
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
                double rate = (value - mLastClose) / mLastClose * 100;
                if (Double.isNaN(rate) || Double.isInfinite(rate)) {
                    return "";
                }
                String s = String.format(Locale.getDefault(), "%.2f%%",
                        rate);
                if(rate >= 0) {
                    s = "+" + s;
                }
                if (TextUtils.equals("-0.00%", s)) {
                    return "+0.00%";
                }
                return s;
            }
        });

        int[] colorArray = {mAxisColor, mAxisColor, mAxisColor, mAxisColor, mAxisColor};
        Transformer leftYTransformer = mChartPrice.getRendererLeftYAxis().getTransformer();
        ColorContentYAxisRenderer leftColorContentYAxisRenderer = new ColorContentYAxisRenderer(mChartPrice.getViewPortHandler(), mChartPrice.getAxisLeft(), leftYTransformer);
        leftColorContentYAxisRenderer.setLabelInContent(true);
        leftColorContentYAxisRenderer.setUseDefaultLabelXOffset(false);
        leftColorContentYAxisRenderer.setLabelColor(colorArray);
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
        mChartPrice.setTouchEnabled(false);
        mChartPrice.setEnabled(false);
    }

    public void initData(List<HisData> hisDatas) {
        //jiang 去除limitline 否则可能会残留
        holding = 0;
        mChartPrice.getAxisLeft().removeAllLimitLines();
        if(hisDatas == null || hisDatas.size() <= 0) {
            mChartPrice.clear();
            mChartVolume.clear();
            return;
        }
        mData.clear();
        mData.addAll(DataUtils.calculateHisData(hisDatas));
        mChartPrice.setRealCount(mData.size());

        ArrayList<Entry> priceEntries = new ArrayList<>(INIT_COUNT);

        for (int i = 0; i < mData.size(); i++) {
            HisData hisData = mData.get(i);
            priceEntries.add(new Entry(i, (float) hisData.getClose()));
        }

//        if (!mData.isEmpty() && mData.size() < MAX_COUNT) {
//            for (int i = mData.size(); i < MAX_COUNT; i++) {
//                paddingEntries.add(new Entry(i, (float) mData.get(mData.size() - 1).getClose()));
//            }
//        }

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setLine(TRADE_LINE, priceEntries));
        LineData lineData = new LineData(sets);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        mChartPrice.setData(combinedData);
        //jiang
        mChartPrice.setDrawLastCircle(true);

        mChartPrice.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartPrice.notifyDataSetChanged();
        moveToLast(mChartPrice);
        initChartVolumeData();

//        mChartPrice.getXAxis().setAxisMaximum(combinedData.getXMax() + 0.5f);
//        mTimeBase.getXAxis().setAxisMaximum(combinedData.getXMax() + 0.5f);
//        mChartVolume.getXAxis().setAxisMaximum(mChartVolume.getData().getXMax() + 0.5f);

        mChartPrice.zoom(MAX_COUNT * 1f / INIT_COUNT, 0, 0, 0);
        mTimeBase.zoom(MAX_COUNT * 1f / INIT_COUNT, 0, 0, 0);
        mChartVolume.zoom(MAX_COUNT * 1f / INIT_COUNT, 0, 0, 0);

        HisData hisData = getLastData();
        setDescription(mChartVolume, "");
        //jiang 20180506
        setDescription(mChartPrice, "");
    }

    public void initData(List<HisData> datas, List<HisData> indexDatas) {
        initData(datas);
        if(indexDatas != null && indexDatas.size() > 0) {
            initSpot(indexDatas);
        } else {

        }
    }

    private void initSpot(List<HisData> datas) {
        //spot 首尾是0 不画
        if(isHeadTailNull(datas)) {
            return;
        }
        ArrayList<Entry> indexEntries = new ArrayList<>(INIT_COUNT);

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

    /**
     * @param chart
     */
    protected void moveToLast(FotaCombinedChart chart) {
        if (mData.size() > INIT_COUNT) {
            chart.moveViewToX(mData.size() - INIT_COUNT);
        }
    }

    private BarDataSet setBar(ArrayList<BarEntry> barEntries, int type) {
        BarDataSet barDataSet = new BarDataSet(barEntries, "vol");
        barDataSet.setHighLightAlpha(120);
        barDataSet.setHighLightColor(CommonPub.getColor(getContext(), R.attr.k_highlight_color));
        barDataSet.setDrawValues(false);
        barDataSet.setVisible(type != INVISIABLE_LINE);
        barDataSet.setHighlightEnabled(true);
        barDataSet.setColors(mIncreasingColor, mDecreasingColor);
        //jiang
        int red = getResources().getColor(R.color.red_color_tran);
        int green = getResources().getColor(R.color.green_color_trans);
        int downColor = AppConfigs.getRedup() ? green : red;
        int upColor = AppConfigs.getRedup() ? red : green;
        barDataSet.setColors(upColor, downColor);
        return barDataSet;
    }


    @NonNull
    private LineDataSet setLine(int type, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSet = new LineDataSet(lineEntries, type + "");
        lineDataSet.setDrawValues(false);
        lineDataSet.setHighlightEnabled(false);
        if (type == TRADE_LINE) {
            lineDataSet.setDrawFilled(true);
            Drawable drawable;
            lineDataSet.setColor(getResources().getColor(R.color.future_marker_color));
            lineDataSet.setCircleColor(ContextCompat.getColor(mContext, R.color.future_marker_color));
            drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_future_dark);
            lineDataSet.setFillDrawable(drawable);
            lineDataSet.setCircleRadius(1.5f);
            lineDataSet.setCircleColorHole(CommonPub.getColor(getContext(), R.attr.color_k_fota_bg));
        } else if (type == SPOT_LINE) {
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

    private void initChartVolumeData() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> paddingEntries = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            HisData t = mData.get(i);
            barEntries.add(new BarEntry(i, (float) t.getVol(), t));
        }
//        int maxCount = MAX_COUNT;
//        if (!mData.isEmpty() && mData.size() < maxCount) {
//            for (int i = mData.size(); i < maxCount; i++) {
//                paddingEntries.add(new BarEntry(i, 0));
//            }
//        }

        BarData barData = new BarData(setBar(barEntries, BAR_LINE), setBar(paddingEntries, INVISIABLE_LINE));
        barData.setBarWidth(0.75f);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        mChartVolume.setData(combinedData);
        mTimeBase.setData(combinedData);

        mChartVolume.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mTimeBase.setVisibleXRange(MAX_COUNT, MIN_COUNT);
//        mChartVolume.setVisibleXRangeMaximum(200);

        mTimeBase.notifyDataSetChanged();
        mChartVolume.notifyDataSetChanged();
//        mChartVolume.moveViewToX(combinedData.getEntryCount());
        moveToLast(mChartVolume);
        moveToLast(mTimeBase);
    }

    /**
     * according to the price to refresh the last data of the chart
     */
    public void refreshData(float price) {
        if (price <= 0 || price == mLastPrice) {
            return;
        }
        mLastPrice = price;
        CombinedData data = mChartPrice.getData();
        if (data == null) return;
        LineData lineData = data.getLineData();
        if (lineData != null) {
            ILineDataSet set = lineData.getDataSetByIndex(0);
            if (set.removeLast()) {
                set.addEntry(new Entry(set.getEntryCount(), price));
            }
        }
        CandleData candleData = data.getCandleData();
        if (candleData != null) {
            ICandleDataSet set = candleData.getDataSetByIndex(0);
            if (set.removeLast()) {
                HisData hisData = mData.get(mData.size() - 1);
                hisData.setClose(price);
                hisData.setHigh(Math.max(hisData.getHigh(), price));
                hisData.setLow(Math.min(hisData.getLow(), price));
                set.addEntry(new CandleEntry(set.getEntryCount(), (float) hisData.getHigh(), (float) hisData.getLow(), (float) hisData.getOpen(), price));
            }
        }
        mChartPrice.notifyDataSetChanged();
        mChartPrice.invalidate();
    }


    public void addData(HisData hisData) {
        hisData = DataUtils.calculateHisData(hisData, mData);
        CombinedData combinedData = mChartPrice.getData();
        LineData priceData = combinedData.getLineData();
        ILineDataSet padding = priceData.getDataSetByIndex(0);
        CandleData kData = combinedData.getCandleData();
        ICandleDataSet klineSet = kData.getDataSetByIndex(0);
        IBarDataSet volSet = mChartVolume.getData().getBarData().getDataSetByIndex(0);

        if (mData.contains(hisData)) {
            int index = mData.indexOf(hisData);
            klineSet.removeEntry(index);
            padding.removeFirst();
            // ma比较特殊，entry数量和k线的不一致，移除最后一个
            volSet.removeEntry(index);
            mData.remove(index);
        }
        mData.add(hisData);
        mChartPrice.setRealCount(mData.size());
        int klineCount = klineSet.getEntryCount();
        klineSet.addEntry(new CandleEntry(klineCount, (float) hisData.getHigh(), (float) hisData.getLow(), (float) hisData.getOpen(), (float) hisData.getClose()));
        volSet.addEntry(new BarEntry(volSet.getEntryCount(), hisData.getVol(), hisData));

        // 因为ma的数量会少，所以这里用kline的set数量作为x

//        mChartPrice.getXAxis().setAxisMaximum(combinedData.getXMax() + 1.5f);
//        mChartVolume.getXAxis().setAxisMaximum(mChartVolume.getData().getXMax() + 1.5f);

        mChartPrice.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartVolume.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartPrice.notifyDataSetChanged();
        mChartPrice.invalidate();
        mChartVolume.notifyDataSetChanged();
        mChartVolume.invalidate();

        //jiang 20180506
        setDescription(mChartPrice, "");
        setDescription(mChartVolume, "");
    }

    public void setLimitLine(double value, String label) {
        setLimitLine(mChartPrice, value, label);
    }

    public void setLastClose(double lastClose) {
        mLastClose = lastClose;
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
}
