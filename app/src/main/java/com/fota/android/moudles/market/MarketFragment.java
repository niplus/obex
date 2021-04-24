package com.fota.android.moudles.market;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.common.bean.home.BannerBean;
import com.fota.android.common.bean.home.NoticeBean;
import com.fota.android.common.listener.ISystembar;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.URLUtils;
import com.fota.android.core.base.MvpFragment;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.FragmentMarketsBinding;
import com.fota.android.moudles.market.adapter.MarketFragmentAdapter;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class MarketFragment extends MvpFragment<
        MarketPresenter>
        implements MarketViewInterface, ISystembar {
    private FragmentMarketsBinding mBinding;
    private List<String> coinTitles = new ArrayList<>();
    private boolean isCard = true;
    private boolean haveFavor = false;
    //用来标示 是否在有favor的时候跳转到自选页面的boolean
    private boolean needSkipToSelf = false;

    private ImmersionBar mImmersionBar;
    private static final int STATUS_TRANS = 1;
    private static final int STATUS_UNTRANS = 2;
    private int immerseStatus = STATUS_UNTRANS;
    //电池栏的颜色，跟上滑出现的底色一直，黑主题-黑，白主题-蓝
    private int topBlueColor;

    //Fragments
    private List<MarketListFragment> mFragments = new ArrayList<>();
    private MarketFragmentAdapter adapter;

    public static MarketFragment newInstance() {
        Bundle args = new Bundle();
        MarketFragment fragment = new MarketFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_markets, container, false);

        return mBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
//        setBannerHeigh();

        mBinding.llChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCard = !isCard;
                if (isCard)
                    mBinding.imgChange.setImageResource(Pub.getThemeResource(getContext(), R.attr.markets_home_list));
                else
                    mBinding.imgChange.setImageResource(Pub.getThemeResource(getContext(), R.attr.markets_home_card));
                for (MarketListFragment each : mFragments) {
                    each.setCard(isCard);
                }

                marketRefresh(false);
            }
        });

        mImmersionBar = ImmersionBar.with(this);
        setSystemBar();
        onRefresh();
        initTitleAndFragment();
        initAllChildFragment();
