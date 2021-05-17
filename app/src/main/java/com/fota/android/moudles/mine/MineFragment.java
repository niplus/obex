package com.fota.android.moudles.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.Constants;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.common.listener.ISystembar;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentMineBinding;
import com.fota.android.moudles.mine.bean.MineBean;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.utils.StatusBarUtil;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.udesk.UdeskSDKManager;

/**
 * 我的页面
 */
public class MineFragment extends MvpListFragment<MinePresenter> implements View.OnClickListener, IMineView, ISystembar {
    private FragmentMineBinding mBinding;
    private MineBean.UserSecurity userSecurity;
    private MineBean mineBean;

    private static final int SCROLL_STATUS_TRANS = 1;
    private static final int SCROLL_STATUS_UNTRANS = 2;
    private int SCROLL_STATUS = SCROLL_STATUS_TRANS;
    private int barHeigh = 0;
    private ImmersionBar mImmersionBar;

    @Override
    protected boolean viewGroupFocused() {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_wallet:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.WalletFragment);
                break;
            case R.id.ll_login_click:
                if (!UserLoginUtil.havaUser()) {
                    FtRounts.toQuickLogin(mContext);
                }
                break;
//            case R.id.tv_safe:
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("security", userSecurity);
//                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.SafeSettingFragment, bundle);
//                break;
            case R.id.ll_id:
                if (UserLoginUtil.havaUser()) {
                    Bundle bundle_id = new Bundle();
                    if (userSecurity == null || userSecurity.getCardCheckStatus() == 2)
                        return;
                    bundle_id.putSerializable("cardCheckStatus", userSecurity.getCardCheckStatus());
                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.IdentityFragment, bundle_id);
                } else {
                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.IdentityFragment);
                }
                break;
            case R.id.ll_tradehistory:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.TradeHistoryFragment);
                break;
            case R.id.ll_helpcenter:
//                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.HelpFragment);
                if (AppConfigs.isChinaLanguage()) {
                    FtRounts.toWebView(mContext, mContext.getResources().getString(R.string.mine_help), Constants.URL_HELPCENTER_CH);
                } else {
                    FtRounts.toWebView(mContext, mContext.getResources().getString(R.string.mine_help), Constants.URL_HELPCENTER_EN);
                }
                break;
            case R.id.ll_kefu:
                switch (AppConfigs.getLanguegeInt()) {
                    case AppConfigs.LANGAUGE_SIMPLE_CHINESE:
                        UdeskSDKManager.getInstance().initApiKey(FotaApplication.getInstance(), "fangtulian.udesk.cn",
                                "7ecd639842e69e1d2305398ba6c2b46c", "ebef69550c4ff4e3");
                        break;
                    default:
                    case AppConfigs.LANGAUGE_ENGLISH:
                        UdeskSDKManager.getInstance().initApiKey(FotaApplication.getInstance(), "fangtulian.udesk.cn",
                                "a17303a0b2ebc184fd17a3ac35aae0e9", "1c3766a592967341");
                        break;
                }
                FtRounts.toUdeskService(mContext);
                break;
            case R.id.imv_setting:
                Bundle bundle = new Bundle();
                bundle.putSerializable("security", userSecurity);
                SimpleFragmentActivity.gotoFragmentActivity(getContext(),ConstantsPage.SettingFragment, bundle);

//                startActivity(new Intent(requireContext(), SettingActivity.class));

//                startActivity(new Intent(requireContext(), FuturesCalcActivity.class));
                break;
            case R.id.imv_notice:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.NoticeCenterFragment);
                break; 
            case R.id.ll_activite:
                FtRounts.toWebView(mContext, mContext.getResources().getString(R.string.mine_activitys_title), Constants.getH5BaseUrl() + Constants.URL_ACTIVIES);
                break;
            case R.id.ll_bzjaccount:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.ContractAccountFragment);
                break;
            case R.id.tv_assetsmanager:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.UsdtContractTransferFragment);
                break;
            case R.id.ll_commission:
