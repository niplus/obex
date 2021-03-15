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

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * @author fanjianwei
 * @Description 标题栏自定义控件：
 * @date 创建日期 20160419
 */

/**
 * My app title
 *
 * @author fjw
 */
public class TitleLayout extends LinearLayout /*implements TitleInterface*/ {

    public static final int SHOWLEFTICON = 0x10;
    public static final int SHOWRIGHTICON = 0x01;

    private OnLeftButtonClickListener mLeftButtonClickListener;
    private OnRightButtonClickListener mRightButtonClickListener;
    private OnRightMinButtonClickListener mRightMiddleButtonClickListener;
    private View viewAppTitle;

    private ImageView ivRightComplete2;


    private View titleline;
    private TextView ivRightMiddleCount;


    public TitleLayout(Context context) {
        super(context);
        init();
    }

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    protected int getBackXml() {
        return R.layout.titlebar;
    }

    public void setRightMinCount(int count) {
        if (ivRightMiddleCount == null) {
            return;
        }
        if (count > 0) {
            ivRightMiddleCount.setVisibility(View.VISIBLE);
            ivRightMiddleCount.setText(count >= 100 ? "99+" : count + "");
        } else {
            ivRightMiddleCount.setVisibility(View.GONE);
        }
    }

    public ImageView setIvRightComplete(int imageViewId) {
        ivRightComplete.setBackgroundResource(imageViewId);
        ivRightComplete.setVisibility(VISIBLE);
        return ivRightComplete;
    }

    public ImageView setIvRightComplete2(int imageViewId) {
        ivRightComplete2.setBackgroundResource(imageViewId);
        ivRightComplete2.setVisibility(VISIBLE);
        return ivRightComplete2;
    }

    public void setRightCompleteVisibility(int visibility) {
        ivRightComplete.setVisibility(visibility);
    }

    public ImageView getIvRightComplete() {
        return ivRightComplete;
    }

    protected void init() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        viewAppTitle = inflater.inflate(getBackXml(), this);
        llLeftGoBack = (LinearLayout) findViewById(R.id.llLeftGoBack);
        ivLeft = (ImageView) findViewById(R.id.ivLeftBack);
        tvLeft = (TextView) findViewById(R.id.tvLeftBack);
        ivRightComplete2 = (ImageView) findViewById(R.id.ivRightComplete2);
        tvCenterTitle = (TextView) findViewById(R.id.tvCenterTitle);
        llRight = (LinearLayout) findViewById(R.id.llRight);
        ivRightComplete = (ImageView) findViewById(R.id.ivRightComplete);
        tvRightComplete = (TextView) findViewById(R.id.tvRightComplete);
        ivRightMiddle = (ImageView) findViewById(R.id.ivRightMiddle);
        ivRightMiddleCount = (TextView) findViewById(R.id.ivRightMiddle_count);
        titleline = findViewById(R.id.titleline);
        /* this.addView(viewAppTitle, layoutParams);*/
        llLeftGoBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftButtonClickListener != null) {
                    mLeftButtonClickListener.onLeftButtonClick(v);
                }
            }
        });

        ivRightComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightButtonClickListener != null) {
                    mRightButtonClickListener.onRightButtonClick(v);
                }
            }
        });

        tvRightComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightButtonClickListener != null) {
                    mRightButtonClickListener.onRightButtonClick(v);
                }
            }
        });

        ivRightMiddle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightMiddleButtonClickListener != null) {
                    mRightMiddleButtonClickListener.onRightMinButtonClick(v);
                }
            }
        });

        setBackgroundColor(Pub.getColor(getContext(), R.attr.bg_color));
    }

    /**
     * 控制标题是否显示
     *
     * @param visible 11表示都显示  可以用 TitleLayout.SHOWLEFTICON|TitleLayout.SHOWRIGHTICON表示
     */
    public void setViewsVisible(int visible) {
        // 左侧返回
        llLeftGoBack.setVisibility((visible & SHOWLEFTICON) == 0x10 ? View.VISIBLE :
                View.INVISIBLE);
        llRight.setVisibility((visible & SHOWRIGHTICON) == 0x01 ? View.VISIBLE : View
                .INVISIBLE);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setAppTitle(String title) {
        if (tvCenterTitle == null) {
            return;
        }
        if (!TextUtils.isEmpty(title)) {
            tvCenterTitle.setText(title);
        }
    }

    public TextView getTitleTv() {
        return tvCenterTitle;
    }


    public TextView getTvRightComplete() {
        return tvRightComplete;
    }

    public void setRightIcon(int sourceID) {
        ivRightComplete.setImageResource(sourceID);
    }

    /**
     * 设置右侧图标大小
     *
     * @param width
     * @param heigh
     */
    public void setRightIconSize(int width, int heigh) {
        LayoutParams params = new LayoutParams(width,
                heigh);
        ivRightComplete.setLayoutParams(params);
    }

    public void setLeftIcon(int sourceID) {
        ivLeft.setImageResource(sourceID);
    }

    public void setRightText(String str) {
        if (!TextUtils.isEmpty(str)) {
            tvRightComplete.setText(str);
            tvRightComplete.setVisibility(VISIBLE);
        }
    }

    public void setRightTextSize(float size) {
        tvRightComplete.setTextSize(COMPLEX_UNIT_PX, size);
    }

    public void setCenterTitleSize(float size) {
//        tvCenterTitle.setTextSize(COMPLEX_UNIT_PX, size);
        tvCenterTitle.setTextSize(COMPLEX_UNIT_SP, size);
    }

    public String getRightText() {
        return tvRightComplete.getText().toString();
    }

    public void setRightTextColor(int color) {
        tvRightComplete.setTextColor(color);
    }

    public void setTitleTextColor(int color) {
        if (tvCenterTitle == null) {
            return;
        }
        tvCenterTitle.setTextColor(color);
    }

    public void setLeftText(String str) {
        tvLeft.setText(str);
    }

    public void setAppBackground(int color) {
        viewAppTitle.setBackgroundColor(color);
    }

    public View getViewAppTitle() {
        return viewAppTitle;
    }

    public void setAppLineColor(int color) {
        titleline.setBackgroundColor(color);
    }


    public void setOnLeftButtonClickListener(OnLeftButtonClickListener listen) {
        mLeftButtonClickListener = listen;
    }

    public void setOnRightButtonClickListener(OnRightButtonClickListener listen) {
        mRightButtonClickListener = listen;
    }

    public void setOnRightMinButtonClickListener(OnRightMinButtonClickListener listen) {
        mRightMiddleButtonClickListener = listen;
    }

    public interface OnLeftButtonClickListener {
        /**
         * 点击左按钮事件监听
         *
         * @param v
         */
        void onLeftButtonClick(View v);
    }

    public interface OnRightButtonClickListener {
        /**
         * 点击右按钮事件监听
         *
         * @param v
         */
        void onRightButtonClick(View v);
    }

    public interface OnRightMinButtonClickListener {
        /**
         * 点击右过来第二个按钮事件监听
         *
         * @param v
         */
        void onRightMinButtonClick(View v);
    }

    public TextView tvLeft;
    ImageView ivLeft;
    public LinearLayout llLeftGoBack;
    TextView tvCenterTitle;
    public LinearLayout llRight;
    ImageView ivRightComplete;
    ImageView ivRightMiddle;
    public TextView tvRightComplete;
}
