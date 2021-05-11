package com.fota.android.moudles.mine.tradehistory;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BaseFragmentAdapter;
import com.fota.android.core.base.MvpFragment;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.databinding.FragmentTradehistoryBinding;
import com.fota.android.widget.bubblepopup.BubblePopupWindow;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 交易记录
 */
public class TradeHistoryFragment extends MvpFragment<TradeHistoryPresenter> implements View.OnClickListener {
    protected List<Fragment> fragments; //0 现货成交   1.现货委托  2.合约成交   3.合约委托
    FragmentTradehistoryBinding mBinding;
    BaseFragmentAdapter baseFragmentAdapter;

    private BubblePopupWindow popupTypeWindow;
    private BubblePopupWindow popupNameWindow;

    XianhuoWeituoFragment xianhuoWeituoFragment;
    XianhuoChengjiaoFragment xianhuoChengjiaoFragment;
    ContractWeituoFragment contractWeituoFragment;
    ContractChengjiaoFragment contractChengjiaoFragment;
    ConditionHistoryFragment conditionHistoryFragment;
    public static int TYPE_USDT = 2;
    public static int TYPE_HEYUE = 1;

    public static int TYPE_WEITUO = 1;
    public static int TYPE_CHENGJIAO = 2;
    public static int TYPE_CONDITION = 3;
    int TYPE = TYPE_CHENGJIAO;
    int TYPE_NAME = TYPE_USDT;

    @Override
    protected void setTitle(String title) {
        super.setTitle(title);
    }

    @Override
    protected String setAppTitle() {
        return getXmlString(R.string.mine_trade_history);
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


    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_tradehistory,
                null, false);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        initViewPager();
        mBinding.tvNameCheck.setOnClickListener(this);
        mBinding.tvTypeCheck.setOnClickListener(this);

        getPresenter().initList();
    }

    protected void initViewPager() {
        fragments = new ArrayList<>();
        xianhuoChengjiaoFragment = new XianhuoChengjiaoFragment();
        xianhuoWeituoFragment = new XianhuoWeituoFragment();
        contractChengjiaoFragment = new ContractChengjiaoFragment();
        contractWeituoFragment = new ContractWeituoFragment();
        conditionHistoryFragment = new ConditionHistoryFragment();

        fragments.add(xianhuoChengjiaoFragment);
        fragments.add(xianhuoWeituoFragment);
        fragments.add(contractChengjiaoFragment);
        fragments.add(contractWeituoFragment);
        fragments.add(conditionHistoryFragment);
        baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager(),
                fragments, null
        );
        mBinding.viewPager.setAdapter(baseFragmentAdapter);
        mBinding.viewPager.setOffscreenPageLimit(3);
        mBinding.viewPager.setCurrentItem(2);
        ViewPagerScroller scroller = new ViewPagerScroller(mContext);
        scroller.setScrollDuration(0);
        scroller.initViewPagerScroll(mBinding.viewPager);
    }

    /**
     * 设置查询时间
     * @param start_time
     * @param end_time
     */
    public void setCheckDate(long start_time, long end_time){
        if (start_time != 0 && end_time != 0) {
            if (TYPE == TYPE_WEITUO) {
                if (TYPE_NAME == TYPE_HEYUE) {
                    contractWeituoFragment.getPresenter().setTime(start_time, end_time);
                } else if (TYPE_NAME == TYPE_USDT) {
                    xianhuoWeituoFragment.getPresenter().setTime(start_time, end_time);
                }
            } else if (TYPE == TYPE_CHENGJIAO) {
                if (TYPE_NAME == TYPE_HEYUE) {
                    contractChengjiaoFragment.getPresenter().setTime(start_time, end_time);
                } else if (TYPE_NAME == TYPE_USDT) {
                    xianhuoChengjiaoFragment.getPresenter().setTime(start_time, end_time);
                }
            }
        }
    }


    @Override
    protected TradeHistoryPresenter createPresenter() {
        return new TradeHistoryPresenter(this);
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
                    if (model.getValue().equals("1")) {
                        TYPE = TYPE_WEITUO;
//                        mBinding.viewPager.setCurrentItem(0);
//                        weituoFragment.getPresenter().setTypeAndRefresh(TYPE_NAME);
//                        chengjiaoFragment.getPresenter().clear();
                        if (TYPE_NAME == TYPE_HEYUE) {//合约委托
                            mBinding.viewPager.setCurrentItem(3);
                            contractWeituoFragment.getPresenter().refresh();
                        } else if (TYPE_NAME == TYPE_USDT) {//现货委托
                            mBinding.viewPager.setCurrentItem(1);
                            xianhuoWeituoFragment.getPresenter().refresh();
                        }

                    } else if (model.getValue().equals("2")) {
                        TYPE = TYPE_CHENGJIAO;
//                        mBinding.viewPager.setCurrentItem(1);
//                        chengjiaoFragment.getPresenter().setTypeAndRefresh(TYPE_NAME);
//                        weituoFragment.getPresenter().clear();
                        if (TYPE_NAME == TYPE_HEYUE) {//合约成交
                            mBinding.viewPager.setCurrentItem(2);
                            contractChengjiaoFragment.getPresenter().refresh();
                        } else if (TYPE_NAME == TYPE_USDT) {//现货成交
                            mBinding.viewPager.setCurrentItem(0);
                            xianhuoChengjiaoFragment.getPresenter().refresh();
                        }
                    }else if (model.getValue().equals("3")){
                        TYPE = TYPE_CONDITION;
                        mBinding.viewPager.setCurrentItem(4);
                        conditionHistoryFragment.changeType(0);
                    }else if (model.getValue().equals("4")){
                        TYPE = TYPE_CONDITION;
                        mBinding.viewPager.setCurrentItem(4);
                        conditionHistoryFragment.changeType(1);
                    }
                }
            });



//            popupTypeWindow.getAdapter().putList(getPresenter().gettypeList());
            popupTypeWindow.setValue(getPresenter().gettypeList().get(1).getValue());
        }

        if (TYPE_NAME == TYPE_HEYUE){
            popupTypeWindow.getAdapter().putList(getPresenter().gettypeList());
        }else {
            popupTypeWindow.getAdapter().putList(getPresenter().getTypeList1());
        }
//        popupTypeWindow.showAsDropDown(mBinding.tvTypeCheck, (mBinding.tvTypeCheck.getMeasuredWidth() - popupTypeWindow.getSelfWidth()) / 2, 0);
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
                        if (position == 0) {//合约委托
                            TYPE_NAME = TYPE_HEYUE;
                            mBinding.viewPager.setCurrentItem(3);
                            contractWeituoFragment.getPresenter().refresh();
                        } else if (position == 1) {//现货委托
                            TYPE_NAME = TYPE_USDT;
                            mBinding.viewPager.setCurrentItem(1);
                            xianhuoWeituoFragment.getPresenter().refresh();
                        }
//                        weituoFragment.getPresenter().setTypeAndRefresh(TYPE_NAME);

                    } else if (TYPE == TYPE_CHENGJIAO) {
                        if (position == 0) {//合约成交
                            TYPE_NAME = TYPE_HEYUE;
                            mBinding.viewPager.setCurrentItem(2);
                            contractChengjiaoFragment.getPresenter().refresh();
                        } else if (position == 1) {//现货成交
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
//        popupNameWindow.showAsDropDown(mBinding.tvNameCheck, (mBinding.tvNameCheck.getMeasuredWidth() - popupNameWindow.getContentView().getMeasuredWidth()) / 2, 0);
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


}