//                CommissionPopup commissionPopup = new CommissionPopup(getActivity(),new CommissionBean());
//                commissionPopup.setAnimationStyle(R.style.mypopwindow_anim_style);
//                commissionPopup.show();
                //ToastUitl.showShort(getString(R.string.coming_soon));
                //FtRounts.toWebView(mContext, "", Constants.getH5BaseUrl() + Constants.URL_COMMISSION);
                if (!UserLoginUtil.havaUser()) {
                    FtRounts.toQuickLogin(mContext);
                    return;
                }

                String userId = UserLoginUtil.getId();
                if (!userId.equals("0"))
                    FtRounts.toWebView(mContext, "", "https://invite.cboex.com/#/invite?userId=" + userId + "&language="+ MMKV.defaultMMKV().decodeString("language"));
//                BtbMap map = new BtbMap();
//                Http.getWalletService().invite(map)
//                        .compose(new CommonTransformer<String>())
//                        .subscribe(new CommonSubscriber<String>(this) {
//
//                            @Override
//                            public void onNext(String list) {
//                                FtRounts.toWebView(mContext, "", "https://invite.cboex.com/#/invite?userId=" + list);
//                            }
//
//                            @Override
//                            protected void onError(ApiException e) {
//                                //super.onError(e);
//                                FtRounts.toWebView(mContext, "", "https://invite.cboex.com/#/share");
//                            }
//                        });

        }

    }

    /**
     * 跳转到指定Fragment的界面
     *
     * @param fragmentClass
     * @param args
     */
    public void gotoFragmentActivity(String fragmentClass, Bundle args) {
        if (Pub.isStringEmpty(fragmentClass)) {
            return;
        }
        try {
            BaseFragment fragment = (BaseFragment) Class.forName(fragmentClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        KeyBoardUtils.closeKeybord(requireContext());
        if (FotaApplication.containerToobar(fragmentClass) >= 0) {
            FtRounts.toMain(requireContext(), fragmentClass, args);
            return;
        }
        if (UserLoginUtil.havaUser() || ConstantsPage.withoutLoginFragment.contains(fragmentClass)) {
            Intent intent = new Intent(requireContext(), SimpleFragmentActivity.class);
            if (args != null) {
                intent.putExtra(BundleKeys.KEY_FRAGMENT_ARGUMENTS, args);
            }
            intent.putExtra(BundleKeys.KEY_FRAGMENT_CLASS, fragmentClass);
            if (!(requireContext() instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
        } else {
            FtRounts.toQuickLogin(requireContext(), fragmentClass);
        }
    }


    @Override
    protected View setHeadView() {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_mine,
                null, false);
        mBinding.setView(this);
        return mBinding.getRoot();
    }


    @Override
    protected MinePresenter createPresenter() {
        return new MinePresenter(this);
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);

    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        mBinding.llLoginClick.setOnClickListener(this);
        mBinding.imvEye.setOnClickListener(this);
        mBinding.imvNotice.setOnClickListener(this);
        mBinding.llId.setOnClickListener(this);
//        mBinding.tlIdentity.setOnClickListener(this);
//        mBinding.tvTrade.setOnClickListener(this);
//        mBinding.tvTradehistory.setOnClickListener(this);
//        mBinding.tvHelp.setOnClickListener(this);
//        mBinding.tvSetting.setOnClickListener(this);
//        mBinding.imvActive.setOnClickListener(this);
//
//        mBinding.tvKefu.setOnClickListener(this);

        mBinding.imvSetting.setOnClickListener(this);
        mBinding.tvAssetsmanager.setOnClickListener(this);
        mBinding.llWallet.setOnClickListener(this);
        mBinding.llBzjaccount.setOnClickListener(this);
        mBinding.llTradehistory.setOnClickListener(this);
        mBinding.llActivite.setOnClickListener(this);
        mBinding.llHelpcenter.setOnClickListener(this);
        mBinding.llKefu.setOnClickListener(this);
        mBinding.llCommission.setOnClickListener(this);

        if (AppConfigs.getTheme() == 0) {
            mBinding.rlParentHead.setBackgroundColor(getColor(R.color.bg_color_black));
        } else {
            mBinding.rlParentHead.setBackgroundColor(getColor(R.color.main_color_white));
        }


        mBinding.imvEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    UserLoginUtil.setMineHide(true);
                    //如果选中，显示密码
                    if (mineBean == null)
                        return;
                    if (TextUtils.isEmpty(mineBean.getCapitalAmount())) {
                        mBinding.tvWallet.setText(R.string.mine_hengxian);
                    } else {
                        mBinding.tvWallet.setText(mineBean.getCapitalAmount());
                    }
                    if (TextUtils.isEmpty(mineBean.getContractAmount())) {
                        mBinding.tvHeyueusdt.setText(R.string.mine_hengxian);
                    } else {
                        mBinding.tvHeyueusdt.setText(mineBean.getContractAmount());
                    }
                    if (TextUtils.isEmpty(mineBean.getTotalAmount())) {
                        mBinding.tvTotal.setText(R.string.mine_hengxian);
                    } else {
                        mBinding.tvTotal.setText(mineBean.getTotalAmount() + " BTC");
                    }

                    if (TextUtils.isEmpty(mineBean.getTotalValuation())) {
                        mBinding.tvTotalval.setVisibility(View.GONE);
                    } else {
                        mBinding.tvTotalval.setVisibility(View.VISIBLE);
                        mBinding.tvTotalval.setText(" ≈ " + mineBean.getTotalValuation() + " USDT");
                    }

                } else {
                    UserLoginUtil.setMineHide(false);
                    mBinding.tvTotalval.setVisibility(View.GONE);
                    mBinding.tvWallet.setText(R.string.common_hide);
                    mBinding.tvHeyueusdt.setText(R.string.common_hide);
                    mBinding.tvTotal.setText(getString(R.string.common_hide) + " BTC");

                }
            }
        });
        onRefresh();
        barHeigh = StatusBarUtil.getStatusBarHeight(mContext);
        mImmersionBar = ImmersionBar.with(this);
