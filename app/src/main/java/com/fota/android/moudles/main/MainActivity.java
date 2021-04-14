package com.fota.android.moudles.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.Constants;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.app.IntentExtra;
import com.fota.android.common.bean.exchange.ExchangeCurrency;
import com.fota.android.common.listener.IFuturesUpdateFragment;
import com.fota.android.common.listener.ISystembar;
import com.fota.android.common.listener.IUpdateExchangeFragment;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.base.MyActivityManager;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.FileUtils;
import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;
import com.fota.android.commonlib.utils.TimeUtils;
import com.fota.android.commonlib.utils.ToastUitl;
import com.fota.android.core.base.BaseActivity;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.core.event.Event;
import com.fota.android.http.Http;
import com.fota.android.moudles.common.WebPowerfulFragment;
import com.fota.android.moudles.exchange.index.ExchangeFragment;
import com.fota.android.moudles.futures.FutureContractBean;
import com.fota.android.moudles.main.bean.BundleForTradeEntity;
import com.fota.android.moudles.mine.bean.VersionBean;
import com.fota.android.service.UpdateIntentService;
import com.fota.android.socket.WebSocketClient;
import com.fota.android.utils.DeviceUtils;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.utils.apputils.MenuUtils;
import com.fota.android.widget.popwin.CommomDialog;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.RecyclerViewUtils;
import com.fota.android.widget.recyclerview.ViewHolder;
import com.fota.option.OptionActivity;
import com.fota.option.OptionConfig;
import com.fota.option.OptionManager;
import com.fota.option.ShareMenuItem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tencent.mmkv.MMKV;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {


    RecyclerView recyclerView;
    /**
     * 被选中的tab标签
     */
    private int selected_tab = 0;// 记录选中的tab数值

    private BaseFragment mCurrentFragment;
    private BaseFragment[] mFragments;

    // 需要 记录 行情 传递过来的参数， 在交易和合约fragment会用到
    private BundleForTradeEntity bundleForTrade;
    private EasyAdapter<BottomMenuItem, ViewHolder> adapter;

    CommomDialog updateDialog;
    VersionBean updateVersionBean = null;

    String[] tabName = new String[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabName[0] = getString(R.string.main_tab1);
        tabName[1] = getString(R.string.main_tab2);
        tabName[2] = getString(R.string.main_tab3);
        tabName[3] = getString(R.string.main_tab4);
        setContentView(R.layout.activity_main);
//        Beta.checkUpgrade();
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.setBgResource(R.drawable.toast_bg);
        ToastUtils.setMsgColor(Pub.getColor(getContext(), R.attr.font_color));
        if (getIntent().getExtras() != null) {
            selected_tab = getIntent().getIntExtra("index", 0);//获取被传递过来的标签
        }
        initBottomInfo();
        AppConfigs.isChinaLanguage();
        WebSocketClient client = (WebSocketClient) FotaApplication.getInstance().getClient();
        client.openWebSocket();
        checkToken();
        showNoticeDialog();


    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        Bundle bundle = getIntent().getBundleExtra(BundleKeys.KEY_FRAGMENT_ARGUMENTS);
        if (bundle != null) {
            final String pushUrl = (String) bundle.getString("pushTo");
            L.a("pushUrl = " + pushUrl);
            if (!TextUtils.isEmpty(pushUrl)) {
                FtRounts.getPageFromH5(getContext(), pushUrl);
            }
        }
    }

    private void initBottomInfo() {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new EasyAdapter<BottomMenuItem, ViewHolder>(getContext(), R.layout.item_activity_main_bottom) {
        @Override
        public void convert(ViewHolder holder, final BottomMenuItem model, final int position) {
//                holder.setText(R.id.tv_me, (AppConfigs.isChinaLanguage() ? model.getNameZh() : model.getNameEn()));
            holder.setText(R.id.tv_me, tabName[position]);
            holder.<TextView>getView(R.id.tv_me).setSelected(position == selected_tab);
            holder.setVisible(R.id.image_me, position != selected_tab);
            holder.setVisible(R.id.image_me_select, position == selected_tab);
            if (Pub.isStringEmpty(MenuUtils.getImageString(model, position))) {
                holder.<ImageView>getView(R.id.image_me).setImageResource(MenuUtils.getImage(model, position));
            } else {
                Glide.with(getContext()).load(MenuUtils.getImageString(model, position))
                    .into(holder.<ImageView>getView(R.id.image_me));
            }
            if (Pub.isStringEmpty(MenuUtils.getCheckImageString(model, position))) {
                holder.<ImageView>getView(R.id.image_me_select).setImageResource(MenuUtils.getCheckImage(model, position));
            } else {
                Glide.with(getContext()).load(MenuUtils.getCheckImageString(model, position))
                    .into(holder.<ImageView>getView(R.id.image_me_select));
            }
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Attex 特殊处理
                    if (Constants.BROKER_ID_ATTEX.equals(Constants.BROKER_ID) && String.valueOf(MenuUtils.MENU_OPTION).equals(model.getCode())) {
                        UMShareAPI umShareAPI = UMShareAPI.get(getContext());
                        List<ShareMenuItem> shareList = new ArrayList<>();
                        if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.WEIXIN)) {
                            shareList.add(new ShareMenuItem(R.mipmap.umeng_wechat, getXmlString(R.string.share_wechat)));
                            shareList.add(new ShareMenuItem(R.mipmap.umeng_we_circle, getXmlString(R.string.share_circle)));
                        }
                        if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.SINA)) {
                            shareList.add(new ShareMenuItem(R.mipmap.umeng_sina, getXmlString(R.string.share_sina)));
                        }
                        if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.QQ)) {
                            shareList.add(new ShareMenuItem(R.mipmap.umeng_qq, getXmlString(R.string.share_qq)));
                        }
                        shareList.add(new ShareMenuItem(R.mipmap.umeng_twitter, getXmlString(R.string.share_twitter)));
                        OptionManager.getConfig().setShareMenuList(shareList);
                        OptionManager.getConfig().setShareMenuListener(new OptionConfig.OnClickShareMenuListener() {

                            @Override
                            public void onClickShareMenu(Activity activity, int position, Bitmap bitmap) {
                                SHARE_MEDIA share_media = null;
                                UMImage imageLocal = new UMImage(activity, bitmap);
                                ShareMenuItem item = OptionManager.getConfig().getShareMenuList().get(position);
                                if (getXmlString(R.string.share_wechat).equals(item.getMenuString())) {
                                    share_media = SHARE_MEDIA.WEIXIN;
                                }
                                if (getXmlString(R.string.share_circle).equals(item.getMenuString())) {
                                    share_media = SHARE_MEDIA.WEIXIN_CIRCLE;
                                }
                                if (getXmlString(R.string.share_sina).equals(item.getMenuString())) {
                                    share_media = SHARE_MEDIA.SINA;
                                }
                                if (getXmlString(R.string.share_qq).equals(item.getMenuString())) {
                                    share_media = SHARE_MEDIA.QQ;
                                }
                                if (getXmlString(R.string.share_twitter).equals(item.getMenuString())) {
                                    share_media = SHARE_MEDIA.TWITTER;
                                }
                                new ShareAction(activity).withMedia(imageLocal).setPlatform(share_media).share();
                            }
                        });
                        FtRounts.toNextActivity(getContext(), OptionActivity.class);
                        return;
                    }
                    switchTabHost(model, position);
                }
            });
        }
    };
        recyclerView.setAdapter(adapter);
        String json = SharedPreferencesUtil.getInstance().get(UserLoginUtil.FTKey.HOME_MENU, "");
        if (Pub.isStringEmpty(json)) {
            json = FileUtils.ReadDayDayString(getContext(), "menu.json");
        }
        BottomMenuItemInfo info = new Gson().fromJson(json, BottomMenuItemInfo.class);
        mFragments = new BaseFragment[Pub.getListSize(info.getTabbar())];
        RecyclerViewUtils.initGridRecyclerView(recyclerView, getContext(), Pub.getListSize(info.getTabbar()));
        adapter.putList(info.getTabbar());
        FotaApplication.setTabbar(info.getTabbar());
        switchTabHost(info.getTabbar().get(selected_tab), selected_tab);
        MenuUtils.getAppBar(getContext());
        checkUpdate();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int id = intent.getIntExtra("index", 0);
        if (intent.hasExtra(BundleKeys.KEY_FRAGMENT_ARGUMENTS)) {
            Bundle bundle = intent.getBundleExtra(BundleKeys.KEY_FRAGMENT_ARGUMENTS);
            bundleForTrade = bundle == null ? null
            : (BundleForTradeEntity) bundle.getSerializable("trade");
            boolean toLogin = bundle != null && bundle.getBoolean("toLogin", false);
            if (toLogin) {
                FtRounts.toQuickLogin(getContext());
            }
        }
        selected_tab = id;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter == null || !Pub.isListExists(adapter.getListData())) {
            return;
        }
        switchTabHost(adapter.getItem(selected_tab), selected_tab);
        // first 后续需要清空
        bundleForTrade = null;
    }

    public void switchTabHost(BottomMenuItem model, int position) {
        if (!Pub.isListExists(adapter.getListData())) {
            return;
        }
        if (position >= Pub.getListSize(adapter.getListData())) {
            position = 0;
        }
        selected_tab = position;
        adapter.notifyDataSetChanged();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (mFragments[position] == null) {//new
            try {
                mFragments[position] = (BaseFragment) Class.forName(getMenuFromName(model, position)).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            newExchangeFragment(mFragments[position], model);
        } else {
            updateFragment(mFragments[position]);
        }
        switchFragment(transaction, mFragments[position]);
        setElseFragmentHidden(position);
        transaction.commitAllowingStateLoss();
    }

    private String getMenuFromName(BottomMenuItem model, int position) {
        return Pub.isStringEmpty(ConstantsPage.PAGE_MAP.get(model.getPath())) ? ConstantsPage.WebPowerfulFragment
        : ConstantsPage.PAGE_MAP.get(model.getPath());
    }

    private void setSystemBar() {
        if (AppConfigs.getTheme() == 0) {
            mImmersionBar.statusBarDarkFont(false, 0.2f);
        } else {//白色主题设置黑色状态栏字体
            mImmersionBar.statusBarDarkFont(true, 0.2f);
        }
        mImmersionBar.init();
    }

    private void updateFragment(BaseFragment mFragment) {
        if (mFragment instanceof IUpdateExchangeFragment && bundleForTrade != null
        ) {
            FutureContractBean bundleForFuture = bundleForTrade.getBundleForFuture();
            ExchangeCurrency bundleForExchange = bundleForTrade.getBundleForExchange();
            String coin = bundleForTrade.getFutureCoinName();
            boolean isBuy = bundleForTrade.isBuy();
            if (mFragment instanceof IFuturesUpdateFragment) {
                ((IFuturesUpdateFragment) mFragment).updateInstance(coin, bundleForFuture, isBuy);
            } else {
                ((IUpdateExchangeFragment) mFragment).updateInstance(bundleForExchange, isBuy);
            }
        } else {
            mFragment.onRefresh();
        }

//        if (mFragment instanceof HomeFragment) {//设置状态栏颜色
//            ((HomeFragment) mFragment).setSystemBar();
//        } else {
//            setSystemBar();
//        }
        if (mFragment instanceof ISystembar) {//设置状态栏颜色
            ((ISystembar) mFragment).setSystemBar();
        } else {
            setSystemBar();
        }
    }

    private void newExchangeFragment(BaseFragment mFragment, BottomMenuItem model) {
        if (mFragment instanceof ExchangeFragment
            && bundleForTrade != null
        ) {
            String coin = bundleForTrade.getFutureCoinName();
            boolean isBuy = bundleForTrade.isBuy();
            Bundle args = new Bundle();
            args.putString(BundleKeys.KEY, coin);
            if (bundleForTrade.getBundleForFuture() != null) {
                args.putSerializable(BundleKeys.MODEL, bundleForTrade.getBundleForFuture());
            }
            if (bundleForTrade.getBundleForExchange() != null) {
                args.putSerializable(BundleKeys.MODEL, bundleForTrade.getBundleForExchange());
            }
            args.putBoolean("isBuy", isBuy);
            mFragment.setArguments(args);
        }

        if (mFragment instanceof WebPowerfulFragment
        ) {
            Bundle bundle = new Bundle();
            bundle.putString(IntentExtra.DATA, model.getUrl());
            bundle.putString(IntentExtra.TITLE, (AppConfigs.isChinaLanguage() ? model.getNameZh() : model.getNameEn()));
            mFragment.setArguments(bundle);
        }

        if (mFragment instanceof ISystembar) {//设置状态栏颜色
            ((ISystembar) mFragment).setSystemBar();
        } else {
            setSystemBar();
        }
    }

    private void setElseFragmentHidden(int current) {
        for (int i = 0; i < mFragments.length; i++) {
        BaseFragment tempFragment = mFragments[i];
        if (tempFragment != null && i != current) {
            tempFragment.setSelected(false);
            tempFragment.onHide();
        } else if (tempFragment != null && i == current) {
            tempFragment.setSelected(true);
        }
    }
    }

    private void switchFragment(FragmentTransaction transaction, BaseFragment targetFragment) {
        if (targetFragment == null) {
            return;
        }
        if (!targetFragment.isAdded()) {//如果要显示的targetFragment没有添加过
            if (mCurrentFragment != null) {
                //隐藏当前Fragment
                transaction.hide(mCurrentFragment);
            }
            transaction.add(R.id.content_frame, targetFragment, targetFragment.getClass().getName());//添加targetFragment
        } else {//如果要显示的targetFragment已经添加过
            if (!targetFragment.isVisible()) {
                if (mCurrentFragment != null) {
                    //隐藏当前Fragment
                    transaction.hide(mCurrentFragment);
                }
                //显示targetFragment
                transaction.show(targetFragment);
            } else {
                targetFragment.onHiddenChanged(false);
            }
        }
        mCurrentFragment = targetFragment;
    }

    private long touchTime = 0;

    //系统的返回
    @Override
    public void onBackPressed() {
        KeyBoardUtils.closeKeybord(getContext());
        long currentTime = System.currentTimeMillis();
        if ((currentTime - touchTime) >= 2000) {
            ToastUitl.showShort(getString(R.string.common_again_press_exit_app));
            touchTime = currentTime;
        } else {
            FotaApplication.exit();
        }
    }

    private void checkToken() {
        if (!UserLoginUtil.havaUserToken())
            return;
        BtbMap map = new BtbMap();
        map.put("token", UserLoginUtil.getTokenUnlogin());
        Http.getHttpService().loginTokenCheck(map)
            .compose(new CommonTransformer<Boolean>())
            .subscribe(new CommonSubscriber<Boolean>() {

                @Override
                public void onNext(Boolean valied) {
                    if (!valied) {//无效，清空token数据
                        UserLoginUtil.delUser();
                    }
                }

                @Override
                protected void onError(ApiException e) {
                }
            });

    }


    private void showNoticeDialog() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(FotaApplication.getInstance());
        boolean isOpened = manager.areNotificationsEnabled();
        if (isOpened) {
            SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.NOTICE_TIMES, 0);
            return;
        }
        int times = SharedPreferencesUtil.getInstance().get(SharedPreferencesUtil.Key.NOTICE_TIMES, -1);
        if (times < 15 && times != -1) {
            times++;
            SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.NOTICE_TIMES, times);
            return;
        }
        SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.NOTICE_TIMES, 1);

        DialogUtils.showDialog(this, new DialogModel()
            .setMessage(getString(R.string.trade_notice_msg))
            .setSureText(getString(R.string.sure))
            .setCancelText(getString(R.string.cancel))
            .setSureClickListen(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    toSetting();
                    dialogInterface.dismiss();
                }
            }).setCancelClickListen(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
        );
    }

    private void toSetting() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    /**
     * 收到切换中英文通知后调用服务端,暂时去掉此方案
     */
    private void changeLanguage() {
        String token = UserLoginUtil.getTokenUnlogin();
        if (TextUtils.isEmpty(token))
            return;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        String lang = MMKV.defaultMMKV().decodeString("language", "zh");
        jsonObject.addProperty("currentLanguage", lang);
        Http.getHttpService().changeLanguage(jsonObject)
            .compose(new NothingTransformer<BaseHttpEntity>())
            .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {
                @Override
                public void onNext(BaseHttpEntity object) {
                    L.i("changeLanguage = " + object.toString());
                }

                @Override
                protected void onError(ApiException e) {
                    super.onError(e);
                    L.i("changeLanguage = " + e.toString());

                }
            });
    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_main_changelanguage:
            changeLanguage();
            break;
            case R.id.update_downloaded://下载完成更新提示框状态
