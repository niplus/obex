package com.fota.android.moudles.mine.tradehistory;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.BaseFragmentAdapter;
import com.fota.android.databinding.FragmentAllhistoryBinding;
import com.fota.android.utils.StatusBarUtil;
import com.fota.android.widget.timepicker.CustomDatePicker;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 总的交易记录界面
 */
public class AllHistoryFragment extends BaseFragment implements View.OnClickListener {
//    private ViewDataBinding mBinding;


    private FragmentAllhistoryBinding mBinding;
    protected List<Fragment> fragments;
    private TradeHistoryFragment tradeHistoryFragment;
    private OptionHistoryFragment optionHistoryFragment;
    BaseFragmentAdapter baseFragmentAdapter;
    private CustomDatePicker datePicker;

    public static int TYPE_CONTRACT = 1;
    public static int TYPE_OPTION = 2;
    int TYPE = TYPE_CONTRACT;
    boolean toOption = false;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_allhistory,
                null, false);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        int paddingTop = StatusBarUtil.getStatusBarHeight(mContext);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mBinding.rlTitle.getLayoutParams(); // 取控件mGrid当前的布局参数
        linearParams.topMargin = paddingTop + UIUtil.dip2px(mContext, 5);
        mBinding.rlTitle.setLayoutParams(linearParams);
        if (AppConfigs.getTheme() == 0) {
            mBinding.imvDatecheck.setImageResource(R.mipmap.tradehis_time_black);
        } else {
            mBinding.imvDatecheck.setImageResource(R.mipmap.tradehis_time_white);
        }
        initViewPager();
        mBinding.tvContractClick.setOnClickListener(this);
        mBinding.tvOptionClick.setOnClickListener(this);
        mBinding.imvDatecheck.setOnClickListener(this);
        mBinding.llBack.setOnClickListener(this);

        if (AppConfigs.getTheme() == 1) {
            UIUtil.setRoundBg(mBinding.cvOption, mContext.getResources().getColor(R.color.main_color_white));
            UIUtil.setRoundBg(mBinding.cvContract, mContext.getResources().getColor(R.color.main_color_white));
            mBinding.rlCheckbg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_allhis_typecheck));

        } else {
            UIUtil.setRoundBg(mBinding.cvOption, mContext.getResources().getColor(R.color.main_color_black));
            UIUtil.setRoundBg(mBinding.cvContract, mContext.getResources().getColor(R.color.main_color_black));
            mBinding.rlCheckbg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_allhis_typecheck_black));

        }
        if (toOption) {
            setToOption();
        }


    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        toOption = bundle.getBoolean("to_option", false);
    }

    protected void initViewPager() {
        fragments = new ArrayList<>();
        tradeHistoryFragment = new TradeHistoryFragment();
        optionHistoryFragment = new OptionHistoryFragment();

        fragments.add(tradeHistoryFragment);
        fragments.add(optionHistoryFragment);
        baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager(),
                fragments, null
        );
        mBinding.viewPager.setAdapter(baseFragmentAdapter);
        mBinding.viewPager.setOffscreenPageLimit(1);
        mBinding.viewPager.setCurrentItem(0);
        ViewPagerScroller scroller = new ViewPagerScroller(mContext);
        scroller.setScrollDuration(0);
        scroller.initViewPagerScroll(mBinding.viewPager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_datecheck:
                initPicker();
                break;
            case R.id.tv_contract_click:
                TYPE = TYPE_CONTRACT;
                mBinding.viewPager.setCurrentItem(0);
                mBinding.cvContract.setVisibility(View.VISIBLE);
                mBinding.cvOption.setVisibility(View.GONE);
                mBinding.tvOption.setTextColor(Pub.getColor(mContext, R.attr.main_color));
                mBinding.tvContract.setTextColor(mContext.getResources().getColor(R.color.font_color_black));
                break;
            case R.id.tv_option_click:
                TYPE = TYPE_OPTION;
                mBinding.viewPager.setCurrentItem(1);
                mBinding.cvContract.setVisibility(View.GONE);
                mBinding.cvOption.setVisibility(View.VISIBLE);
                mBinding.tvOption.setTextColor(mContext.getResources().getColor(R.color.font_color_black));
                mBinding.tvContract.setTextColor(Pub.getColor(mContext, R.attr.main_color));
                break;
            case R.id.ll_back:
                finish();
                break;
        }

    }


    private void initPicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String time = sdf.format(new Date());
        String date = time.split(" ")[0];


        /**
         * 设置年月日
         */
        datePicker = new CustomDatePicker(mContext, mContext.getResources().getString(R.string.timepicker_start), new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(long start_time, long end_time) {
                L.a("starttime = " + start_time + " end-time = " + end_time);
                if (start_time != 0 && end_time != 0) {
                    if (TYPE == TYPE_CONTRACT) {
                        tradeHistoryFragment.setCheckDate(start_time, end_time);
                    } else if (TYPE == TYPE_OPTION) {
                        optionHistoryFragment.setCheckDate(start_time, end_time);
                    }

                }
            }
        }, "2007-01-01 00:00", time);
        datePicker.showSpecificTime(false); //显示时和分
        datePicker.setIsLoop(false);
        datePicker.setDayIsLoop(true);
        datePicker.setMonIsLoop(true);
        datePicker.show(date);

    }

    //viewpager切换不经过中间界面
    public class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 0;             // 滑动速度搜索

        /**
         * 设置速度
         *
         * @param duration
         */
        public void setScrollDuration(int duration) {
            this.mScrollDuration = duration;
        }

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        public void initViewPagerScroll(ViewPager viewPager) {
            try {
                Field mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                mScroller.set(viewPager, this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 默认到option
     */
    private void setToOption() {
        TYPE = TYPE_OPTION;
        mBinding.viewPager.setCurrentItem(1);
        mBinding.cvContract.setVisibility(View.GONE);
        mBinding.cvOption.setVisibility(View.VISIBLE);
        mBinding.tvOption.setTextColor(mContext.getResources().getColor(R.color.font_color_black));
        mBinding.tvContract.setTextColor(Pub.getColor(mContext, R.attr.main_color));
    }
}
