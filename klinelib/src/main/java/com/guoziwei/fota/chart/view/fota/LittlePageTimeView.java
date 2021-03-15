package com.guoziwei.fota.chart.view.fota;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonPub;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.guoziwei.fota.R;
import com.guoziwei.fota.chart.AppCombinedChart;
import com.guoziwei.fota.chart.FotaTimeBaseRenderChart;
import com.guoziwei.fota.chart.render.FotaLittleTimeBarXAxisRenderer;
import com.guoziwei.fota.model.HisData;
import com.guoziwei.fota.util.DataUtils;
import com.guoziwei.fota.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * kline
 * Created by jiang on 2018/08/04.
 */
public class LittlePageTimeView extends LinearLayout {
    public int MAX_COUNT = 48;
    public int MIN_COUNT = 10;
    public int INIT_COUNT = 48;

    protected List<HisData> mData = new ArrayList<>();
    protected String mDateFormat = "HH:mm";
    private int mAxisColor;

    public static final int NORMAL_LINE = 0;

    /**
     * hide line
     */
    public static final int INVISIABLE_LINE = 6;

//    protected CombinedChart mChartPrice;
    protected AppCombinedChart mChartPrice;
    protected AppCombinedChart mChartVolume;
    protected FotaTimeBaseRenderChart mTimeBase;

    protected Context mContext;
    //是不是指数数据view
    private boolean isIndex;

    public void setIndex(boolean index) {
        isIndex = index;
    }

    /**
     * last price
     */
    private double mLastPrice;

    public LittlePageTimeView(Context context) {
        this(context, null);
    }

    public LittlePageTimeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LittlePageTimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_littlepage_timeline, this);
        mAxisColor = CommonPub.getColor(getContext(), R.attr.axis_color);
        mChartPrice = findViewById(R.id.price_chart);
        mChartVolume = (AppCombinedChart) findViewById(R.id.vol_chart);

        mChartPrice.setNoDataText(context.getString(R.string.loading));
        initChartPrice();
        initBottomChart(mChartVolume);
        mTimeBase = (FotaTimeBaseRenderChart) findViewById(R.id.time_chart);
        initBottomTimeBase();
        setOffset();
        mChartPrice.setTouchEnabled(false);
        mChartVolume.setTouchEnabled(false);
    }

    protected void initBottomChart(AppCombinedChart chart) {
        chart.setScaleEnabled(false);
        chart.setScaleXEnabled(false);
        chart.setDrawBorders(false);
        chart.setDragEnabled(false);
        chart.setScaleYEnabled(false);
        chart.setAutoScaleMinMaxEnabled(false);
        chart.setDragDecelerationEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        XAxis xAxisVolume = chart.getXAxis();
        xAxisVolume.setEnabled(false);
//        xAxisVolume.setDrawLabels(true);
//        xAxisVolume.setDrawAxisLine(false);
//        xAxisVolume.setDrawGridLines(false);
//        xAxisVolume.setTextColor(mAxisColor);
//        xAxisVolume.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxisVolume.setAvoidFirstLastClipping(true);
//        xAxisVolume.setLabelCount(3, true);
//        xAxisVolume.setAxisMinimum(-0.5f);

//        xAxisVolume.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                if (mData.isEmpty()) {
//                    return "";
//                }
//                if (value < 0) {
//                    value = 0;
//                }
//                if (value < mData.size()) {
//                    return DateUtils.formatDate(mData.get((int) value).getDate(), mDateFormat);
//                }
//                return "";
//            }
//        });

        //右边y
        YAxis axisRightVolume = chart.getAxisRight();
        axisRightVolume.setEnabled(false);

        YAxis axisLeftVolume = chart.getAxisLeft();
        axisLeftVolume.setEnabled(false);
    }

    protected void initBottomTimeBase() {
        mTimeBase.setTouchEnabled(false);
        XAxis xAxisTime = mTimeBase.getXAxis();
        xAxisTime.setDrawLabels(true);
        xAxisTime.setDrawAxisLine(false);
        xAxisTime.setDrawGridLines(false);
        xAxisTime.setTextColor(mAxisColor);
        xAxisTime.setPosition(XAxis.XAxisPosition.TOP);
        xAxisTime.setLabelCount(3, true);
        xAxisTime.setAvoidFirstLastClipping(false);
        xAxisTime.setAxisMinimum(-0.5f);
        mTimeBase.setXAisRender(new FotaLittleTimeBarXAxisRenderer(mTimeBase.getViewPortHandler(), mTimeBase.getXAxis(), mTimeBase.getTransformer(YAxis.AxisDependency.LEFT)));

        xAxisTime.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (mData.isEmpty()) {
                    return "";
                }
                if (value < 0) {
                    value = 0;
                }
                //jiang 0908 处理 length.03 or length.9999的情况
                if ((int)value + 1 < mData.size() - 1) {
                    return DateUtils.formatDate(mData.get((int) value).getDate(), mDateFormat);
                } else {
                    int length = mData.size();
                    return DateUtils.formatDate(mData.get(length - 1).getDate(), mDateFormat);
                }
            }
        });
    }

    protected void initChartPrice() {
        mChartPrice.setScaleEnabled(false);
        mChartPrice.setScaleXEnabled(false);
        mChartPrice.setDrawBorders(false);
        mChartPrice.setDragEnabled(false);
        mChartPrice.setScaleYEnabled(false);
        mChartPrice.getDescription().setEnabled(false);
        Legend lineChartLegend = mChartPrice.getLegend();
        lineChartLegend.setEnabled(false);


        XAxis xAxisPrice = mChartPrice.getXAxis();
//        xAxisPrice.setAvoidFirstLastClipping(true);
//        xAxisPrice.setLabelCount(3, true);
        //最右侧总是出现一条空的柱形空间，可能跟这个设置有关，volumebar设置了此属性，但是price没有设置。
//        xAxisPrice.setAxisMinimum(-0.2f);
        xAxisPrice.setEnabled(false);

        YAxis axisLeftPrice = mChartPrice.getAxisLeft();
        axisLeftPrice.setEnabled(false);

        YAxis axisRightPrice = mChartPrice.getAxisRight();
        axisRightPrice.setEnabled(false);
    }

    public void initData(List<HisData> hisDatas) {
        mData.clear();
        mData.addAll(DataUtils.calculateHisData(hisDatas));
        mChartPrice.setRealCount(mData.size());
        int length = mData.size();
        //jiang 0908 取少一条的数，防止右侧的竖空白条
        MAX_COUNT = length - 1;

        ArrayList<Entry> priceEntries = new ArrayList<>(length);
        ArrayList<Entry> paddingEntries = new ArrayList<>(length);

        for (int i = 0; i < mData.size(); i++) {
            priceEntries.add(new Entry(i, (float) mData.get(i).getClose()));
        }
//        if (!mData.isEmpty()) {
//            for (int i = mData.size(); i < MAX_COUNT; i++) {
//                paddingEntries.add(new Entry(i, (float) mData.get(mData.size() - 1).getClose()));
//            }
//        }
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setLine(NORMAL_LINE, priceEntries));
//        sets.add(setLine(INVISIABLE_LINE, paddingEntries));
        LineData lineData = new LineData(sets);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        mChartPrice.setData(combinedData);

        mChartPrice.setVisibleXRange(0, MAX_COUNT);

        //jiang 0920 test bug
