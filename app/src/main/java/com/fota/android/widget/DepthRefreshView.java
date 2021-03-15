package com.fota.android.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.fota.android.R;
import com.fota.android.app.Constants;
import com.fota.android.common.bean.exchange.CurrentPriceBean;
import com.fota.android.common.bean.home.EntrustBean;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.GradientDrawableUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.commonlib.view.DownTrigonView;
import com.fota.android.core.anim.ViewWrapper;
import com.fota.android.moudles.common.DepthFixNumberAdapter;
import com.fota.android.widget.popwin.DigitalPopupWindow;
import com.fota.android.widget.popwin.SelectItemDialog;
import com.fota.android.widget.popwin.WindowCloseListener;
import com.fota.android.widget.recyclerview.ViewHolder;

import java.util.List;


/**
 * Created by jiang on 2018/08/14.
 */

public class DepthRefreshView extends LinearLayout implements View.OnClickListener, DigitalPopupWindow.DigitalInterface {
    //Depth挂单埠的 宽度
    private int totalWith;

    private DepthFixNumberLinearView topFive;
    private DepthFixNumberLinearView bottomFive;

    private DepthFixNumberAdapter<EntrustBean, ViewHolder> topFiveAdapter;
    private DepthFixNumberAdapter<EntrustBean, ViewHolder> bottomFiveAdapter;
    private TextView digitTextView;

    //private DigitalPopupWindow popupWindow;

    //
    private String currentDigital = "";
    private String lastDigital = "";

    //盘口区的深度 最大的买入卖出量单
    private double maxSellVol = 0.0;
    private double maxBuyVol = 0.0;

    private TextView txtTickerPrice;
    private DownTrigonView imgUpDown;
    private TextView txtTickerTrend;
    private boolean depthAllContentIsup = true;
    private View tickerContainer;
    private CurrentPriceBean currentPrice;
    //
    //private String compositeKey;//1-1001 entityType + entityId
    private int type;////2-行情合约 3-USDT
    private String coniPair = "";
    private List<String> precisions;

    public DepthRefreshView(Context context) {
        super(context);
        init(context);
    }

    public DepthRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.depth);