//        initCoordinateLayout();

        //获取缓存数据
        initDiskData();
        bannerClick();
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        if (getPresenter().isInit()) {
            startProgressDialog();
        }
        getPresenter().getMarketCard();
        getPresenter().getBanner();
        getPresenter().getNotice();
    }

    private void initHeaderViewPager() {
        if (adapter == null) {
            adapter = new MarketFragmentAdapter(getChildFragmentManager(), mFragments, coinTitles);
            mBinding.allViewPager.setAdapter(adapter);

            mBinding.tabCoinTitle.setupWithViewPager(mBinding.allViewPager);
            mBinding.allViewPager.setOffscreenPageLimit(Pub.getListSize(coinTitles) - 1 <= 0 ? 1 : Pub.getListSize
                    (coinTitles) - 1);

            for (int i = 0;i < 4;i++){
                TabLayout.Tab tab = mBinding.tabCoinTitle.getTabAt(i);
                tab.setCustomView(R.layout.tab_market);
                View tabView = tab.getCustomView();
                tabView.setBackgroundColor(0x00000000);
                ((TextView)tabView.findViewById(R.id.tv_tab)).setText(coinTitles.get(i));
            }
        }
    }

    public void initAllChildFragment() {
        initHeaderViewPager();

        mBinding.allViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MarketListFragment fragment = mFragments.get(position);
                if (fragment != null) {
                    adapter.setCurrent(position);
                    String symbol = "";
                    switch (position) {
                        case 0:
                            symbol = "FAVOR";
                            break;
                        case 1:
                            symbol = "ALL";
                            break;
                        case 2:
                            symbol = "SPOT";
                            break;
                        case 4:
                            symbol = "INDEX";
                            break;
                        default:
                            symbol = coinTitles.get(position);
                            break;
                    }
                    fragment.refreshData(symbol);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initTitleAndFragment() {
        coinTitles = new ArrayList<>();
        mFragments = new ArrayList<>();

        coinTitles.add(getString(R.string.market_card_self));
        coinTitles.add(getString(R.string.common_all));
        coinTitles.add(getString(R.string.common_spot));
        coinTitles.add(getString(R.string.common_future));
//        coinTitles.add(getString(R.string.common_index));
//        coinTitles.add("BTC");
//        coinTitles.add("ETH");
//        coinTitles.add("EOS");
//        coinTitles.add("BCH");
//        coinTitles.add("ETC");
//        coinTitles.add("LTC");
//        coinTitles.add("USDT");
//        mFragments.add(MarketListFragment.newInstance("ALL"));
        for (int i = 0; i < coinTitles.size(); i++) {
            if (i == 0) {
                mFragments.add(MarketFavorListFragment.newInstance());
            } else if (i == 2 || i == 4) {
                mFragments.add(MarketIndexSpotListFragment.newInstance());
            } else
                mFragments.add(MarketListFragment.newInstance());
        }
    }

    @Override
    protected MarketPresenter createPresenter() {
        return new MarketPresenter(this, this);
    }

    @Override
    public void setMarketList(List<FutureItemEntity> list) {
        List markets = FotaApplication.getInstance().getMarketsCardsList();
        markets.clear();
        haveFavor = false;
        for (FutureItemEntity each : list) {
            markets.add(each);
            if (haveFavor) {
                continue;
            }
            if (each.isFavorite()) {
                haveFavor = true;
            }
        }

        for (MarketListFragment each : mFragments) {
            each.setNoDataError(false);
        }
        changeErrorBack(false);

        if (haveFavor) {
            mFragments.get(0).refreshData("FAVOR");
        } else {
            MarketListFragment fragment = mFragments.get(1);
            fragment.refreshData("ALL");

            adapter.setCurrent(1);
            mBinding.allViewPager.setCurrentItem(1);
        }
    }

    private void checkHaveFavor() {
        List<FutureItemEntity> markets = FotaApplication.getInstance().getMarketsCardsList();
        haveFavor = false;
        for (FutureItemEntity each : markets) {
            if (haveFavor) {
                continue;
            }
            if (each.isFavorite()) {
                haveFavor = true;
            }
        }
    }

    @Override
    public void marketRefresh(boolean isSocket) {
        if (adapter != null) {
            for (MarketListFragment each : mFragments) {
                each.setNoDataError(false);
            }
            changeErrorBack(false);
            if (!isSocket) {
                checkHaveFavor();
                if (haveFavor && needSkipToSelf) {
                    adapter.setCurrent(0);
                    mBinding.allViewPager.setCurrentItem(0);
                    needSkipToSelf = false;
                    return;
                }
            }
            int index = adapter.getCurrent();
            if (index == 0) {
                mFragments.get(adapter.getCurrent()).refreshData("FAVOR");
            } else {
                int position = index;
                String symbol = "";
                if (position == 1)
                    symbol = "ALL";
                else if (position == 2)
                    symbol = "SPOT";
                else if (position == 4)
                    symbol = "INDEX";
                else
                    symbol = coinTitles.get(position);
                mFragments.get(position).refreshData(symbol);
            }
        }
    }

    @Override
    public void onCompleteLoading() {
        stopProgressDialog();
    }

    @Override
    public void onErrorDeal(ApiException e) {
        changeErrorBack(true);
        for (MarketListFragment each : mFragments) {
            each.showFailer(e.message, e);
        }
    }

    private void changeErrorBack(boolean hasError) {
//        int recycleBg = hasError ? Pub.getColor(getContext(), R.attr.bg_color) : Pub.getColor(getContext(), R.attr.market_bg);
        int recycleBg = Pub.getColor(getContext(), R.attr.reverse_bg);
        mBinding.allViewPager.setBackgroundColor(recycleBg);
    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_market_search_back:
//                String param = event.getParam(String.class);
//                tabLayout.getTabAt(1).select();
//                int index = coinTitles.indexOf(param);
//                adapter.setCurrent(index);
//                mBinding.allViewPager.setCurrentItem(index);
                break;
            case R.id.event_market_favor_login:
                needSkipToSelf = true;
                getPresenter().setInit(false);
                startProgressDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopProgressDialog();
                    }
                }, 500);
                break;
            case R.id.event_market_favor_add:
                adapter.setCurrent(1);
                mBinding.allViewPager.setCurrentItem(1);
                break;
            case R.id.event_market_reload:
                getPresenter().setInit(false);
                startProgressDialog();
                getPresenter().getMarketCard();
                break;
        }
    }


    @Override
    public void setDoingEntity(List<BannerBean> doingEntity) {
        setBannerList(doingEntity);
    }

    @Override
    public void setNotice(List<NoticeBean> listBeans) {
//        UIUtil.setVisibility(mBinding.layoutHead, (listBeans != null && listBeans.size() > 0));
//        if (listBeans != null) {
//            mBinding.tvActivityContent.getResource(listBeans);
//        }
    }

    /**
     * 设置头部的banner
     *
     * @param bannerList
     */
    private void setBannerList(final List<BannerBean> bannerList) {
        if (!Pub.isListExists(bannerList)) {
            mBinding.bannerImagebg.setVisibility(View.VISIBLE);
        } else {
            mBinding.bannerImagebg.setVisibility(View.GONE);
        }

        setMZBannerData(bannerList);

    }

    /**
     * 设置banner高度
     */
