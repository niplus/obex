package com.fota.android.moudles.mine.tradehistory;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.ScreenUtils;
import com.fota.android.core.base.BaseFragmentAdapter;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.widget.TitleLayout;
import com.fota.android.widget.bubblepopup.BubblePopupWindow;
import com.fota.android.widget.timepicker.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 现货交易记录
 */
public class SpotHistoryFragment extends TradeHistoryFragment implements TitleLayout.OnRightButtonClickListener {
    protected List<Fragment> fragments; //0 现货成交   1.现货委托

    private BubblePopupWindow popupTypeWindow;
    private BubblePopupWindow popupNameWindow;
    private CustomDatePicker datePicker;

    @Override
    public String setAppTitle() {
        return getResources().getString(R.string.mine_trade_history);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_name_check:
                showNamePopWindow();
                break;
            case R.id.tv_type_check:
                showTypePopWindow();
                break;
        }
    }

    protected void initViewPager() {
        fragments = new ArrayList<>();
        xianhuoChengjiaoFragment = new XianhuoChengjiaoFragment();
        xianhuoWeituoFragment = new XianhuoWeituoFragment();

        fragments.add(xianhuoChengjiaoFragment);
        fragments.add(xianhuoWeituoFragment);
        baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager(),
                fragments, null
        );
        mBinding.viewPager.setAdapter(baseFragmentAdapter);
        mBinding.viewPager.setOffscreenPageLimit(3);
        mBinding.viewPager.setCurrentItem(0);
        ViewPagerScroller scroller = new ViewPagerScroller(mContext);
        scroller.setScrollDuration(0);
        scroller.initViewPagerScroll(mBinding.viewPager);

        //
        if(mTitleLayout != null) {
            if (AppConfigs.getTheme() == 0) {
                mTitleLayout.setRightIcon(R.mipmap.tradehis_time_black);
            } else {
                mTitleLayout.setRightIcon(R.mipmap.tradehis_time_white);
            }
            int px = ScreenUtils.dipToPx(getContext(), 18);
            mTitleLayout.setRightIconSize(px, px);
            mTitleLayout.setOnRightButtonClickListener(this);
        }
    }

    /**
     * 设置查询时间
     * @param start_time
     * @param end_time
     */
    public void setCheckDate(long start_time, long end_time){
        if (start_time != 0 && end_time != 0) {
            if (TYPE == TYPE_WEITUO) {
                xianhuoWeituoFragment.getPresenter().setTime(start_time, end_time);
            } else if (TYPE == TYPE_CHENGJIAO) {
                xianhuoChengjiaoFragment.getPresenter().setTime(start_time, end_time);
            }
        }
    }

    private void showTypePopWindow() {
        if (!Pub.isListExists(getPresenter().gettypeList())) {
            return;
        }
        if (popupTypeWindow == null) {
            popupTypeWindow = new BubblePopupWindow(getContext());
            popupTypeWindow.setOnPopListener(new BubblePopupWindow.OnPopClickListener() {
                @Override
                public void onPopClick(FtKeyValue model, int position) {
                    mBinding.tvTypeCheck.setText(model.getKey());
                    if (position == 0) {
                        TYPE = TYPE_WEITUO;
                        //现货委托
                        mBinding.viewPager.setCurrentItem(1);
                        xianhuoWeituoFragment.getPresenter().refresh();
                    } else if (position == 1) {
                        TYPE = TYPE_CHENGJIAO;
                        //现货成交
                        mBinding.viewPager.setCurrentItem(0);
                        xianhuoChengjiaoFragment.getPresenter().refresh();

                    }
                }
            });
            popupTypeWindow.getAdapter().putList(getPresenter().gettypeList());
            popupTypeWindow.setValue(getPresenter().gettypeList().get(1).getValue());
        }
        popupTypeWindow.show(mBinding.tvTypeCheck);
    }

    private void showNamePopWindow() {
        if (!Pub.isListExists(getPresenter().getnameList())) {
            return;
        }
        if (popupNameWindow == null) {
            popupNameWindow = new BubblePopupWindow(getContext());
            popupNameWindow.setOnPopListener(new BubblePopupWindow.OnPopClickListener() {
                @Override
                public void onPopClick(FtKeyValue model, int position) {
                    mBinding.tvNameCheck.setText(model.getKey());
                    if (TYPE == TYPE_WEITUO) {//0 现货成交   1.现货委托  2.合约成交   3.合约委托
                        if (position == 0) {//现货委托
                            TYPE_NAME = TYPE_USDT;
                            mBinding.viewPager.setCurrentItem(1);
                            xianhuoWeituoFragment.getPresenter().refresh();
                        }
                    } else if (TYPE == TYPE_CHENGJIAO) {
                        if (position == 0) {//现货成交
                            TYPE_NAME = TYPE_USDT;
                            mBinding.viewPager.setCurrentItem(0);
                            xianhuoChengjiaoFragment.getPresenter().refresh();
                        }
                    }
                }
            });
            popupNameWindow.getAdapter().putList(getPresenter().getnameList());
            popupNameWindow.setValue(getPresenter().getnameList().get(0).getValue());
        }
        popupNameWindow.show(mBinding.tvNameCheck);
    }

    @Override
    public void onRightButtonClick(View v) {
        initPicker();
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
                    setCheckDate(start_time, end_time);
                }
            }
        }, "2007-01-01 00:00", time);
        datePicker.showSpecificTime(false); //显示时和分
        datePicker.setIsLoop(false);
        datePicker.setDayIsLoop(true);
        datePicker.setMonIsLoop(true);
        datePicker.show(date);
    }
}
