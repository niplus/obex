package com.fota.android.moudles.mine.set;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentSettingBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.MineBean;
import com.fota.android.moudles.mine.bean.VersionBean;
import com.fota.android.service.UpdateIntentService;
import com.fota.android.utils.DeviceUtils;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.LanguageKt;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.widget.dialog.ShareDialog;
import com.fota.android.widget.popwin.CommomDialog;
import com.ndl.lib_common.utils.LiveDataBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by sunchao
 * 设置
 */

public class SettingFragment extends BaseFragment implements View.OnClickListener {
    FragmentSettingBinding fragmentSettingBinding;
    private boolean logOut;
    //    AppDownloadManager appDownloadManager = null;
    CommomDialog updateDialog;
    VersionBean updateVersionBean = null;
    private MineBean.UserSecurity userSecurity;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentSettingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        return fragmentSettingBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        fragmentSettingBinding.setView(this);
        fragmentSettingBinding.btnLogout.setOnClickListener(this);
        if (UserLoginUtil.havaUser()) {
            fragmentSettingBinding.btnLogout.setVisibility(View.VISIBLE);
        } else {
            fragmentSettingBinding.btnLogout.setVisibility(View.GONE);
        }
        fragmentSettingBinding.tvLanguage.setText(LanguageKt.getLanguageString());
        fragmentSettingBinding.tvAboutfota.setOnClickListener(this);
        fragmentSettingBinding.rlLanguage.setOnClickListener(this);
//        fragmentSettingBinding.rlBg.setOnClickListener(this);

        fragmentSettingBinding.tvVersionupdate.setOnClickListener(this);
//        if (AppConfigs.getTheme() == 0) {
//            fragmentSettingBinding.tvBg.setText(R.string.set_bg_black);
//        } else {//白色主题设置黑色状态栏字体
//            fragmentSettingBinding.tvBg.setText(R.string.set_bg_white);
//        }
//        appDownloadManager = new AppDownloadManager(getActivity());

        fragmentSettingBinding.tvSafe.setOnClickListener(this);
        fragmentSettingBinding.tlIdentity.setOnClickListener(this);
        fragmentSettingBinding.tvTradelever.setOnClickListener(this);

        int theme = AppConfigs.getTheme();
        if (theme == 0) {//黑板
            fragmentSettingBinding.cbTheme.setChecked(true);
        } else {
            fragmentSettingBinding.cbTheme.setChecked(false);
        }
        fragmentSettingBinding.cbTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {//选中，变黑板
                    AppConfigs.themeOrLangChanged = true;
                    AppConfigs.setTheme(0);
//                    notify(R.id.event_theme_changed);
                    getHoldingActivity().recreate();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    EventWrapper.post(Event.create(R.id.mine_refreshbar));//通知我的页面刷新状态栏
                    Event event = Event.create(R.id.event_theme_changed);
                    event.putParam(Integer.class, R.id.event_theme_changed);
                    EventWrapper.post(event);
                } else {
                    AppConfigs.themeOrLangChanged = true;
                    AppConfigs.setTheme(1);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getHoldingActivity().recreate();
                    EventWrapper.post(Event.create(R.id.mine_refreshbar));//通知我的页面刷新状态栏
//                    notify(R.id.event_theme_changed);
                    Event event = Event.create(R.id.event_theme_changed);
                    event.putParam(Integer.class, R.id.event_theme_changed);
                    EventWrapper.post(event);
                }
            }
        });
        if (UserLoginUtil.havaUser())
            setIdIcon();
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        userSecurity = (MineBean.UserSecurity) bundle.getSerializable("security");

        LiveDataBus.INSTANCE.getBus("recreate").observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (o.equals("true"))
                    finish();
            }
        });
    }

    @Override
    public String setAppTitle() {
        return getResources().getString(R.string.setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                DialogUtils.showDialog(getContext(), new DialogModel()
                        //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                        .setMessage(getString(R.string.mine_logout))
                        .setSureText(getString(R.string.sure))
                        .setCancelText(getString(R.string.cancel))
                        .setSureClickListen(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logOut();
                                dialogInterface.dismiss();
                            }
                        })
                );

//
                break;
            case R.id.rl_language:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.LanguageFragment);
//                addFragment(new LanguageFragment());
                break;
            case R.id.tv_aboutfota:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.AboutFotaFragment);
                break;
            case R.id.tv_versionupdate:
