package com.fota.android.moudles.welcome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.commonlib.utils.WelcomeUtil;
import com.fota.android.core.base.BaseActivity;
import com.fota.android.moudles.main.MainActivity;
import com.fota.android.moudles.welcome.adapter.GuideViewPagerAdapter;
import com.fota.android.widget.btbwidget.FixedSpeedScroller;
import com.fota.android.widget.myview.BounceBackViewPager;
import com.fota.android.widget.myview.CustomPageIndicator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Administrator on 2018/3/28.
 */

public class WelcomeActivity extends BaseActivity implements BounceBackViewPager.DispatchDrawListener {
    BounceBackViewPager viewPager;
    TextView tv_enter, tv_msg1, tv_msg2;
    LinearLayout ll_text, ll_viewpager;
    CustomPageIndicator customPageIndicator;
    View view_bg;

    private GuideViewPagerAdapter adapter;
    private List<View> views;
    private boolean isScrolling = false;

    private float lastPercent = -1;
    int count = 4;
    private String msg1Color = "#FFA4ACBD";
    private String msg1ColorAlpf = "#00000000";
    private String msg2Color = "#FF606572";
    private String msg2ColorAlpf = "#00000000";
    private int pageIndex = 0;
    RelativeLayout.LayoutParams paramLL;
    RelativeLayout.LayoutParams paramEnter;
    RelativeLayout.LayoutParams paramIndicator;
    private Bitmap bg;

    int startmargin;
    int endmargin;
    int totalmargin;
    View checkedView = null;
    CountDownTimer timer;//边界回弹渐变效果
    int overmoveleft;//记录开始回弹的位置
    int indicatorWidth;


    // 引导页图片资源
    private static final int[] pics = {R.layout.guid_view1,
            R.layout.guid_view2, R.layout.guid_view3, R.layout.guid_view4};

    @Override
    protected void onDestroy() {
        if (bg != null)
            bg.recycle();
        bg = null;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (mImmersionBar != null) {
            mImmersionBar.statusBarDarkFont(false, 0.2f);
            mImmersionBar.init();
        }
        views = new ArrayList<View>();

        // 初始化引导页视图列表
        for (int i = 0; i < pics.length; i++) {
            View view = LayoutInflater.from(this).inflate(pics[i], null);
            views.add(view);
        }

        view_bg = findViewById(R.id.view_bg);
        viewPager = (BounceBackViewPager) findViewById(R.id.vp_guide);
        customPageIndicator = findViewById(R.id.pagerIndicator);
        tv_enter = findViewById(R.id.tv_enter);
        ll_text = findViewById(R.id.ll_text);
        tv_msg1 = findViewById(R.id.tv_msg1);
        tv_msg2 = findViewById(R.id.tv_msg2);
        ll_viewpager = findViewById(R.id.ll_viewpager);
        paramLL = (RelativeLayout.LayoutParams) ll_text.getLayoutParams();
        paramEnter = (RelativeLayout.LayoutParams) tv_enter.getLayoutParams();
        paramIndicator = (RelativeLayout.LayoutParams) customPageIndicator.getLayoutParams();
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setPageMargin(0);//设置间距
        viewPager.setBackGroud(BitmapFactory.decodeResource(getResources(), R.mipmap.welcome_bg));
        viewPager.setDispatchDrawListener(this);

        bg = BitmapFactory.decodeResource(getResources(), R.mipmap.welcome_bg);
        totalmargin = totalMagin();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view_bg.getLayoutParams();
        params.height = UIUtil.getScreenWidth(this) * 5 / 4;
        params.setMargins(-1 * startmargin - totalmargin, UIUtil.dip2px(this, 40), -1 * endmargin, 0);
        view_bg.setLayoutParams(params);
        viewPager.setDispatchDrawListener(this);

//        bgtTotalWidth = getBgTotalWidth();


        // 初始化adapter
        adapter = new GuideViewPagerAdapter(views);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new PageChangeListener());
//        controlViewPagerSpeed();