//            coloums = array.getInt(R.styleable.depth_numbers, 5);
            array.recycle();
        }
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_depth_refresh, this);

        txtTickerPrice = findViewById(R.id.txt_ticker_price);
        imgUpDown = findViewById(R.id.img_up_down);
        imgUpDown.setUp(true);
        txtTickerTrend = findViewById(R.id.txt_ticker_trend);
        tickerContainer = findViewById(R.id.ll_ticker);
        tickerContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && currentPrice != null) {
                    listener.onTickClick(currentPrice);
                }
            }
        });

        topFive = DepthRefreshView.this.findViewById(R.id.depth_top_five);
        bottomFive = DepthRefreshView.this.findViewById(R.id.depth_bottom_five);
        digitTextView = findViewById(R.id.tv_digital);
        UIUtil.setRoundCornerBorderBg(digitTextView, Pub.getColor(getContext(), R.attr.font_color4), 2);
        digitTextView.setOnClickListener(this);
        arrowUp(false);
        initTopDepth();
        initBottomDepth();
    }

    public void setUnit(int type) {
        if (type == 2) {
            TextView unitText = findViewById(R.id.txt_unit);
            if (unitText != null) {
                unitText.setText(getContext().getString(R.string.market_amount_unit1));
            }
        }
    }

    public void setType(int type, String coinPair) {
        this.type = type;
        //更改右侧显示文体
        //币对
        this.coniPair = coinPair;
    }

    public void setSmallLargePrecision(List<String> precisions) {
        this.precisions = precisions;
        String unit = getContext().getString(R.string.common_precesion_unit) + getContext().getString(R.string.common_precesion);
        currentDigital = precisions.get(0);
        if (digitTextView != null) {
            String text = type == 3 ? currentDigital + unit : currentDigital;
            digitTextView.setText(text);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        totalWith = getWidth();
    }

    /**
     * 初始化上面五档控件
     * 卖盘 跌色
     */
    private void initTopDepth() {
        topFiveAdapter = new DepthFixNumberAdapter<EntrustBean, ViewHolder>(getContext(), R.layout.item_five, 5) {
            @Override
            public void convert(ViewHolder holder, final EntrustBean model, int position) {
                holder.setTextColor(R.id.txt_depth_price, AppConfigs.getDownColor());
                if (model == null || model.getDoulePrice() == 0.0) {
                    holder.setText(R.id.txt_depth_price, Constants.NONE);
                    holder.setText(R.id.txt_depth_volume, Constants.NONE);
                    View item = holder.getView(R.id.five_item_view);
                    ObjectAnimator.ofInt(new ViewWrapper(item), "width", 0).setDuration(100).start();
                    holder.getConvertView().setOnClickListener(null);
                    return;
                }

                double indexAmout = model.getDoubleAmount();
                double rate = maxSellVol == 0.0 ? 0 : indexAmout / maxSellVol;
                int width = (int) (totalWith * rate);

                View item = holder.getView(R.id.five_item_view);
                item.setBackground(getResources().getDrawable(R.drawable.ft_left_corner_2));
                GradientDrawableUtils.setBgColor(item, AppConfigs.getDownColor());
                GradientDrawableUtils.setBgAlpha(item, 30);

                ObjectAnimator.ofInt(new ViewWrapper(item), "width", width).setDuration(100).start();
//                holder.setText(R.id.txt_depth_price, model.getPriceCarry(currentDigital));
                holder.setText(R.id.txt_depth_price, model.getPrice());
                if (model.getDoubleAmount() > 1000.0) {
                    holder.setText(R.id.txt_depth_volume, Pub.getTotalStringForLength(model.getDoubleAmount(), 1));
                } else {
                    holder.setText(R.id.txt_depth_volume, model.getAmount());
                }
                holder.getConvertView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.8f,
                                1, 0.8f, 0.5f, 0.5f);
                        scaleAnimation.setDuration(150);
                        v.startAnimation(scaleAnimation);
                        scaleAnimation.start();
                        if (listener != null) {
                            listener.onClickItem(model);
                        }
                    }
                });
            }
        };
        topFive.setDepthAdapter(topFiveAdapter);
        topFiveAdapter.putList(null);
        topFive.onRefreshView(true);
    }

    /**
     * 初始化下面五档控件
     * 买盘 涨色
     */
    private void initBottomDepth() {
        bottomFiveAdapter = new DepthFixNumberAdapter<EntrustBean, ViewHolder>(getContext(), R.layout.item_five, 5) {
            @Override
            public void convert(ViewHolder holder, final EntrustBean model, int position) {
                holder.setTextColor(R.id.txt_depth_price, AppConfigs.getUpColor());
                if (model == null || model.getDoulePrice() == 0.0) {
                    holder.setText(R.id.txt_depth_price, Constants.NONE);
                    holder.setText(R.id.txt_depth_volume, Constants.NONE);
                    View item = holder.getView(R.id.five_item_view);
                    ObjectAnimator.ofInt(new ViewWrapper(item), "width", 0).setDuration(100).start();
                    holder.getConvertView().setOnClickListener(null);
                    return;
                }

                double indexAmout = model.getDoubleAmount();
                double rate = maxBuyVol == 0.0 ? 0 : indexAmout / maxBuyVol;
                int width = (int) (totalWith * rate);

                View item = holder.getView(R.id.five_item_view);
                item.setBackground(getResources().getDrawable(R.drawable.ft_left_corner_2));
                GradientDrawableUtils.setBgColor(item, AppConfigs.getUpColor());
                GradientDrawableUtils.setBgAlpha(item, 30);

                ObjectAnimator.ofInt(new ViewWrapper(item), "width", width).setDuration(100).start();
//                holder.setText(R.id.txt_depth_price, model.getPriceCut(currentDigital));
                holder.setText(R.id.txt_depth_price, model.getPrice());
                if (model.getDoubleAmount() > 1000.0) {
                    holder.setText(R.id.txt_depth_volume, Pub.getTotalStringForLength(model.getDoubleAmount(), 1));
                } else {
                    holder.setText(R.id.txt_depth_volume, model.getAmount());
                }
                holder.getConvertView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.8f,
                                1, 0.8f, 0.5f, 0.5f);
                        scaleAnimation.setDuration(150);
                        v.startAnimation(scaleAnimation);
                        scaleAnimation.start();
                        if (listener != null) {
                            listener.onClickItem(model);
                        }
                    }
                });
            }
        };
        bottomFive.setDepthAdapter(bottomFiveAdapter);
        bottomFiveAdapter.putList(null);
        bottomFive.onRefreshView(false);
    }

    /**
     * 刷新与小数位有关状态
     */
    private void refreshDigit(boolean needMix) {
        arrowUp(false);
        if (listener != null) {
            listener.onRefreshDigital(lastDigital, currentDigital);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_digital:
                if (!Pub.isListExists(precisions)) {
                    return;
                }
                arrowUp(true);
                SelectItemDialog dialog = new SelectItemDialog(getContext(), null, precisions, currentDigital);
                dialog.setCloseListener(new WindowCloseListener() {
                    @Override
                    public void dismiss() {
                        arrowUp(false);
                    }
                });
                dialog.setOnStringClickListener(new SelectItemDialog.OnStringClickListener() {
                    @Override
                    public void onClick(String digital) {
                        lastDigital = currentDigital;
                        currentDigital = digital;
                        if (digitTextView != null) {
                            String unit = getContext().getString(R.string.common_precesion_unit) + getContext().getString(R.string.common_precesion);
                            String text = type == 3 ? currentDigital + unit : currentDigital;
                            digitTextView.setText(text);
                        }
                        refreshDigit(false);
                    }
                });
                dialog.show();
                break;
        }
    }

    protected void arrowUp(boolean isUp) {
        Drawable drawable = getResources().getDrawable(Pub.getThemeResource(getContext(),
                isUp ? R.attr.common_arrow_up :
                        R.attr.common_arrow_down));
        drawable.setBounds(0, 0, UIUtil.dip2px(getContext(), 8), UIUtil.dip2px(getContext(), 4));
        digitTextView.setCompoundDrawablePadding(UIUtil.dip2px(getContext(), 2));
        digitTextView.setCompoundDrawables(null, null, drawable, null);
    }

    @Override
    public void digitalChange(String digital) {
        lastDigital = currentDigital;
        currentDigital = digital;
        if (digitTextView != null) {
            String unit = getContext().getString(R.string.common_precesion_unit) + getContext().getString(R.string.common_precesion);
            String text = type == 3 ? currentDigital + unit : currentDigital;
            digitTextView.setText(text);
        }
        refreshDigit(false);
    }

    public void refreshDepth(List<EntrustBean> buys, List<EntrustBean> sells) {
        if (buys != null && buys.size() > 0) {
            maxBuyVol = 0.0;
            for (int i = 0; i < buys.size(); i++) {
                EntrustBean each = buys.get(i);
                if (each.getDoubleAmount() > maxBuyVol) {
                    maxBuyVol = buys.get(i).getDoubleAmount();
                }
            }
            bottomFiveAdapter.putList(buys);
            bottomFive.onRefreshView(false);
        } else {
            maxBuyVol = 0.0;
            bottomFiveAdapter.putList(null);
            bottomFive.onRefreshView(false);
        }

        if (sells != null && sells.size() > 0) {
            maxSellVol = 0.0;
            for (int i = 0; i < sells.size(); i++) {
                EntrustBean each = sells.get(i);
                if (each.getDoubleAmount() > maxSellVol) {
                    maxSellVol = sells.get(i).getDoubleAmount();
                }
            }

            topFiveAdapter.putList(sells);
            topFive.onRefreshView(true);
        } else {
            maxSellVol = 0.0;
            topFiveAdapter.putList(null);
            topFive.onRefreshView(true);
        }
    }

    DepthRefreshViewListener listener;

    public void setListener(DepthRefreshViewListener listener) {
        this.listener = listener;
    }

    public void resetTicker() {
        this.currentPrice = null;
        depthAllContentIsup = true;
    }

    public void setTickInfo(CurrentPriceBean model) {
        if (model == null) {
            resetTickForNull();
            return;
        }

        if (currentPrice != null && model != null) {
            double delta = Pub.GetDouble(model.getPrice()) - Pub.GetDouble(currentPrice.getPrice());
            if (delta != 0) {
                depthAllContentIsup = delta - 0 > 0;
            }
        }
        this.currentPrice = model;
//        UIUtil.setText(txtTickerPrice, model.getPriceFloor(currentDigital));
        UIUtil.setText(txtTickerPrice, model.getPrice());
        UIUtil.setTextColor(txtTickerPrice, AppConfigs.getColor(depthAllContentIsup));
        UIUtil.setText(txtTickerTrend, model.getDailyReturn());
        UIUtil.setTextColor(txtTickerTrend, AppConfigs.getColor(depthAllContentIsup));
        imgUpDown.setUp(depthAllContentIsup);
    }

    private void resetTickForNull() {
        UIUtil.setText(txtTickerPrice, getResources().getString(R.string.common_null));
        UIUtil.setTextColor(txtTickerPrice, AppConfigs.getColor(true));
        UIUtil.setText(txtTickerTrend, "+--%");
        UIUtil.setTextColor(txtTickerTrend, AppConfigs.getColor(true));
    }

    public interface DepthRefreshViewListener {

        void onClickItem(EntrustBean bean);

        void onTickClick(CurrentPriceBean currentPrice);

        /**
         * @param remove 取消订阅的小数位
         * @param add    订阅的小数位
         *               此方法回调需要刷新五档的订阅
         */
        void onRefreshDigital(String remove, String add);
    }

    /**
     * 获取对手价
     */
    public String get1Price(boolean isBuy) {
        if (isBuy) {
            return getBuy1Price();
        } else {
            return getSell1Price();
        }
    }

    /**
     * 获取卖1价
     */
    private String getSell1Price() {
        if (topFiveAdapter != null && Pub.isListExists(topFiveAdapter.getListData()) &&
                topFiveAdapter.getListData().get(0) != null) {
            return topFiveAdapter.getListData().get(0).getPrice();
        }
        return "";
    }

    /**
     * 获取买1价
     */
    private String getBuy1Price() {
        if (bottomFiveAdapter != null && Pub.isListExists(bottomFiveAdapter.getListData()) &&
                bottomFiveAdapter.getListData().get(0) != null
                ) {
            return bottomFiveAdapter.getListData().get(0).getPrice();
        }
        return "";
    }

    /**
     * 获取买1价
     */
    public String getCurrentPrice() {
        if (txtTickerPrice == null) {
            return "";
        }
        return txtTickerPrice.getText().toString();
    }

}