//                Beta.checkUpgrade();

                checkUpdate();
                break;
            case R.id.tv_safe:
                Bundle bundle = new Bundle();
                bundle.putSerializable("security", userSecurity);
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.SafeSettingFragment, bundle);

                break;
            case R.id.tl_identity:
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
            case R.id.tv_tradelever:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.TradeLeverFragment);
                break;

        }
    }


    /**
     * 退出登录
     */
    public void logOut() {
        if (!UserLoginUtil.havaUser()) {
            return;
        }
        logOut = true;
        fragmentSettingBinding.btnLogout.setVisibility(View.GONE);
        Http.getHttpService().logOut()
                .compose(new CommonTransformer<String>())
                .subscribe(new CommonSubscriber<String>(this) {
                    @Override
                    public void onNext(String outBean) {
                        UserLoginUtil.delUser();
                        ShareDialog.Companion.setInviteCode("");
                        if (getView() == null) return;
                        FtRounts.toQuickLogin(mContext);
                        FotaApplication.setLoginStatus(false);
                        getActivity().finish();
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        UserLoginUtil.delUser();
                        if (getView() == null) return;
                        FotaApplication.setLoginStatus(false);
                        FtRounts.toQuickLogin(mContext);
                        finish();
                    }

                });
    }


    @Override
    public boolean onBackHandled() {
        if (AppConfigs.themeOrLangChanged || logOut) {
            //已经消费themeChanged
            AppConfigs.themeOrLangChanged = false;
            FtRounts.reStartMain(getContext());
            getHoldingActivity().finish();
            return true;
        }
        return super.onBackHandled();
    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_theme_changed:
            case R.id.event_language_changed:
                getHoldingActivity().recreate();
                //recreate();
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
                .subscribe(new CommonSubscriber<VersionBean>(this) {
                    @Override
                    public void onNext(VersionBean versionBean) {
//                        UserLoginUtil.delUser();
                        L.a("version ===   suc " + versionBean.toString());
                        updateVersionBean = versionBean;
                        if (versionBean.isNewest()) {
                            showToast(R.string.update_newest);
                        } else {
                            showUpdateDialog(versionBean);
                        }


                    }

                    @Override
                    protected void onError(ApiException e) {
//                        super.onError(e);
                        stopProgressDialog();
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
//                        appDownloadManager.downloadApk("http://172.16.50.201:8089/mapi/home/download", getString(R.string.app_name), getString(R.string.update_title));
                        if (TextUtils.isEmpty(versionBean.getUrl()))
                            return;
                        beforeUpdateWork(versionBean.getUrl());
//                        beforeUpdateWork("http://192.168.1.176:8084/mapi/home/download");
                    }
                });
        if (versionBean.isCompulsory()) {
            dialogModel.setCancelable(false).setCanCancelOnTouchOutside(false);
        } else {
            dialogModel.setCancelText(getString(R.string.cancel));
        }

        updateDialog = DialogUtils.getDialog(mContext, dialogModel);
        updateDialog.show();
        ((TextView) updateDialog.findViewById(R.id.content)).setGravity(Gravity.CENTER_VERTICAL);
    }


    /**
     * 开始下载
     *
     * @param url
     */
    private void beforeUpdateWork(String url) {
//        if (!isEnableNotification()) {
//            showNotificationAsk();
//            return;
//        }
        toIntentServiceUpdate(url);

    }


    private boolean isEnableNotification() {
        boolean ret = true;
        try {
            NotificationManagerCompat manager = NotificationManagerCompat.from(mContext);
            ret = manager.areNotificationsEnabled();
        } catch (Exception e) {
            return true;
        }
        return ret;
    }

    private void toSetting() {
        try {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
            startActivity(localIntent);
        } catch (Exception e) {

        }
    }

    private void toIntentServiceUpdate(String url) {
        Intent updateIntent = new Intent(mContext, UpdateIntentService.class);
        updateIntent.setAction(UpdateIntentService.ACTION_UPDATE);
        updateIntent.putExtra("appName", "update-1.0.1");
        //随便一个apk的url进行模拟
        updateIntent.putExtra("downUrl", url);
//        updateIntent.putExtra("downUrl", "http://192.168.1.173:8084/home/download");

        mContext.startService(updateIntent);
    }

    /**
     * 设置身份认证图标
     */
    private void setIdIcon() {
        if (userSecurity != null) {
            Drawable drawableRight = getResources().getDrawable(
                    Pub.getThemeResource(mContext, R.attr.icon_right));
            if (userSecurity.getCardCheckStatus() == 0) {
                Drawable drawableleft = getResources().getDrawable(
                        R.mipmap.icon_id_uncheck);
                fragmentSettingBinding.tvIdentity.setText(R.string.safesetting_goauth);
                fragmentSettingBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(drawableleft, null, drawableRight, null);

            } else if (userSecurity.getCardCheckStatus() == 1 || userSecurity.getCardCheckStatus() == 4) {
                Drawable drawableleft = getResources().getDrawable(
                        R.mipmap.icon_id_uncheck);
                fragmentSettingBinding.tvIdentity.setText(R.string.safesetting_ident_shenhezhong);
                fragmentSettingBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(drawableleft, null, drawableRight, null);
            } else if (userSecurity.getCardCheckStatus() == 2) {
                Drawable drawableleft = getResources().getDrawable(
                        R.mipmap.safe_icon_setted);
                fragmentSettingBinding.tvIdentity.setText(R.string.safesetting_ident_over);
                fragmentSettingBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(drawableleft, null, null, null);
            } else if (userSecurity.getCardCheckStatus() == 3) {
                Drawable drawableleft = getResources().getDrawable(
                        R.mipmap.icon_id_uncheck);
                fragmentSettingBinding.tvIdentity.setText(R.string.safesetting_ident_fail);
                fragmentSettingBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(drawableleft, null, drawableRight, null);

            } else {
//                fragmentSettingBinding.tvIdentity.setText(R.string.safesetting_ident_shenhezhong);
//                fragmentSettingBinding.tvIdentity.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserLoginUtil.havaUser()) {
            fragmentSettingBinding.btnLogout.setVisibility(View.VISIBLE);
        } else {
            fragmentSettingBinding.btnLogout.setVisibility(View.GONE);
        }
        if (!UserLoginUtil.havaUser())
            return;
        getMindeMsg();
    }

    /**
     * 获取我的数据
     */
    public void getMindeMsg() {
        Http.getHttpService().getMineData()
                .compose(new CommonTransformer<MineBean>())
                .subscribe(new CommonSubscriber<MineBean>(this) {
                    @Override
                    public void onNext(MineBean mineBean) {
                        if (getView() == null) {
                            return;
                        }

                        if (mineBean != null && mineBean.getUserSecurity() != null) {
                            userSecurity = mineBean.getUserSecurity();
                            setIdIcon();
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                    }

                    @Override
                    protected boolean showLoading() {
                        return false;
                    }

                });
    }


}