//        initListListenter();  上拉时改变状态栏字体颜色 先去掉
        setBg(Pub.getColor(mContext, R.attr.reverse_bg));
        setJustWhiteBarTxt();
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        if (UserLoginUtil.havaUser()) {
            getPresenter().getMindeMsg(); //获取我的页面显示信息
            if (mBinding == null)
                return;
            if (UserLoginUtil.getMineHide()) {
                mBinding.imvEye.setChecked(true);
            } else {
                mBinding.imvEye.setChecked(false);
            }
            String account = UserLoginUtil.getLoginedAccount();
            if (!TextUtils.isEmpty(account) && account.contains("@")) {
                mBinding.tvUsername.setText(hideEmaile(account));
            } else if (!TextUtils.isEmpty(account)) {
                mBinding.tvUsername.setText(hidePhone(account));
            }
            mBinding.imvEye.setVisibility(View.VISIBLE);
            mBinding.imvNotice.setVisibility(View.VISIBLE);
            mBinding.llId.setVisibility(View.VISIBLE);
        } else {
            unloginDataset();
            doComplete();
        }
    }

    /**
     * 必须要实现的
     * 这里是我们的主adapter  也就是mvplist  会帮你自动实现逻辑的adapter
     */
    protected void initMainAdapter() {
        adapter = new EasyAdapter(getContext(), R.layout.item_tradelever) {
            @Override
            public void convert(RecyclerView.ViewHolder holder, Object model, int position) {

            }
        };
    }

    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_notitle_xml;
    }


    /**
     * 获取我的数据成功
     *
     * @param mineBean
     */
    @Override
    public void mineDataSuccess(MineBean mineBean) {
        if (mBinding.imvEye.isChecked()) {
            if (TextUtils.isEmpty(mineBean.getCapitalAmount())) {
                mBinding.tvWallet.setText(R.string.mine_hengxian);
            } else {
                mBinding.tvWallet.setText(mineBean.getCapitalAmount());
            }
            if (TextUtils.isEmpty(mineBean.getContractAmount())) {
                mBinding.tvHeyueusdt.setText(R.string.mine_hengxian);
            } else {
                mBinding.tvHeyueusdt.setText(mineBean.getContractAmount());
            }
            if (TextUtils.isEmpty(mineBean.getTotalAmount())) {
                mBinding.tvTotal.setText(R.string.mine_hengxian);
            } else {
                mBinding.tvTotal.setText(mineBean.getTotalAmount() + " BTC");
            }
            if (TextUtils.isEmpty(mineBean.getTotalValuation())) {
                mBinding.tvTotalval.setVisibility(View.GONE);
            } else {
                mBinding.tvTotalval.setVisibility(View.VISIBLE);
                mBinding.tvTotalval.setText(" ≈ " + mineBean.getTotalValuation() + " USDT");
            }
        }
//        证件审核状态 0-未审核 1-审核中 2-审核通过 3-审核失败
        userSecurity = mineBean.getUserSecurity();
        this.mineBean = mineBean;
        if (userSecurity != null) {
            if (userSecurity.getCardCheckStatus() == 0) {
                mBinding.tvId.setText(R.string.safesetting_goauth);
                mBinding.imvId.setImageResource(R.mipmap.icon_id_uncheck);
            } else if (userSecurity.getCardCheckStatus() == 1 || userSecurity.getCardCheckStatus() == 4) {
                mBinding.tvId.setText(R.string.safesetting_ident_shenhezhong);
                mBinding.imvId.setImageResource(R.mipmap.icon_id_uncheck);
            } else if (userSecurity.getCardCheckStatus() == 2) {
                mBinding.tvId.setText(R.string.safesetting_ident_over);
                mBinding.imvId.setImageResource(R.mipmap.safe_icon_setted);
            } else if (userSecurity.getCardCheckStatus() == 3) {
                mBinding.tvId.setText(R.string.safesetting_ident_fail);
                mBinding.imvId.setImageResource(R.mipmap.icon_id_uncheck);
            } else {
//                mBinding.tvIdentity.setText(R.string.safesetting_ident_shenhezhong);
//                mBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
            }
        }
        msgactIconset(mineBean);
        doComplete();
        Event event = Event.create(R.id.safe_refresh);
        event.putParam(MineBean.UserSecurity.class, userSecurity);
        EventWrapper.post(event);//通知安全设置页面更新
    }

    private void msgactIconset(MineBean mineBean) {
//        if (mineBean == null)
//            return;
//        if (mineBean.getActivityNum() > 0) {
//            mBinding.imvNotice.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.mine_notice));
//        } else {
//            mBinding.imvNotice.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.mine_notice_none));
//
//        }
//        if (mineBean.getMessageNum() > 0) {
//            mBinding.imvActive.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.mine_active));
//
//        } else {
//            mBinding.imvActive.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.mine_active_none));
//
//        }
        getPresenter().socketSubscribe();
    }

    /**
     * 设置为登陆的显示状态
     */
    private void unloginDataset() {
        if (mBinding == null) {
            return;
        }
        mBinding.imvNotice.setVisibility(View.GONE);
        mBinding.llId.setVisibility(View.GONE);

        mBinding.tvWallet.setText(R.string.mine_hengxian);
        mBinding.tvHeyueusdt.setText(R.string.mine_hengxian);
        mBinding.tvTotal.setText(R.string.mine_hengxian);
        mBinding.tvTotalval.setVisibility(View.GONE);
        mBinding.tvUsername.setText(R.string.mine_login);
        mBinding.imvEye.setVisibility(View.GONE);
        Drawable drawableRight = getResources().getDrawable(
                Pub.getThemeResource(mContext, R.attr.icon_right));
//        mBinding.tvIdentity.setText("");
//        mBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
    }

    @Override
    public void mineDataFail() {
        doComplete();
    }

    @Override
    public void unLogin() {
        unloginDataset();
        UserLoginUtil.delUser();
    }

    @Override
    public void setNoticeView(String haveNew) {
        if ("false".equals(haveNew)) {
            mBinding.imvNotice.setImageResource(R.mipmap.mine_notice);
        } else {
            mBinding.imvNotice.setImageResource(R.mipmap.mine_notice_none);
        }
    }

    private String hidePhone(String str) {
        return StringFormatUtils.getHidePhone(str);
    }

    private String hideEmaile(String str) {
        return StringFormatUtils.getHideEmail(str);
    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.mine_refresh://更新我的信息
                onRefresh();
                if (UserLoginUtil.getMineHide()) {
                    mBinding.imvEye.setChecked(true);
                } else {
                    mBinding.imvEye.setChecked(false);
                }
                break;
            case R.id.mine_refreshbar://更新状态栏颜色
                SCROLL_STATUS = SCROLL_STATUS_TRANS;
                setSystemBar();
                break;
            case R.id.mine_noticedel://新消息已阅读
                mBinding.imvNotice.setImageResource(R.mipmap.mine_notice_none);
                break;
        }
    }

    //    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initListListenter() {