//    private void setBannerHeigh() {
//        int mzbannerHeigh = (UIUtil.getScreenWidth(mContext) - UIUtil.dip2px(mContext, 30)) * 5 / 23;
//        LinearLayout.LayoutParams mzbannerParams = (LinearLayout.LayoutParams) mBinding.bannerLl.getLayoutParams();
//        mzbannerParams.height = mzbannerHeigh;
//        mBinding.bannerLl.setLayoutParams(mzbannerParams);
//    }

    private void setMZBannerData(final List<BannerBean> bannerList) {

        // 代码中更改indicator 的位置
        //mMZBanner.setIndicatorAlign(MZBannerView.IndicatorAlign.LEFT);
        //mMZBanner.setIndicatorPadding(10,0,0,150);
//        mBinding.mzbanner.setPages(bannerList, new MZHolderCreator<BannerViewHolder>() {
//            @Override
//            public BannerViewHolder createViewHolder() {
//                return new BannerViewHolder();
//            }
//        });
//        mBinding.mzbanner.start();

    }


    private class BannerViewHolder implements MZViewHolder<BannerBean> {
        private com.fota.android.widget.btbwidget.CustomRoundAngleImageView mImageView;

        @Override
        public View createView(Context context) {
            // 返回页面布局文件
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
            mImageView = (com.fota.android.widget.btbwidget.CustomRoundAngleImageView) view.findViewById(R.id.banner_image);
            return view;
        }

        @Override
        public void onBind(Context context, int position, BannerBean data) {
            // 数据绑定
//            mImageView.setImageResource(banne);

            RequestOptions options = new RequestOptions().
                    placeholder(AppConfigs.getTheme() == 0 ? R.mipmap.banner_bg_black : R.mipmap.banner_bg_white)
                    .error(AppConfigs.getTheme() == 0 ? R.mipmap.banner_bg_black : R.mipmap.banner_bg_white);

            Glide.with(context).load(URLUtils.getFullPath((String) data.getPictureStoregedUrl())).apply(options).into(mImageView);
        }
    }

    /**
     * 设置初始化数据
     */
    private void initDiskData() {
        getPresenter().getDiskDoing();
    }

    private void bannerClick() {
//        mBinding.mzbanner.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
//            @Override
//            public void onPageClick(View view, Object object) {
//                BannerBean banner = (BannerBean) object;
//                if (banner != null) {
//                    FtRounts.toWebView(mContext, "", banner.getHyperlink());
//                }
//            }
//        });
    }

    @Override
    public void onPause() {
        super.onPause();
//        mBinding.mzbanner.pause();
//        mBinding.tvActivityContent.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mBinding.mzbanner.start();
//        mBinding.tvActivityContent.start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
//            mBinding.mzbanner.pause();
//            mBinding.tvActivityContent.pause();
        } else {
//            mBinding.mzbanner.start();
//            mBinding.tvActivityContent.start();
            setSystemBar();
        }
    }


    @Override
    public void setSystemBar() {
        if (mImmersionBar == null)
            return;
        if (AppConfigs.getTheme() == 0) {//black
            mImmersionBar.statusBarDarkFont(false, 0.2f);
        } else {//白色主题设置黑色状态栏字体
            mImmersionBar.statusBarDarkFont(true, 0.2f);
        }
        mImmersionBar.statusBarAlpha(0f);
        if (immerseStatus == STATUS_TRANS) {
            mImmersionBar.statusBarDarkFont(false, 0.2f);
            mImmersionBar.statusBarColor(android.R.color.transparent);
        } else if (immerseStatus == STATUS_UNTRANS) {
            if (AppConfigs.getTheme() == 0) {//黑色
                topBlueColor = Pub.getThemeResource(getContext(), R.attr.bg_color);
                mImmersionBar.statusBarColor(topBlueColor);
            } else {//白色主题设置黑色状态栏字体
                mImmersionBar.statusBarColor(R.color.white);
            }
        }
        mImmersionBar.init();
    }
}
