package com.guoziwei.fota.chart.view.fota;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fota.android.commonlib.utils.CommonPub;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.guoziwei.fota.R;
import com.guoziwei.fota.model.HisData;
import com.guoziwei.fota.util.DataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * kline
 * Created by jiang on 2018/08/18.
 */
public class ImBeddedNarrowTimeView extends LinearLayout {
    protected List<HisData> mData = new ArrayList<>();
    protected String mDateFormat = "HH:mm";
    private int mAxisColor;

    public static final int NORMAL_LINE = 0;
    public static final int SPOT_LINE = 1;

    /**
     * hide line
     */
    public static final int INVISIABLE_LINE = 6;

    protected CombinedChart mChartPrice;

    protected Context mContext;

    /**
     * last price
     */
    private double mLastPrice;

    public ImBeddedNarrowTimeView(Context context) {
        this(context, null);
    }

    public ImBeddedNarrowTimeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImBeddedNarrowTimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_imbed_narrow_timeline, this);
        mAxisColor = CommonPub.getColor(getContext(), R.attr.axis_color);
        mChartPrice = findViewById(R.id.price_chart);

        mChartPrice.setNoDataText(context.getString(R.string.loading));
        initChartPrice();
        setOffset();
        mChartPrice.setTouchEnabled(false);
    }

    protected void initChartPrice() {
        mChartPrice.setScaleEnabled(false);
        mChartPrice.setScaleXEnabled(false);
        mChartPrice.setDrawBorders(false);
        mChartPrice.setDragEnabled(false);
        mChartPrice.setScaleYEnabled(false);
        mChartPrice.getDescription().setEnabled(false);
        mChartPrice.setAutoScaleMinMaxEnabled(true);
        mChartPrice.setDragDecelerationEnabled(false);
        Legend lineChartLegend = mChartPrice.getLegend();
        lineChartLegend.setEnabled(false);

        XAxis xAxisPrice = mChartPrice.getXAxis();
        xAxisPrice.setEnabled(false);
        YAxis axisLeftPrice = mChartPrice.getAxisLeft();
        axisLeftPrice.setEnabled(true);
        axisLeftPrice.setDrawGridLines(false);

        YAxis axisRightPrice = mChartPrice.getAxisRight();
        axisRightPrice.setEnabled(false);
        axisRightPrice.setDrawGridLines(false);
    }

    public void initData(List<HisData> hisDatas) {
        mData.clear();
        mData.addAll(DataUtils.calculateHisData(hisDatas));
        mChartPrice.setRealCount(mData.size());
        int length = mData.size();

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

        mChartPrice.setVisibleXRange(0, length);

        mChartPrice.notifyDataSetChanged();
        mChartPrice.invalidate();
//        mChartPrice.moveViewToX(combinedData.getEntryCount());
//        moveToLast(mChartPrice);

//        mChartPrice.getXAxis().setAxisMaximum(combinedData.getXMax() + 0.5f);
//        mChartVolume.getXAxis().setAxisMaximum(mChartVolume.getData().getXMax() + 0.5f);

//        mChartPrice.zoom(MAX_COUNT * 1f / INIT_COUNT, 0, 0, 0);
//        mChartVolume.zoom(MAX_COUNT * 1f / INIT_COUNT, 0, 0, 0);
    }

    public void initData(List<HisData> datas, List<HisData> indexDatas) {
        if(datas != null && datas.size() > 0) {
            initData(datas);
        }
        if(indexDatas != null && indexDatas.size() > 0) {
            initSpot(indexDatas);
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

    private void initSpot(List<HisData> datas) {
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

        mChartPrice.notifyDataSetChanged();
        mChartPrice.invalidate();
    }

    @NonNull
    private LineDataSet setLine(int type, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "" + type);
        lineDataSetMa.setDrawValues(false);
        if (type == NORMAL_LINE) {
            lineDataSetMa.setDrawFilled(true);
            lineDataSetMa.setColor(getResources().getColor(R.color.color_imbed_usdt));
            lineDataSetMa.setCircleColor(ContextCompat.getColor(mContext, R.color.color_imbed_usdt));
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_future_dark);
            lineDataSetMa.setFillDrawable(drawable);
        } else if(type == SPOT_LINE) {
            lineDataSetMa.setDrawFilled(true);
            lineDataSetMa.setColor(getResources().getColor(R.color.color_imbed_spot));
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_spot_dark);
            lineDataSetMa.setFillDrawable(drawable);
        } else {
            lineDataSetMa.setVisible(false);
            lineDataSetMa.setHighlightEnabled(false);
        }
        lineDataSetMa.setCircleRadius(1f);

        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setDrawCircleHole(false);

        return lineDataSetMa;
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

        mChartPrice.notifyDataSetChanged();
        mChartPrice.invalidate();
    }

    /**
     *
     */
    private void setOffset() {
//        int chartHeight = getResources().getDimensionPixelSize(R.dimen.bottom_chart_height_littlepage);
//        mChartVolume.setViewPortOffsets(0, 0, 0, DisplayUtils.dip2px(mContext, 20));
        mChartPrice.setViewPortOffsets(0, 0, 0, 0);
        mChartPrice.invalidate();
    }

    public void setDateFormat(String mDateFormat) {
        this.mDateFormat = mDateFormat;
    }
}