//        Log.e("barline init", System.currentTimeMillis() + "" + mChartPrice.toString());
        mChartPrice.notifyDataSetChanged();
        initChartVolumeData();
    }

    private BarDataSet setBar(ArrayList<BarEntry> barEntries, int type) {
        BarDataSet barDataSet = new BarDataSet(barEntries, "vol");
        barDataSet.setHighLightAlpha(120);
        barDataSet.setHighLightColor(CommonPub.getColor(getContext(), R.attr.k_highlight_color));
        barDataSet.setDrawValues(false);
        barDataSet.setVisible(type != INVISIABLE_LINE);
        barDataSet.setHighlightEnabled(type != INVISIABLE_LINE);
        int red = getResources().getColor(R.color.red_color_tran);
        int green = getResources().getColor(R.color.green_color_trans);
        int downColor = AppConfigs.getRedup() ? green : red;
        int upColor = AppConfigs.getRedup() ? red : green;
        barDataSet.setColors(upColor, downColor);
        return barDataSet;
    }

    @NonNull
    private LineDataSet setLine(int type, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "" + type);
        lineDataSetMa.setDrawValues(false);
        if (type == NORMAL_LINE) {
            lineDataSetMa.setDrawFilled(true);
            if(isIndex) {
                lineDataSetMa.setColor(getResources().getColor(R.color.color_little_page_index));
                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_card_dark);
                lineDataSetMa.setFillDrawable(drawable);
            } else {
                lineDataSetMa.setColor(getResources().getColor(R.color.color_little_page));
                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_future_dark);
                lineDataSetMa.setFillDrawable(drawable);
            }
        } else {
            lineDataSetMa.setVisible(false);
            lineDataSetMa.setHighlightEnabled(false);
        }

        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setDrawCircleHole(false);

        return lineDataSetMa;
    }

    private void initChartVolumeData() {
        int length = mData.size();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> paddingEntries = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            HisData t = mData.get(i);
            barEntries.add(new BarEntry(i, (float) t.getVol(), t));
        }
//        int maxCount = length;
//        if (!mData.isEmpty() && mData.size() < maxCount) {
//            for (int i = mData.size(); i < maxCount; i++) {
//                paddingEntries.add(new BarEntry(i, 0));
//            }
//        }

        BarData barData = new BarData(setBar(barEntries, NORMAL_LINE));
        barData.setBarWidth(0.75f);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        mChartVolume.setRealCount(length);
        mChartVolume.setData(combinedData);
        mChartVolume.setVisibleXRange(0, MAX_COUNT);

        mTimeBase.setRealCount(length);
        mTimeBase.setData(combinedData);
        mTimeBase.setVisibleXRange(0, MAX_COUNT);

        mTimeBase.notifyDataSetChanged();
        mChartVolume.notifyDataSetChanged();
        mChartVolume.invalidate();
        mTimeBase.invalidate();
        //jiang 0920 test bug
//        Log.e("barline init", System.currentTimeMillis() + "" + mChartVolume.toString());
    }

    /**
     * align two chart
     */
    private void setOffset() {
        mChartVolume.setViewPortOffsets(0, 0, 0, 0);
        mChartPrice.setViewPortOffsets(0, 0, 0, 0);
        mChartPrice.invalidate();
        mChartVolume.invalidate();
    }

    public void setDateFormat(String mDateFormat) {
        this.mDateFormat = mDateFormat;
    }
}
