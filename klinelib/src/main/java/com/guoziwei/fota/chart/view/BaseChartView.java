package com.guoziwei.fota.chart.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonPub;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.Transformer;
import com.guoziwei.fota.R;
import com.guoziwei.fota.chart.FotaTimeBaseRenderChart;
import com.guoziwei.fota.chart.render.ColorContentYAxisRenderer;
import com.guoziwei.fota.chart.view.fota.FotaChartYFutureMarkerView;
import com.guoziwei.fota.chart.view.fota.FotaChartYSpotMarkerView;
import com.guoziwei.fota.model.HisData;
import com.guoziwei.fota.util.DateUtils;
import com.guoziwei.fota.util.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

public class BaseChartView extends LinearLayout {
    private String biaodi;

    public void setBiaodi(String biaodi) {
        this.biaodi = biaodi;
        if(TextUtils.isEmpty(biaodi)) {
            return;
        }
        TextView txtTradePriceName = layoutLegend.findViewById(R.id.txt_trade_price_name);
        if(AppConfigs.isChinaLanguage() && !TextUtils.isEmpty(biaodi)) {
            txtTradePriceName.setText(biaodi.toUpperCase() + getContext().getString(R.string.usdt_price));
        } else {
            txtTradePriceName.setText(getContext().getString(R.string.usdt_price));
        }
    }

    protected String mDateFormat = "HH:mm";

    protected int mDecreasingColor;
    protected int mIncreasingColor;
    protected int mAxisColor;
    protected int mGridColor;
    protected int mTransparentColor;
    protected double holding;

    /**
     * the digits of the symbol
     */
    protected int mDigits = 2;

    protected FotaTimeBaseRenderChart mTimeBase;
    //K线图例
    protected LinearLayout layoutLegend;
    private KlinePeriodInterface periodInterface;
    private KlineScreenInterface screenInterface;

    //
    protected LinearLayout loadingLayout;

    public int MAX_COUNT = 50;
    public int MIN_COUNT = 10;
    public int INIT_COUNT = 50;

    protected List<HisData> mData = new ArrayList<>(300);

