package com.guoziwei.fota.chart.view.fota;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.fota.android.commonlib.utils.Pub;
import com.guoziwei.fota.R;
import com.guoziwei.fota.chart.view.ChartInfoView;
import com.guoziwei.fota.model.HisData;
import com.guoziwei.fota.util.DateUtils;

import java.math.RoundingMode;
import java.util.Locale;

/**
 * Created by jiang on 2018/08/05.
 */

public class ComplexChartInfoView extends ChartInfoView {
    private int type;//2合约 3现货
    private boolean isFromTrade;//false 行情 比较高，true 合约和兑换，比较低
    private int digits;//小数位

    public void setType(int type) {
        this.type = type;
    }

    public void setFromTrade(boolean fromTrade) {
        isFromTrade = fromTrade;
    }

    private TextView mTvPrice;
    private TextView mTvTime;
    private TextView mTvOpenPrice;
    private TextView mTvClosePrice;
    private TextView mTvHighPrice;
    private TextView mTvLowPrice;
    private TextView mTvVol;
    private TextView mTvVolKey;
    private TextView mTvHold;
    private TextView mTvChangeRate;
    private TextView mTVSpot;
    private TextView mTvOpenTitle;
    private TextView mTvCloseTitle;
    private TextView mTvHighTitle;
    private TextView mTvLowTitle;
    private LinearLayout mLlOpen;
    private LinearLayout mLlClose;
    private LinearLayout mLlHigh;
    private LinearLayout mLlLow;
    private LinearLayout mLlIndex;

    public ComplexChartInfoView(Context context) {
        this(context, null);
    }

    public ComplexChartInfoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComplexChartInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_complex_chart_info, this);
        mTvPrice = (TextView) findViewById(R.id.tv_price);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvOpenPrice = (TextView) findViewById(R.id.tv_open_price);
        mTvClosePrice = (TextView) findViewById(R.id.tv_close_price);
        mTvHighPrice = (TextView) findViewById(R.id.tv_high_price);
        mTvLowPrice = (TextView) findViewById(R.id.tv_low_price);
        mTvChangeRate = (TextView) findViewById(R.id.tv_change_rate);
        mTvVol = (TextView) findViewById(R.id.tv_vol);
        mTvVolKey = (TextView) findViewById(R.id.tv_volume_key);
        mTvHold = (TextView) findViewById(R.id.tv_hold);
        mTVSpot = (TextView) findViewById(R.id.tv_spot);
        mTvOpenTitle = (TextView) findViewById(R.id.tv_open_title);
        mTvCloseTitle = (TextView) findViewById(R.id.tv_close_title);
        mTvHighTitle = (TextView) findViewById(R.id.tv_high_title);
        mTvLowTitle = (TextView) findViewById(R.id.tv_low_title);
        mLlOpen = findViewById(R.id.ll_open);
        mLlHigh = findViewById(R.id.ll_high);
        mLlLow = findViewById(R.id.ll_low);
        mLlClose = findViewById(R.id.ll_close);
        mLlIndex = findViewById(R.id.ll_index);
    }

    @Override
    public void setData(double spotPrice, HisData data) {
        if(isFromTrade) {
            mTvOpenTitle.setText(getContext().getString(R.string.open_price) + ": ");
            mTvCloseTitle.setText(getContext().getString(R.string.close_price) + ": ");
            mTvHighTitle.setText(getContext().getString(R.string.high_price) + ": ");
            mTvLowTitle.setText(getContext().getString(R.string.low_price) + ": ");
            mLlOpen.setOrientation(HORIZONTAL);
            mLlOpen.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mLlHigh.setOrientation(HORIZONTAL);
            mLlHigh.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mLlLow.setOrientation(HORIZONTAL);
            mLlLow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mLlClose.setOrientation(HORIZONTAL);
            mLlClose.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        if(type == 2) {
            mLlIndex.setVisibility(VISIBLE);
        } else if(type == 3) {
            mLlIndex.setVisibility(GONE);
        }
        mTvTime.setText(DateUtils.formatDate(data.getDate(), "MM/dd HH:mm"));
//        mTvClosePrice.setText(DoubleUtil.formatDecimalFotDigits(data.getClose(), digits));
//        mTvOpenPrice.setText(DoubleUtil.formatDecimalFotDigits(data.getOpen(), digits));
//        mTvHighPrice.setText(DoubleUtil.formatDecimalFotDigits(data.getHigh(), digits));
//        mTvLowPrice.setText(DoubleUtil.formatDecimalFotDigits(data.getLow(), digits));
        mTvClosePrice.setText(String.format(Locale.getDefault(), "%."+digits+"f", data.getClose()));
        mTvOpenPrice.setText(String.format(Locale.getDefault(), "%."+digits+"f", data.getOpen()));
        mTvHighPrice.setText(String.format(Locale.getDefault(), "%."+digits+"f", data.getHigh()));
        mTvLowPrice.setText(String.format(Locale.getDefault(), "%."+digits+"f", data.getLow()));
//        mTvChangeRate.setText(String.format(Locale.getDefault(), "%.2f%%", (data.getClose()- data.getOpen()) / data.getOpen() * 100));
        String trend = "";
        double rate = (data.getClose() - data.getOpen()) / data.getOpen() * 100;
        if (Double.isNaN(rate) || Double.isInfinite(rate)) {
            trend = "";
        } else {
            trend = String.format(Locale.getDefault(), "%.2f%%", rate);
            if(rate >= 0) {
                trend = "+" + trend;
            }
        }
        mTvChangeRate.setText(trend);
//        mTvHold.setText(data.getHolding() + "");
        mTvVol.setText(Pub.zoomZero(Pub.getPriceFormat(data.getVol(), digits, RoundingMode.UP)));
        mTVSpot.setText(String.format(Locale.getDefault(), "%."+digits+"f", spotPrice));
        removeCallbacks(mRunnable);
        postDelayed(mRunnable, 1000);
    }

    @Override
    public void setTochData(double price) {
        mTvPrice.setText(String.format(Locale.getDefault(), "%."+ digits + "f", price));
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }
}