        int marginLeft = (UIUtil.getScreenWidth(this) - customPageIndicator.getIndicWidth()) / 2;
        int marginBottom = UIUtil.dip2px(this, 15) + (int) (UIUtil.dip2px(this, 60));
        L.a("marginleft = " + marginLeft);
        paramIndicator.leftMargin = marginLeft;
        paramIndicator.bottomMargin = marginBottom;
        customPageIndicator.setLayoutParams(paramIndicator);
        customPageIndicator.setIndiaSize(1.0f);
        tv_enter.getBackground().setAlpha(0);

        indicatorWidth = customPageIndicator.getIndicWidth();

    }

    private void enterMainActivity() {
        Intent intent = new Intent(this,
                MainActivity.class);
        startActivity(intent);
        SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.FIRST_OPEN, true);
        finish();
    }

    /**
     * 监听
     *
     * @param scrollx
     */
    @Override
    public void dispatchDraw(int scrollx) {
        int totalWidth = 3 * UIUtil.getScreenWidth(this);
        int left = (int) (totalmargin * (1.0f - 1.0f * scrollx / totalWidth) + startmargin);
        int right = (int) (totalmargin * 1.0f * scrollx / totalWidth + endmargin);


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view_bg.getLayoutParams();

        params.setMargins(-1 * left, UIUtil.dip2px(this, 40), -1 * right, 0);
        view_bg.setLayoutParams(params);
    }

    @Override
    public void moveOver1(int left, int right) {

    }

    @Override
    public void moveOver2(int left, int right) {//最左边变大 0-300
        overmoveleft = left;
        paramLL.leftMargin = UIUtil.dip2px(this, 40) + (int) (UIUtil.dip2px(this, 40) * 1.0f * left / 500);
        tv_msg1.setTextColor(WelcomeUtil.evaluate(1.0f * left / 500, WelcomeUtil.getColorint(msg1Color), WelcomeUtil.getColorint(msg1ColorAlpf)));
        tv_msg2.setTextColor(WelcomeUtil.evaluate(1.0f * left / 500, WelcomeUtil.getColorint(msg2Color), WelcomeUtil.getColorint(msg2ColorAlpf)));
        ll_text.setLayoutParams(paramLL);

//        int width = (int)(UIUtil.getScreenWidth(this)*1.0f*(300-left)/300);
//        int height = (int)(UIUtil.getScreenHeigh(this)*1.0f*(300-left)/300);
//        RelativeLayout.LayoutParams paramLLVp = (RelativeLayout.LayoutParams) ll_viewpager.getLayoutParams();
//        paramLLVp.width = width;
//        paramLLVp.height = height;
//        ll_viewpager.setLayoutParams(paramLLVp);
        float scale = 0.8f + 0.2f * (500 - left) / 500;
        L.a("moveOver2 = " + scale + "  count = " + viewPager.getChildCount() + "  id= " + viewPager.getChildAt(0).getId());

        if (checkedView != null) {
            checkedView.setScaleX(scale);
            checkedView.setScaleY(scale);
        }
        viewPager.getChildAt(0).setScaleX(scale);
        viewPager.getChildAt(0).setScaleY(scale);

    }

    @Override
    public void moveOver3(int left, int right) {//最右边负值变大
        overmoveleft = left;
        tv_msg1.setTextColor(WelcomeUtil.evaluate(-1.0f * left / 500, WelcomeUtil.getColorint(msg1Color), WelcomeUtil.getColorint(msg1ColorAlpf)));
        tv_msg2.setTextColor(WelcomeUtil.evaluate(-1.0f * left / 500, WelcomeUtil.getColorint(msg2Color), WelcomeUtil.getColorint(msg2ColorAlpf)));
        paramLL.leftMargin = UIUtil.dip2px(this, 40) + (int) (UIUtil.dip2px(this, 40) * 1.0f * left / 500);
        ll_text.setLayoutParams(paramLL);

        float scale = 0.8f + 0.2f * (500 + left) / 500;
        if (checkedView != null) {
            checkedView.setScaleX(scale);
            checkedView.setScaleY(scale);
        }
        viewPager.getChildAt(0).setScaleX(scale);
        viewPager.getChildAt(0).setScaleY(scale);

//        int totalWidth = 3 * UIUtil.getScreenWidth(this);
//        int totald = totalWidth / 120;
//        int marginleft = (int) (1.0f * totald * left / 300 + startmargin);
//        int marginright = (int) (totalmargin + -1.0f * totald * left / 300 + endmargin);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view_bg.getLayoutParams();
//        params.setMargins(-1 * marginleft, UIUtil.dip2px(this, 40), -1 * marginright, 0);
//        view_bg.setLayoutParams(params);
    }

    @Override
    public void moveOver4(int left, int right) {//最左边正直变大  最右边负值变大
        overmoveleft = left;
        if (left > 0) {
            paramLL.leftMargin = UIUtil.dip2px(this, 40) + (int) (UIUtil.dip2px(this, 40) * 1.0f * left / 500);
            ll_text.setLayoutParams(paramLL);

            tv_msg1.setTextColor(WelcomeUtil.evaluate(1.0f * left / 500, WelcomeUtil.getColorint(msg1Color), WelcomeUtil.getColorint(msg1ColorAlpf)));
            tv_msg2.setTextColor(WelcomeUtil.evaluate(1.0f * left / 500, WelcomeUtil.getColorint(msg2Color), WelcomeUtil.getColorint(msg2ColorAlpf)));

//            int width = (int)(UIUtil.getScreenWidth(this)*1.0f*(300-left)/300);
//            int height = (int)(UIUtil.getScreenHeigh(this)*1.0f*(300-left)/300);
//            RelativeLayout.LayoutParams paramLLVp = (RelativeLayout.LayoutParams) ll_viewpager.getLayoutParams();
//            paramLLVp.width = width;
//            paramLLVp.height = height;
//            ll_viewpager.setLayoutParams(paramLLVp);
            float scale = 0.8f + 0.2f * (500 - left) / 500;
            L.a("moveOver4 = " + scale + "  count = " + viewPager.getChildCount() + "  id= " + viewPager.getChildAt(0).getId());
            if (checkedView != null) {
                checkedView.setScaleX(scale);
                checkedView.setScaleY(scale);
            }
            viewPager.getChildAt(0).setScaleX(scale);
            viewPager.getChildAt(0).setScaleY(scale);


        } else {
            tv_msg1.setTextColor(WelcomeUtil.evaluate(-1.0f * left / 500, WelcomeUtil.getColorint(msg1Color), WelcomeUtil.getColorint(msg1ColorAlpf)));
            tv_msg2.setTextColor(WelcomeUtil.evaluate(-1.0f * left / 500, WelcomeUtil.getColorint(msg2Color), WelcomeUtil.getColorint(msg2ColorAlpf)));
            paramLL.leftMargin = UIUtil.dip2px(this, 40) + (int) (UIUtil.dip2px(this, 40) * 1.0f * left / 500);
            ll_text.setLayoutParams(paramLL);

            int totalWidth = 3 * UIUtil.getScreenWidth(this);
            int totald = totalWidth / 120;
            int marginleft = (int) (1.0f * totald * left / 500 + startmargin);
            int marginright = (int) (totalmargin + -1.0f * totald * left / 500 + endmargin);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view_bg.getLayoutParams();
            params.setMargins(-1 * marginleft, UIUtil.dip2px(this, 40), -1 * marginright, 0);
            view_bg.setLayoutParams(params);

            float scale = 0.8f + 0.2f * (500 + left) / 500;
            L.a("moveOver4 = " + scale);
            viewPager.getChildAt(0).setScaleX(scale);
            viewPager.getChildAt(0).setScaleY(scale);
        }

    }

    @Override
    public void moveOver5(int left, int right) {//松手
//        paramLL.leftMargin = UIUtil.dip2px(this, 40);
//        ll_text.setLayoutParams(paramLL);

//        tv_msg1.setTextColor(WelcomeUtil.evaluate(0, WelcomeUtil.getColorint(msg1Color), WelcomeUtil.getColorint(msg1ColorAlpf)));
//        tv_msg2.setTextColor(WelcomeUtil.evaluate(0, WelcomeUtil.getColorint(msg2Color), WelcomeUtil.getColorint(msg2ColorAlpf)));

//        if (checkedView != null) {
//            checkedView.setScaleX(1.0f);
//            checkedView.setScaleY(1.0f);
//        }
//        viewPager.getChildAt(0).setScaleX(1.0f);
//        viewPager.getChildAt(0).setScaleY(1.0f);

        timer = new CountDownTimer(300, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                int left = overmoveleft >= 0 ? overmoveleft : -1 * overmoveleft;
                float scale = 0.8f + 0.2f * (500 - left) / 500 + 0.2f * (left) / 500 * (30 - millisUntilFinished / 10) / 30;
                L.a("moveOver4 = " + scale);
                if (checkedView != null) {
                    checkedView.setScaleX(scale);
                    checkedView.setScaleY(scale);
                }
                viewPager.getChildAt(0).setScaleX(scale);
                viewPager.getChildAt(0).setScaleY(scale);

                float color = 1.0f * left / 500 - 1.0f * left / 500 * (30 - millisUntilFinished / 10) / 30;
                L.a("color = " + color);
                tv_msg1.setTextColor(WelcomeUtil.evaluate(1.0f * left / 500 - 1.0f * left / 500 * (30 - millisUntilFinished / 10) / 30, WelcomeUtil.getColorint(msg1Color), WelcomeUtil.getColorint(msg1ColorAlpf)));
                tv_msg2.setTextColor(WelcomeUtil.evaluate(1.0f * left / 500 - 1.0f * left / 500 * (30 - millisUntilFinished / 10) / 30, WelcomeUtil.getColorint(msg2Color), WelcomeUtil.getColorint(msg2ColorAlpf)));

                if (overmoveleft > 0) {
                    paramLL.leftMargin = UIUtil.dip2px(WelcomeActivity.this, 40) + (int) (UIUtil.dip2px(WelcomeActivity.this, 40) * 1.0f * left / 500 * (millisUntilFinished / 10) / 30);
                } else {
                    paramLL.leftMargin = UIUtil.dip2px(WelcomeActivity.this, 40) - (int) (UIUtil.dip2px(WelcomeActivity.this, 40) * 1.0f * left / 500 * (millisUntilFinished / 10) / 30);
                }
                ll_text.setLayoutParams(paramLL);
            }

            @Override
            public void onFinish() {
//                phoneReset();
            }
        };
        timer.start();
    }

    /**
     * 总的超出屏幕的像素
     * 按显示背景长款3比4
     *
     * @return
     */
    private int totalMagin() {
        if (this.bg == null) {
            this.bg = BitmapFactory.decodeResource(getResources(), R.mipmap.welcome_bg);
        }
        if (bg == null)
            return 0;
        int width = this.bg.getWidth();
        int height = this.bg.getHeight();
        int margin = (int) ((5.0f / 4 * width / height - 1) * UIUtil.getScreenWidth(this));

        startmargin = (int) (5.0f / 4 * width / height * UIUtil.getScreenWidth(this)) / 20;
        endmargin = (int) (5.0f / 4 * width / height * UIUtil.getScreenWidth(this)) / 10;
        return margin - startmargin - endmargin;

    }


    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        // 当滑动状态改变时调用
        @Override
        public void onPageScrollStateChanged(int position) {
            L.a("onPageScrollStateChanged position= " + position + " pageIndex = " + pageIndex);
            if (position != 0) {
                isScrolling = true;
            } else {
                isScrolling = false;
                if (pageIndex == pics.length - 1) {
                    tv_enter.setEnabled(true);
//                    tv_enter.getBackground().setAlpha(1);
//                    tv_enter.setTextColor(WelcomeUtil.evaluate(1, WelcomeUtil.getColorint(msg2ColorAlpf), WelcomeUtil.getColorint("#FFFFFFFF")));
                    customPageIndicator.setFullIndiaSize(pageIndex, 0.0f);

                } else {
                    tv_enter.setEnabled(false);
                    tv_enter.getBackground().setAlpha(0);
                    tv_enter.setTextColor(WelcomeUtil.evaluate(0, WelcomeUtil.getColorint(msg2ColorAlpf), WelcomeUtil.getColorint("#FFFFFFFF")));
                    int marginLeft = (UIUtil.getScreenWidth(getActivity()) - indicatorWidth) / 2;
                    int marginBottom = UIUtil.dip2px(getActivity(), 15) + (int) (UIUtil.dip2px(getActivity(), 60));
                    L.a("marginleft onPageSelected= " + marginLeft);
                    paramIndicator.leftMargin = marginLeft;
                    paramIndicator.bottomMargin = marginBottom;
                    customPageIndicator.setLayoutParams(paramIndicator);

                    if (pageIndex == pics.length - 2) {
                        customPageIndicator.setFullIndiaSize(pageIndex, 1.0f);
                    } else {
                        customPageIndicator.setIndiaSize(1.0f);
                    }
                }
            }

        }

        // 当前页面被滑动时调用
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            L.a("positoion = " + position + "  positionOffset = " + positionOffset + "  positionOffsetPixels = " + positionOffsetPixels);

            float percent = positionOffset;
            if (isScrolling && percent > 0.05f && percent < 0.95f) {
                customPageIndicator.setScrollProgress(position, percent);
            }

            setTextAlpa(position, percent);
            setText(position, percent);
            setTextMove(position, percent);
            setEnter(position, percent);
            lastPercent = percent;
        }

        // 当新的页面被选中时调用
        @Override
        public void onPageSelected(int position) {
//            customPageIndicator.setActivityCircle(position);
            if (position == pics.length - 1) {


                tv_enter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        enterMainActivity();
                    }
                });
            } else {
            }
        }

    }

    class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        public static final float MAX_SCALE = 1.0f;
        public static final float MIN_SCALE = 0.6f;


        public static final float defaultScale = 0.1f;

        public void transformPage(View view, float position) {
            L.a("ZoomOutPageTransformer position = " + position);
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();
            if (position == 0 || position == 1)
                checkedView = view;


            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
//                view.setAlpha(0);
                view.setScaleX(defaultScale);
                view.setScaleY(defaultScale);
            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float tempScale = position < 0 ? 1 + position : 1 - position;

                float slope = (MAX_SCALE - MIN_SCALE) / 1;

                float scaleValue = MIN_SCALE + tempScale * slope;

                // Scale the page down (between MIN_SCALE and 1)
                L.a("ZoomOutPageTransformer scaleFactor = " + scaleValue);
                view.setScaleX(scaleValue);
                view.setScaleY(scaleValue);

                // Fade the page relative to its size.
//                view.setAlpha(MIN_ALPHA +
//                        (scaleFactor - MIN_SCALE) /
//                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
//                view.setAlpha(0);
                view.setScaleX(defaultScale);
                view.setScaleY(defaultScale);
            }
        }
    }

    /**
     * 设置文字内容
     *
     * @param position
     * @param percent
     */
    private void setText(int position, float percent) {
        int page = 0;
        if (percent > 1.0f || percent < 0.0f)
            return;
        // 手指右滑-递减,position是下一page
        if (lastPercent > percent) {
            if (percent == 0f) {
                page = position;
            } else if (percent < 0.5f) {
                page = position;
            } else if (percent > 0.5f) {
                page = position + 1;
            }

        } else {//左滑-递曾,position当前page
            if (percent == 0f) {
                page = position;
            } else if (percent < 0.5f) {
                page = position;
            } else if (percent > 0.5f) {
                page = position + 1;
            }
        }
        if (page == pageIndex)
            return;
        if (page == 0) {
            tv_msg1.setText(R.string.welcome_page1_title);
            tv_msg2.setText(R.string.welcome_page1_msg);
        } else if (page == 1) {
            tv_msg1.setText(R.string.welcome_page2_title);
            tv_msg2.setText(R.string.welcome_page2_msg);
        } else if (page == 2) {
            tv_msg1.setText(R.string.welcome_page3_title);
            tv_msg2.setText(R.string.welcome_page3_msg);
        } else if (page == 3) {
            tv_msg1.setText(R.string.welcome_page4_title);
            tv_msg2.setText(R.string.welcome_page4_msg);
        }

        pageIndex = page;
    }

    /**
     * 设置文字透明度
     *
     * @param position
     * @param percent
     */
    private void setTextAlpa(int position, float percent) {
        if (percent > 1.0f || percent < 0.0f)
            return;
        // 手指右滑-递减,position是下一page
        if (lastPercent > percent) {
            if (percent < 0.25f) {
                tv_msg1.setTextColor(WelcomeUtil.evaluate(percent * 4, WelcomeUtil.getColorint(msg1Color), WelcomeUtil.getColorint(msg1ColorAlpf)));
                tv_msg2.setTextColor(WelcomeUtil.evaluate(percent * 4, WelcomeUtil.getColorint(msg2Color), WelcomeUtil.getColorint(msg2ColorAlpf)));

            } else if (percent > 0.75f) {
                tv_msg1.setTextColor(WelcomeUtil.evaluate((percent - 0.75f) * 4, WelcomeUtil.getColorint(msg1ColorAlpf), WelcomeUtil.getColorint(msg1Color)));
                tv_msg2.setTextColor(WelcomeUtil.evaluate((percent - 0.75f) * 4, WelcomeUtil.getColorint(msg2ColorAlpf), WelcomeUtil.getColorint(msg2Color)));

            }

        } else {//左滑-递曾,position是当前page
            if (percent < 0.25f) {
                tv_msg1.setTextColor(WelcomeUtil.evaluate(percent * 4, WelcomeUtil.getColorint(msg1Color), WelcomeUtil.getColorint(msg1ColorAlpf)));
                tv_msg2.setTextColor(WelcomeUtil.evaluate(percent * 4, WelcomeUtil.getColorint(msg2Color), WelcomeUtil.getColorint(msg2ColorAlpf)));

            } else if (percent > 0.75f) {
                tv_msg1.setTextColor(WelcomeUtil.evaluate((percent - 0.75f) * 4, WelcomeUtil.getColorint(msg1ColorAlpf), WelcomeUtil.getColorint(msg1Color)));
                tv_msg2.setTextColor(WelcomeUtil.evaluate((percent - 0.75f) * 4, WelcomeUtil.getColorint(msg2ColorAlpf), WelcomeUtil.getColorint(msg2Color)));

            }
        }
    }

    /**
     * 设置文字位置
     *
     * @param position
     * @param percent
     */
    private void setTextMove(int position, float percent) {
        if (percent >= 1.0f || percent <= 0.0f)
            return;
        // 手指右滑-递减,position是下一page
        if (lastPercent > percent) {
            if (percent < 0.5f) {
                paramLL.leftMargin = UIUtil.dip2px(this, 40) - (int) (UIUtil.dip2px(this, 60) * percent);

            } else if (percent > 0.5f) {
                paramLL.leftMargin = UIUtil.dip2px(this, 40) + (int) (UIUtil.dip2px(this, 60) * (1.0f - percent));

            }
        } else {//左滑-递曾,position是当前page
            if (percent < 0.5f) {
                paramLL.leftMargin = UIUtil.dip2px(this, 40) - (int) (UIUtil.dip2px(this, 60) * percent);

            } else if (percent > 0.5f) {
                paramLL.leftMargin = UIUtil.dip2px(this, 40) + (int) (UIUtil.dip2px(this, 60) * (1.0f - percent));

            }
        }
        ll_text.setLayoutParams(paramLL);
    }

    /**
     * 设置进入应用按钮效果
     *
     * @param position
     * @param percent
     */
    private void setEnter(int position, float percent) {
        if (percent >= 1.0f || percent <= 0.0f)
            return;
        // 手指右滑-递减,position是下一page
        if (lastPercent > percent) {
            if (position < 2)
                return;
            //指示器开始 left 25-居中 bottom 15-60
            int dValueLeft = (UIUtil.getScreenWidth(this) - customPageIndicator.getIndicWidth()) / 2 - UIUtil.dip2px(this, 25);
            int marginLeft = UIUtil.dip2px(this, 25) + (int) (dValueLeft * (1.0f - percent));
            L.a("marginleft = " + marginLeft + "  dvalue = " + dValueLeft);
            int marginBottom = UIUtil.dip2px(this, 15) + (int) (UIUtil.dip2px(this, 60) * (1.0f - percent));
            paramIndicator.leftMargin = marginLeft;
            paramIndicator.bottomMargin = marginBottom;
            //指示器圆点大小
            customPageIndicator.setIndiaSize(1.0f - percent);
            if (percent > 0.5f) {
                paramEnter.bottomMargin = UIUtil.dip2px(this, 50) - (int) (UIUtil.dip2px(this, 20) * (1.0f - percent) * 2);
                tv_enter.getBackground().setAlpha((int) ((percent - 0.5f) * 2 * 255));
                tv_enter.setTextColor(WelcomeUtil.evaluate((percent - 0.5f) * 2, WelcomeUtil.getColorint(msg2ColorAlpf), WelcomeUtil.getColorint("#FFFFFFFF")));

            } else {
                paramEnter.bottomMargin = UIUtil.dip2px(this, 50) - (int) (UIUtil.dip2px(this, 20) * (1.0f - percent) * 2);
                tv_enter.getBackground().setAlpha(0);
                tv_enter.setTextColor(WelcomeUtil.evaluate(0, WelcomeUtil.getColorint(msg2ColorAlpf), WelcomeUtil.getColorint("#FFFFFFFF")));

            }
        } else {//左滑-递曾,position是当前page
            if (position < 2)
                return;
            if (percent > 0.5f) {
                paramEnter.bottomMargin = UIUtil.dip2px(this, 50) - (int) (UIUtil.dip2px(this, 20) * (1.0f - percent) * 2);

                tv_enter.getBackground().setAlpha((int) ((percent - 0.5f) * 2 * 255));
                tv_enter.setTextColor(WelcomeUtil.evaluate((percent - 0.5f) * 2, WelcomeUtil.getColorint(msg2ColorAlpf), WelcomeUtil.getColorint("#FFFFFFFF")));
                //指示器开始 left 25-居中 bottom 15-60
                int marginLeft = UIUtil.dip2px(this, 25);
                int marginBottom = UIUtil.dip2px(this, 15);
                L.a("marginleft = " + marginLeft);
                paramIndicator.leftMargin = marginLeft;
                paramIndicator.bottomMargin = marginBottom;
                //指示器圆点大小
                customPageIndicator.setIndiaSize(0f);
            } else {
                //指示器开始 left 25-居中 bottom 15-60
                int dValueLeft = (UIUtil.getScreenWidth(this) - customPageIndicator.getIndicWidth()) / 2 - UIUtil.dip2px(this, 25);
                int marginLeft = UIUtil.dip2px(this, 25) + (int) (dValueLeft * (0.5f - percent) * 2);
                L.a("marginleft = " + marginLeft + "  dvalue = " + dValueLeft);
                int marginBottom = UIUtil.dip2px(this, 15) + (int) (UIUtil.dip2px(this, 60) * (0.5f - percent) * 2);
                paramIndicator.leftMargin = marginLeft;
                paramIndicator.bottomMargin = marginBottom;

                //指示器圆点大小
                customPageIndicator.setIndiaSize((0.5f - percent) * 2);
            }
        }
        tv_enter.setLayoutParams(paramEnter);
        customPageIndicator.setLayoutParams(paramIndicator);

    }


    FixedSpeedScroller mScroller = null;

    private void controlViewPagerSpeed() {
        try {
            Field mField;

            mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);

            mScroller = new FixedSpeedScroller(
                    viewPager.getContext(),
                    new AccelerateInterpolator());
            mScroller.setmDuration(400); // 2000ms
            mField.set(viewPager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置默认语言
     */
    private void setLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }
//获取语言的正确姿势:
        String lang = locale.getLanguage();// + "-" + locale.getCountry();
        L.a("language = " + lang);
        if ("en".equals(lang)) {
            AppConfigs.setLanguege(1);
        }
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        setLanguage();
    }
}
