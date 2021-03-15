package com.fota.android.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;

/**
 * @author jiang
 * @Description 标题栏自定义控件：
 * @date 创建日期 20180806
 */

/**
 * market kline timeline
 * tabbar titlebar
 *
 * @author jiang
 */
public class KlineTitleBarLayout extends LinearLayout /*implements TitleInterface*/ {
    private OnLeftClickListener mLeftClickListener;
    //放大  全屏 or
    private OnRightImage1ClickListener mRightImage1ClickListener;
    //K线 or 分时线
    private OnRightImage2ClickListener mRightImage2ClickListener;
    //full screen 缩小 非全屏
    private OnRightImage3ClickListener mRightImage3ClickListener;

    private View rootView;
    private LinearLayout leftLinearLayout;
    private ImageView imgBack;
    private TextView tvTitle;
    private TextView tvFutureTip;
    private TextView tvPeriod;
    //full sreen or not
    private ImageView ivRightMagnify;
    // time -- klin change
    private ImageView ivRightTypeChange;
    //full sreen or not
    private ImageView ivRightMinify;

    public KlineTitleBarLayout(Context context) {
        super(context);
        init();
    }

    public KlineTitleBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KlineTitleBarLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected int getBackXml() {
        return R.layout.content_kline_title_header;
    }

    protected void init() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(getBackXml(), this);
        leftLinearLayout = findViewById(R.id.llLeft);
        imgBack = findViewById(R.id.img_back);
        tvTitle = findViewById(R.id.tv_future_name);
        tvFutureTip = findViewById(R.id.tv_future_tip);
        tvPeriod = findViewById(R.id.tv_period);

        ivRightMagnify = findViewById(R.id.img_magnify);
        ivRightTypeChange = findViewById(R.id.img_type_change);
        ivRightMinify = findViewById(R.id.img_minify);
        /* this.addView(viewAppTitle, layoutParams);*/
        tvTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftClickListener != null) {
                    mLeftClickListener.onLeftClick(v);
                }
            }
        });
        leftLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftClickListener != null) {
                    mLeftClickListener.onLeftClick(v);
                }
            }
        });

        ivRightMagnify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightImage1ClickListener != null) {
                    mRightImage1ClickListener.onRightScaleClick(v);
                }
            }
        });

        ivRightTypeChange.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightImage2ClickListener != null) {
                    mRightImage2ClickListener.onRightTypeClick(v);
                }
            }
        });

        ivRightMinify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRightImage3ClickListener != null) {
                    mRightImage3ClickListener.onRightMinifyClick(v);
                }
            }
        });
    }

    public void setFutureTip(String msg) {
        if (tvFutureTip == null) {
            return;
        }
        if (!TextUtils.isEmpty(msg)) {
            tvFutureTip.setText(msg);
        }
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (tvTitle == null) {
            return;
        }
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
    }

    public void setmLeftClickListener(OnLeftClickListener mLeftClickListener) {
        this.mLeftClickListener = mLeftClickListener;
    }

    public void setmRightImage1ClickListener(OnRightImage1ClickListener mRightImage1ClickListener) {
        this.mRightImage1ClickListener = mRightImage1ClickListener;
    }

    public void setmRightImage2ClickListener(OnRightImage2ClickListener mRightImage2ClickListener) {
        this.mRightImage2ClickListener = mRightImage2ClickListener;
    }

    public void setmRightImage3ClickListener(OnRightImage3ClickListener mRightImage3ClickListener) {
        this.mRightImage3ClickListener = mRightImage3ClickListener;
    }

//    public TextView getTvPeriod() {
//        return tvPeriod;
//    }
//
//    public void setTvPeriodVisible(boolean visible) {
//        if(tvPeriod != null) {
//            if(visible)
//                tvPeriod.setVisibility(VISIBLE);
//            else
//                tvPeriod.setVisibility(INVISIBLE);
//        }
//    }

    public LinearLayout getLeftLinearLayout() {
        return leftLinearLayout;
    }

    public void setLeftLinearLayout(LinearLayout leftLinearLayout) {
        this.leftLinearLayout = leftLinearLayout;
    }

    public ImageView getImgBack() {
        return imgBack;
    }

    public void setImgBack(ImageView imgBack) {
        this.imgBack = imgBack;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(TextView tvTitle) {
        this.tvTitle = tvTitle;
    }

    public TextView getTvFutureTip() {
        return tvFutureTip;
    }

    public void setTvFutureTip(TextView tvFutureTip) {
        this.tvFutureTip = tvFutureTip;
    }

    public ImageView getIvRightMagnify() {
        return ivRightMagnify;
    }

    public void setIvRightManifyVisible(boolean visible) {
        if(ivRightMagnify != null) {
            if(visible) {
                ivRightMagnify.setVisibility(VISIBLE);
                if(ivRightTypeChange != null) {
                    ivRightTypeChange.setImageResource(Pub.getThemeResource(getContext(), R.attr.chart_time_line));
                }
            } else {
                ivRightMagnify.setVisibility(INVISIBLE);
                if(ivRightTypeChange != null) {
                    ivRightTypeChange.setImageResource(Pub.getThemeResource(getContext(), R.attr.chart_kline));
                }
            }
        }
    }

    public void setIvRightTypeVisible(boolean visible) {
        if(ivRightTypeChange != null) {
            if(visible) {
                ivRightTypeChange.setVisibility(VISIBLE);
            } else {
                ivRightTypeChange.setVisibility(INVISIBLE);
            }
        }
    }

    public void setIvMinifyVisible(boolean visible) {
        if(ivRightMinify != null) {
            if(visible) {
                ivRightMinify.setVisibility(VISIBLE);
                if(ivRightMagnify != null) {
                    ivRightMagnify.setVisibility(GONE);
                }
                if(ivRightTypeChange != null) {
                    ivRightTypeChange.setVisibility(GONE);
                }
            } else {
                ivRightMinify.setVisibility(GONE);
                if(ivRightMagnify != null) {
                    ivRightMagnify.setVisibility(VISIBLE);
                }
                if(ivRightTypeChange != null) {
                    ivRightTypeChange.setVisibility(VISIBLE);
                }
            }
//            setTvPeriodVisible(visible);
        }
    }

    public ImageView getIvRightTypeChange() {
        return ivRightTypeChange;
    }

    public void setIvRightTypeChange(ImageView ivRightTypeChange) {
        this.ivRightTypeChange = ivRightTypeChange;
    }

    public interface OnLeftClickListener {
        /**
         * 点击左按钮事件监听
         *
         * @param v
         */
        void onLeftClick(View v);
    }

    public interface OnRightImage1ClickListener {
        /**
         * 点击右按钮事件监听
         *
         * @param v
         */
        void onRightScaleClick(View v);
    }

    public interface OnRightImage2ClickListener {
        /**
         * 点击右过来第二个按钮事件监听
         *
         * @param v
         */
        void onRightTypeClick(View v);
    }

    public interface OnRightImage3ClickListener {
        /**
         * 点击右过来缩小按钮事件监听
         *
         * @param v
         */
        void onRightMinifyClick(View v);
    }
}