//                getHoldingActivity().recreate();
            //recreate();
            if (updateDialog != null && updateDialog.isShowing() && updateVersionBean != null) {
                if (updateVersionBean.isCompulsory()) {//强更
                } else {
                    updateDialog.findViewById(R.id.ll_cancel).setVisibility(View.VISIBLE);
                }
                updateDialog.findViewById(R.id.submit).setEnabled(true);
                ((com.fota.android.widget.btbwidget.FotaButton) updateDialog.findViewById(R.id.submit)).setText(R.string.update_sure);
            }
            break;
            case R.id.login_quicktoast:
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showQuickLoginDialog();
                }
            }, 1000);

            break;
        }
    }

    /**
     * 检测更新
     */
    public void checkUpdate() {
        BtbMap map = new BtbMap();
        map.p("version", DeviceUtils.getVersonName(FotaApplication.getInstance()));
        map.p("platform", 2);
        Http.getHttpService().getVersionUpdate(map)
            .compose(new CommonTransformer<VersionBean>())
            .subscribe(new CommonSubscriber<VersionBean>(FotaApplication.getInstance()) {
                @Override
                public void onNext(VersionBean versionBean) {
//                        UserLoginUtil.delUser();
                    L.a("version ===   suc " + versionBean.toString());
                    updateVersionBean = versionBean;
                    if (versionBean.isNewest()) {
//                            showToast("你已经是最新版本");
                    } else {
                        if (versionBean.isCompulsory()) {//强更
                            showUpdateDialog(versionBean);
                        } else {
                            String version = SharedPreferencesUtil.getInstance().get(SharedPreferencesUtil.Key.UPDATE_VERSION, "");
                            if (!TextUtils.isEmpty(version) && !version.equals(versionBean.getVersion())) {//没有获取过此版本信息，弹出并保存版本信息
                                showUpdateDialog(versionBean);
                                if (!TextUtils.isEmpty(versionBean.getVersion())) {
                                    SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.UPDATE_VERSION, versionBean.getVersion());
                                    SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.UPDATE_TIME, System.currentTimeMillis());
                                }
                            } else {//获取过此版本信息
                                long oldTime = SharedPreferencesUtil.getInstance().get(SharedPreferencesUtil.Key.UPDATE_TIME, Long.valueOf(0));

                                if (TimeUtils.aboveOneday(System.currentTimeMillis(), oldTime)) {//距离上次显示超过1天
                                    showUpdateDialog(versionBean);
                                    SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.UPDATE_TIME, System.currentTimeMillis());//更新显示时间
                                } else {

                                }
                            }

                        }

                    }


                }

                @Override
                protected void onError(ApiException e) {
                    L.a("version ===   fail ");


                }

            });
    }

    /**
     * 更新弹窗
     *
     * @param versionBean
     */
    private void showUpdateDialog(final VersionBean versionBean) {
        DialogModel dialogModel = new DialogModel()
            //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
            .setMessage(versionBean.getText())
            .setSureText(getString(R.string.update_sure))
            .setTitle(getString(R.string.update_title))
            .setClickAutoDismiss(false)
            .setCanCancelOnTouchOutside(false)
            .setCancelClickListen(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            }).setSureClickListen(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateDialog.findViewById(R.id.ll_cancel).setVisibility(View.GONE);
                    updateDialog.findViewById(R.id.submit).setEnabled(false);
                    ((com.fota.android.widget.btbwidget.FotaButton) updateDialog.findViewById(R.id.submit)).setText(R.string.update_downloading);
//                        beforeUpdateWork("http://172.16.50.201:8089/mapi/home/download");
                    if (TextUtils.isEmpty(versionBean.getUrl()))
                        return;
                    beforeUpdateWork(versionBean.getUrl());
                }
            });
        if (versionBean.isCompulsory()) {
            dialogModel.setCancelable(false).setCanCancelOnTouchOutside(false);
        } else {
            dialogModel.setCancelText(getString(R.string.cancel));
        }

        updateDialog = DialogUtils.getDialog(this, dialogModel);
        updateDialog.show();
        ((TextView) updateDialog.findViewById(R.id.content)).setGravity(Gravity.CENTER_VERTICAL);

    }

    private void beforeUpdateWork(String url) {
//        if (!isEnableNotification()) {
//            showNotificationAsk();
//            return;
//        }
        toIntentServiceUpdate(url);

    }

    private void toIntentServiceUpdate(String url) {
        Intent updateIntent = new Intent(this, UpdateIntentService.class);
        updateIntent.setAction(UpdateIntentService.ACTION_UPDATE);
        updateIntent.putExtra("appName", "update-1.0.1");
        //随便一个apk的url进行模拟
        updateIntent.putExtra("downUrl", url);
//        updateIntent.putExtra("downUrl", "http://192.168.1.173:8084/home/download");

        startService(updateIntent);
    }

    private void showQuickLoginDialog() {
        if (!UserLoginUtil.haveQuickLogin())
            DialogUtils.showDialog(MyActivityManager.getInstance().getCurrentActivity(), new DialogModel()
                //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                .setMessage(getString(R.string.quicklogin_guide_msg))
                .setTitle(getString(R.string.quicklogin_guide_title))
                .setSureText(getString(R.string.goto_set_quicklog))
                .setCancelText(getString(R.string.cancel))
                .setSureClickListen(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SimpleFragmentActivity.gotoFragmentActivity(MyActivityManager.getInstance().getCurrentActivity(),
                            ConstantsPage.SafeSettingFragment
                        );
                        dialogInterface.dismiss();
                    }
                }));
    }

}