//        mBinding.scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                L.a("i1 ========= "+i1);
//                if (i1 > barHeigh) {
//                    if (SCROLL_STATUS == SCROLL_STATUS_UNTRANS)
//                        return;
//                    SCROLL_STATUS = SCROLL_STATUS_UNTRANS;
//                    if (AppConfigs.getTheme() == 0) {//黑色
//                        mImmersionBar.statusBarDarkFont(false, 0.2f);
//                        mImmersionBar.statusBarColor(R.color.black);
//                    } else {//白色主题设置黑色状态栏字体
//                        mImmersionBar.statusBarDarkFont(true, 0.2f);
//                        mImmersionBar.statusBarColor(R.color.white);
//                    }
//                    mImmersionBar.statusBarAlpha(0f);
//                } else {
//
//                    if (SCROLL_STATUS == SCROLL_STATUS_TRANS)
//                        return;
//                    SCROLL_STATUS = SCROLL_STATUS_TRANS;
//                    if (AppConfigs.getTheme() == 0) {
//                        mImmersionBar.statusBarDarkFont(false, 0.2f);
//                    } else {//白色主题设置黑色状态栏字体
//                        mImmersionBar.statusBarDarkFont(true, 0.2f);
//                    }
//                    mImmersionBar.statusBarColor(android.R.color.transparent);
//                    mImmersionBar.statusBarAlpha(0f);
//
//                }
//            }
//        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean alpha = Math.abs(recyclerView.getChildAt(0).getTop()) > barHeigh;
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisiablePos = layoutManager.findFirstVisibleItemPosition();
                if (firstVisiablePos > 1 || alpha) {
                    if (SCROLL_STATUS == SCROLL_STATUS_UNTRANS)
                        return;
                    SCROLL_STATUS = SCROLL_STATUS_UNTRANS;
                    if (AppConfigs.getTheme() == 0) {//黑色
                        mImmersionBar.statusBarDarkFont(false, 0.2f);
                        mImmersionBar.statusBarColor(R.color.black);
                    } else {//白色主题设置黑色状态栏字体
                        mImmersionBar.statusBarDarkFont(true, 0.2f);
                        mImmersionBar.statusBarColor(R.color.white);
                    }
                    mImmersionBar.statusBarAlpha(0f);
                } else {
                    if (SCROLL_STATUS == SCROLL_STATUS_TRANS)
                        return;
                    SCROLL_STATUS = SCROLL_STATUS_TRANS;
                    if (AppConfigs.getTheme() == 0) {
                        mImmersionBar.statusBarDarkFont(false, 0.2f);
                    } else {//白色主题设置黑色状态栏字体
                        mImmersionBar.statusBarDarkFont(true, 0.2f);
                    }
                    mImmersionBar.statusBarColor(android.R.color.transparent);
                    mImmersionBar.statusBarAlpha(0f);

                }
                mImmersionBar.init();
            }
        });

    }


    @Override
    public void setSystemBar() {
        if (mImmersionBar == null)
            return;
        mImmersionBar.statusBarDarkFont(false, 0.2f);
        mImmersionBar.statusBarColor(android.R.color.transparent);
        mImmersionBar.init();


//        if (AppConfigs.getTheme() == 0) {
////            mImmersionBar.statusfon(true, 0.2f);
//            mImmersionBar.statusBarDarkFont(false, 0.2f);
//        } else {//白色主题设置黑色状态栏字体
//            mImmersionBar.statusBarDarkFont(true, 0.2f);
//        }
//        mImmersionBar.statusBarAlpha(0f);
//        if (SCROLL_STATUS == SCROLL_STATUS_TRANS) {
//            mImmersionBar.statusBarColor(android.R.color.transparent);
//        } else if (SCROLL_STATUS == SCROLL_STATUS_UNTRANS) {
//            if (AppConfigs.getTheme() == 0) {//黑色
//                mImmersionBar.statusBarColor(R.color.black);
//            } else {//白色主题设置黑色状态栏字体
//                mImmersionBar.statusBarColor(R.color.white);
//            }
//        }
//        mImmersionBar.init();
    }
}