    //jiang loading runnable 用于长时间未返回的时候关闭dialog
    protected Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //jiang
            if (this != null && loadingLayout != null) {
                if(loadingLayout.getVisibility() != GONE) {
                    loadingLayout.setVisibility(View.GONE);
                }
            }
        }
    };

    protected LineChartXMarkerView mvx;
    protected FotaChartYSpotMarkerView mLastYSpotMarker;
    protected FotaChartYFutureMarkerView mLastYFutureMarker;

    protected boolean isFromTrade;

    public BaseChartView(Context context) {
        this(context, null);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //jiang
//        SharedPreferencesUtil.init(context);
        mAxisColor = CommonPub.getColor(getContext(), R.attr.axis_color);
//        mGridColor = ContextCompat.getColor(getContext(), R.color.grid_line_color);
        mGridColor = CommonPub.getColor(getContext(), R.attr.grid_line_color);
        mTransparentColor = ContextCompat.getColor(getContext(), android.R.color.transparent);

        int red = ContextCompat.getColor(getContext(), R.color.red_color);
        int green = ContextCompat.getColor(getContext(), R.color.green_color);
        mDecreasingColor = AppConfigs.getRedup() ? green : red;
        mIncreasingColor = AppConfigs.getRedup() ? red : green;

        mvx = new LineChartXMarkerView(context);
    }

    public void setLoading(boolean start) {
        removeCallbacks(mRunnable);
        if(start) {
            if(loadingLayout != null) {
                loadingLayout.setVisibility(VISIBLE);
            }
            postDelayed(mRunnable, 5000);
        } else {
            removeCallbacks(mRunnable);
            post(mRunnable);
        }
    }

    public void setPeriodInterface(KlinePeriodInterface periodInterface) {
        this.periodInterface = periodInterface;
    }

    public void setScreenInterface(KlineScreenInterface screenInterface) {
        this.screenInterface = screenInterface;
    }

    protected void setLegendLoading(boolean isKline, int entityType) {
        loadingLayout = findViewById(R.id.ll_progress);
        layoutLegend = findViewById(R.id.legend);
        if(layoutLegend == null) {
            return;
        }
        layoutLegend.setVisibility(VISIBLE);
        LinearLayout llLengendFutre = layoutLegend.findViewById(R.id.ll_lengend_future);
        LinearLayout llLengendSpot = layoutLegend.findViewById(R.id.ll_lengend_spot);
        if (isKline) {
            LinearLayout llPeriod = layoutLegend.findViewById(R.id.ll_peroid_change);
            //20181123 全屏放到charView中，如果是交易界面，没有时间刻度
            if(!isFromTrade) {
                initPeriod(llPeriod);
            } else {
                llPeriod.setVisibility(GONE);
                LinearLayout llScreen = layoutLegend.findViewById(R.id.ll_full_screen);
                llScreen.setVisibility(VISIBLE);
                llScreen.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(screenInterface != null) {
                            screenInterface.fullScreen();
                        }
                    }
                });
            }
            if (entityType == 1) {

            } else if (entityType == 2) {
                llLengendFutre.setVisibility(View.VISIBLE);
                llLengendFutre.setVisibility(View.GONE);
                llLengendSpot.setVisibility(View.VISIBLE);
            } else if (entityType == 3) {
                llLengendFutre.setVisibility(View.GONE);
                llLengendSpot.setVisibility(View.GONE);
            }
        } else {
            llLengendFutre.setVisibility(View.VISIBLE);
            if (entityType == 1) {
                llLengendFutre.setVisibility(View.GONE);
                llLengendSpot.setVisibility(View.VISIBLE);
            } else if (entityType == 2) {
                TextView txtTradePriceName = layoutLegend.findViewById(R.id.txt_trade_price_name);
                txtTradePriceName.setText(getContext().getString(R.string.legend_future_price));
                llLengendFutre.setVisibility(View.VISIBLE);
                llLengendSpot.setVisibility(View.VISIBLE);
            } else if (entityType == 3) {
                llLengendFutre.setVisibility(View.VISIBLE);
                llLengendSpot.setVisibility(View.GONE);
            }
        }
    }

    private void initPeriod(final LinearLayout llPeriod) {
        llPeriod.setVisibility(VISIBLE);
        int length = llPeriod.getChildCount();
        for(int i=0;i<length;i++) {
            final int index = i;
            final TextView each = (TextView) llPeriod.getChildAt(i);
            each.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int childsLength = llPeriod.getChildCount();
                    for(int j=0;j<childsLength;j++) {
                        each.setTextColor(CommonPub.getColor(getContext(), R.attr.font_color));
                        if(j != index) {
                            TextView child = (TextView) llPeriod.getChildAt(j);
                            child.setTextColor(CommonPub.getColor(getContext(), R.attr.font_color3));
                        }
                    }
                    periodInterface.periodClick(index);
                }
            });
        }
    }

    protected boolean isHeadTailNull(List<HisData> datas) {
        if(datas != null && datas.size() >= 2) {
            int length = datas.size();
            HisData first = datas.get(0);
            HisData last = datas.get(length - 1);
            if(first.isZero() && last.isZero()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    protected void initBottomChart(CombinedChart chart) {
        chart.setScaleEnabled(true);
        chart.setDrawBorders(false);
        chart.setBorderWidth(1);
        chart.setDragEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.setDragDecelerationEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        Legend lineChartLegend = chart.getLegend();
        lineChartLegend.setEnabled(false);


        XAxis xAxisVolume = chart.getXAxis();
        xAxisVolume.setEnabled(false);
//        xAxisVolume.setDrawLabels(true);
//        xAxisVolume.setDrawAxisLine(false);
//        xAxisVolume.setDrawGridLines(false);
//        xAxisVolume.setTextColor(mAxisColor);
//        xAxisVolume.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxisVolume.setLabelCount(3, true);
//        xAxisVolume.setAvoidFirstLastClipping(true);
//        xAxisVolume.setAxisMinimum(-0.5f);
//
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

        YAxis axisLeftVolume = chart.getAxisLeft();
        axisLeftVolume.setDrawLabels(false);
        axisLeftVolume.setDrawGridLines(false);
//        axisLeftVolume.setLabelCount(3, true);
        axisLeftVolume.setDrawAxisLine(false);
        axisLeftVolume.setTextColor(mAxisColor);
//        axisLeftVolume.setSpaceTop(10);
//        axisLeftVolume.setSpaceBottom(0);
        axisLeftVolume.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        /*axisLeftVolume.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String s;
                if (value > 10000) {
                    s = (int) (value / 10000) + "w";
                } else if (value > 1000) {
                    s = (int) (value / 1000) + "k";
                } else {
                    s = (int) value + "";
                }
                return String.format(Locale.getDefault(), "%1$5s", s);
            }
        });
        */
        Transformer leftYTransformer = chart.getRendererLeftYAxis().getTransformer();
        ColorContentYAxisRenderer leftColorContentYAxisRenderer = new ColorContentYAxisRenderer(chart.getViewPortHandler(), chart.getAxisLeft(), leftYTransformer);
        leftColorContentYAxisRenderer.setLabelInContent(true);
        leftColorContentYAxisRenderer.setUseDefaultLabelXOffset(false);
        chart.setRendererLeftYAxis(leftColorContentYAxisRenderer);

        //右边y
        YAxis axisRightVolume = chart.getAxisRight();
        axisRightVolume.setDrawLabels(false);
        axisRightVolume.setDrawGridLines(false);
        axisRightVolume.setDrawAxisLine(false);

    }

    protected void initBottomTimeBase(CombinedChart chart) {
        chart.setTouchEnabled(false);
        XAxis xAxisTime = chart.getXAxis();
        xAxisTime.setDrawLabels(true);
        xAxisTime.setDrawAxisLine(false);
        xAxisTime.setDrawGridLines(false);
        xAxisTime.setTextColor(mAxisColor);
        xAxisTime.setPosition(XAxis.XAxisPosition.TOP);
        xAxisTime.setLabelCount(3, true);
        xAxisTime.setAvoidFirstLastClipping(false);
        xAxisTime.setAxisMinimum(-0.5f);

        xAxisTime.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (mData.isEmpty()) {
                    return "";
                }
                if (value < 0) {
                    value = 0;
                }
                if ((int)value < mData.size()) {
                    return DateUtils.formatDate(mData.get((int) value).getDate(), mDateFormat);
                }  else {
                    int length = mData.size();
                    return DateUtils.formatDate(mData.get(length - 1).getDate(), mDateFormat);
                }
            }
        });
    }

    /**
     * @param chart
     */
    protected void moveToLast(CombinedChart chart) {
        if (mData.size() > INIT_COUNT) {
            chart.moveViewToX(mData.size() - INIT_COUNT);
        }
    }


    /**
     * add limit line to chart
     */
    public void setLimitLine(CombinedChart mChartPrice, double value, String label) {
        if(holding == value) {
            return;
        }

        holding = value;
        mvx.setHoldingText(label);
        mvx.setChartView(mChartPrice);
        LimitLine limitLine = new LimitLine((float) value);
        limitLine.enableDashedLine(3, 5, 0);
        limitLine.setLineColor(CommonPub.getColor(getContext(), R.attr.ave_color));
        mChartPrice.getAxisLeft().removeAllLimitLines();
        mChartPrice.getAxisLeft().addLimitLine(limitLine);
    }

    /**
     * set the count of k chart
     */
    public void setCount(int init, int max, int min) {
        INIT_COUNT = init;
        MAX_COUNT = max;
        MIN_COUNT = min;
    }

    protected void setDescription(Chart chart, String text) {
        Description description = chart.getDescription();
//        float dx = chart.getWidth() - chart.getViewPortHandler().offsetRight() - description.getXOffset();
//        description.setPosition(dx, description.getTextSize());
        description.setText(text);
    }

    public HisData getLastData() {
        if (mData != null && !mData.isEmpty()) {
            return mData.get(mData.size() - 1);
        }
        return null;
    }


    /**
     * align two chart
     */
    protected void setOffset(CombinedChart mChartPrice, CombinedChart mChartVolume) {
//        int chartHeight = getResources().getDimensionPixelSize(R.dimen.bottom_chart_height_littlepage);
        int right = DisplayUtils.dip2px(getContext(), 55);
//        int chartHeight = DisplayUtils.dip2px(mContext, 60);
        mChartPrice.setViewPortOffsets(0, 0, right, 0);
        mChartVolume.setViewPortOffsets(0, 0, right, 0);
        mTimeBase.setViewPortOffsets(0, 0, right, 0);
    }

    public void setDateFormat(String mDateFormat) {
        this.mDateFormat = mDateFormat;
    }

    public void setmDigits(int mDigits) {
        this.mDigits = mDigits;
        if(mLastYFutureMarker != null) {
            mLastYFutureMarker.setDigits(mDigits);
        }
        if(mLastYSpotMarker != null) {
            mLastYSpotMarker.setDigits(mDigits);
        }
    }

    public interface KlinePeriodInterface {
        void periodClick(int index);
    }

    public interface KlineScreenInterface {
        void fullScreen();
    }
}
