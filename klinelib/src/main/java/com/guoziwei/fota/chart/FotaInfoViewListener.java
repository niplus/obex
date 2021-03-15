package com.guoziwei.fota.chart;

import android.content.Context;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.guoziwei.fota.chart.render.AppCombinedChartRenderer;
import com.guoziwei.fota.chart.render.CandleStickChartRenderer;
import com.guoziwei.fota.chart.view.ChartInfoView;
import com.guoziwei.fota.model.HisData;
import com.guoziwei.fota.util.DisplayUtils;

import java.util.List;

/**
 * Created by dell on 2017/9/28.
 */

public class FotaInfoViewListener implements OnChartValueSelectedListener, View.OnTouchListener {

    private List<HisData> mList;
    private double mLastClose;
    private ChartInfoView mInfoView;
    private int mWidth;
    /**
     * if otherChart not empty, highlight will disappear after 3 second
     */
    private Chart[] mOtherChart;

    private CombinedChart mChart;
    private final GestureDetector mDetector;
    private boolean mIsLongPress = false;

    public FotaInfoViewListener(Context context, double lastClose, List<HisData> list, CombinedChart chart, ChartInfoView infoView) {
        mWidth = DisplayUtils.getWidthHeight(context)[0];
        mLastClose = lastClose;
        mList = list;
        mInfoView = infoView;

        mChart = chart;
        mDetector = new GestureDetector(mChart.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                mIsLongPress = true;
                Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
                if (h != null) {
                    mChart.highlightValue(h, true);
                    mChart.disableScroll();
                }
            }

        });
    }

    public FotaInfoViewListener(Context context, double lastClose, List<HisData> list, CombinedChart chart, ChartInfoView infoView, Chart... otherChart) {
        mWidth = DisplayUtils.getWidthHeight(context)[0];
        mLastClose = lastClose;
        mList = list;
        mInfoView = infoView;
        mOtherChart = otherChart;

        mChart = chart;
        mDetector = new GestureDetector(mChart.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                mIsLongPress = true;
                Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
                if (h != null) {
                    mChart.highlightValue(h, true);
                    if (mOtherChart != null) {
                        for (Chart aMOtherChart : mOtherChart) {
                            aMOtherChart.highlightValues(new Highlight[]{new Highlight(h.getX(), Float.NaN, h.getDataSetIndex())});
                        }
                    }
                    mChart.disableScroll();
                }
            }

        });
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int x = (int) e.getX();
        if (x < mList.size() && mIsLongPress) {
            mInfoView.setVisibility(View.VISIBLE);
            double spot = 0;
            LineData lineData = mChart.getLineData();
            if(lineData != null) {
                IDataSet dataSet = lineData.getDataSetByIndex(1);
                if(dataSet != null) {
                    Entry entry = dataSet.getEntryForIndex(x);
                    spot = entry.getY();
                }
            }
            mInfoView.setData(spot, mList.get(x));
        }
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInfoView.getLayoutParams();
        if (h.getXPx() < mWidth / 2) {
            lp.gravity = Gravity.RIGHT;
        } else {
            lp.gravity = Gravity.LEFT;
        }
        mInfoView.setLayoutParams(lp);
        if (mOtherChart != null) {
            for (Chart aMOtherChart : mOtherChart) {
                if(mIsLongPress)
                    aMOtherChart.highlightValues(new Highlight[]{new Highlight(h.getX(), Float.NaN, h.getDataSetIndex())});
                else {
                    mInfoView.setVisibility(View.GONE);
                    mChart.highlightValue(null);
                    aMOtherChart.highlightValue(null);
                }
            }
        }
    }

    @Override
    public void onNothingSelected() {
        mInfoView.setVisibility(View.GONE);
        mChart.highlightValue(null);
        if (mOtherChart != null) {
            for (int i = 0; i < mOtherChart.length; i++) {
                mOtherChart[i].highlightValues(null);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            mIsLongPress = false;
        }
        //记录 手指点击的Y点
        AppCombinedChartRenderer render = (AppCombinedChartRenderer) mChart.getRenderer();
        List<DataRenderer> mRenderers = render.getSubRenderers();
        for (DataRenderer renderer : mRenderers) {
            if (renderer instanceof CandleStickChartRenderer) {
                CandleStickChartRenderer candelChartRenderer = (CandleStickChartRenderer) renderer;
                candelChartRenderer.setTouchY(event.getY());
                break;
            }
        }

        //price in y
        Transformer trans = mChart.getTransformer(YAxis.AxisDependency.LEFT);
        double price = trans.getValuesByTouchPoint(event.getX(), event.getY()).y;
        mInfoView.setTochData(price);

        if (mIsLongPress && event.getAction() == MotionEvent.ACTION_MOVE) {
            Highlight h = mChart.getHighlightByTouchPoint(event.getX(), event.getY());
            if (h != null) {
                mChart.highlightValue(h, true);
                if (mOtherChart != null) {
                    for (Chart aMOtherChart : mOtherChart) {
                        aMOtherChart.highlightValues(new Highlight[]{new Highlight(h.getX(), Float.NaN, h.getDataSetIndex())});
                    }
                }
                mChart.disableScroll();
            }
            return true;
        }
        return false;
    }

    public void setmLastClose(double mLastClose) {
        this.mLastClose = mLastClose;
    }
}
