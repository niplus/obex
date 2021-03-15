package com.fota.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.fota.android.app.BundleKeys;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.app.IntentExtra;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.ActivityChangeUtil;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.moudles.main.MainActivity;
import com.fota.android.moudles.mine.login.FotaLoginActivity;
import com.fota.android.moudles.welcome.TransformActivity;
import com.fota.android.moudles.welcome.TransformActivity2;
import com.fota.android.utils.apputils.CustomerServiceUtils;

import cn.udesk.UdeskSDKManager;
import cn.udesk.config.UdeskConfig;
import udesk.core.LocalManageUtil;

public class FtRounts extends ActivityChangeUtil {


    /**
     * 设置资金密码成功页面.
     *
     * @param context
     */
    public static void toWithdrawActivity(Context context, WalletItem walletBean) {
        Class cls = null;
        try {
            cls = Class.forName(ConstantsPage.WithdrawActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(context, cls);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKeys.MODEL, walletBean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 设置资金密码成功页面.
     *
     * @param context
     */
    public static void toActivity(Context context, String name) {
        Class cls = null;
        try {
            cls = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (cls == null) {
            return;
        }
        Intent intent = new Intent(context, cls);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 设置资金密码成功页面.
     *
     * @param context
     */
    public static void toUdeskService(Context context) {
        String sdktoken = UserLoginUtil.getTokenUnlogin();
        if (Pub.isStringEmpty(sdktoken)) {
            sdktoken = "--";
        }
        LocalManageUtil.saveSelectLanguage(context, AppConfigs.getLanguege());
        UdeskConfig.Builder builder = CustomerServiceUtils.getUdeskConfigBuild(sdktoken, context);
        UdeskSDKManager.getInstance().entryChat(context, builder.build(), sdktoken);
    }


    /**
     * @param url
     */
    public static void getPageFromH5(Context context, String url) {
        if (url == null) {
            return;
        }
        //跳转到fota页面
        if (isNativeUrl(url)) {
            if (ConstantsPage.PAGE_MAP.containsKey(url)) {
                SimpleFragmentActivity.gotoFragmentActivity(context, ConstantsPage.PAGE_MAP.get(url));
                return;
            }
            if (ConstantsPage.PAGE_ACTIVITY.containsKey(url)) {
                if (ConstantsPage.FotaLoginActivity.equals(ConstantsPage.PAGE_ACTIVITY.get(url))){
                    toQuickLogin(context);
                }else {
                    toActivity(context, ConstantsPage.PAGE_ACTIVITY.get(url));
                }
                return;
            }
            if (url.startsWith("fota://goto/webview?url=")) {// webView是小写的webview 坑
                url = url.substring("fota://goto/webview?url=".length(), url.length());
                toWebView(context, "", url);
                return;
            }
        } else {
            //目前都是跳转webview
            toWebView(context, "", url);
        }
    }

    /**
     * 是原生界面
     *
     * @param url
     * @return
     */
    public static boolean isNativeUrl(String url) {
        return url.startsWith(ConstantsPage.DOMAIN);
    }


    /**
     * @param context
     */
    public static void reStartMain(Context context) {
        Intent intent = new Intent(context, AppConfigs.isWhiteTheme() ? TransformActivity2.class
                : TransformActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        //toMain(context, ConstantsPage.MineFragment);
    }

    public static void toWebView(Context context, String title, String data) {
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtra.DATA, data);
        bundle.putString(IntentExtra.TITLE, title);
        SimpleFragmentActivity.gotoFragmentActivity(context, ConstantsPage.WebPowerfulFragment, bundle);
    }

    public static void toMain(Context context, String fragmentClass, Bundle args, boolean noHistory) {
        int index = FotaApplication.containerToobar(fragmentClass);
        if (index < 0) {
            index = 0;
        }
        Intent intent = new Intent(context, MainActivity.class);
        if (args != null) {
            intent.putExtra(BundleKeys.KEY_FRAGMENT_ARGUMENTS, args);
        }
        intent.putExtra("index", index);
        if (noHistory)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void toMain(Context context, String fragmentClass, Bundle args) {
        toMain(context, fragmentClass, args, true);
    }

    /**
     * 跳转到手势指纹资金密码验证页
     * 返回true代表设置了指纹或资金密码
     *
     * @param context
     */
    public static boolean toQuickCapital(Context context, String page) {
        if (TextUtils.isEmpty(UserLoginUtil.getCapital())) {
            return false;
        }
        if (TextUtils.isEmpty(UserLoginUtil.getLoginedGesture()) && !UserLoginUtil.getFingerOpen()) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("page", page);
        SimpleFragmentActivity.gotoFragmentActivity(context, ConstantsPage.QuickCheckCapitalFragment, bundle);
        return true;
    }

    /**
     * 返回true代表设置了指纹或资金密码
     *
     * @param context
     */
    public static boolean isQuickCapitalNoJumper(Context context) {
        if (TextUtils.isEmpty(UserLoginUtil.getCapital())) {
            return false;
        }
        if (TextUtils.isEmpty(UserLoginUtil.getLoginedGesture()) && !UserLoginUtil.getFingerOpen()) {
            return false;
        }
        return true;
    }

    /**
     * 跳转到手势指纹登录页
     *
     * @param context
     */
    public static void toQuickLogin(Context context) {
        if (!UserLoginUtil.havaUserToken()) {
            toLogin(context);
            return;
        }
        if (TextUtils.isEmpty(UserLoginUtil.getLoginedGesture()) && !UserLoginUtil.getFingerOpen()) {
            toLogin(context);
            return;
        }
        SimpleFragmentActivity.gotoFragmentActivity(context, ConstantsPage.QuickLoginFragment);
    }


    /**
     * 跳转到手势指纹登录页
     *
     * @param context
     */
    public static void toQuickLogin(Context context, String key) {
        if (!UserLoginUtil.havaUserToken()) {
            FtRounts.toLogin(context, key);
            return;
        }
        if (TextUtils.isEmpty(UserLoginUtil.getLoginedGesture()) && !UserLoginUtil.getFingerOpen()) {
            FtRounts.toLogin(context, key);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.LOGIN_JUMPKEY, key);
        SimpleFragmentActivity.gotoFragmentActivity(context, ConstantsPage.QuickLoginFragment, bundle);
    }

    /**
     * 跳转到登录页
     *
     * @param context
     */
    public static void toLogin(Context context, String key) {
        Intent intent = new Intent(context, FotaLoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.LOGIN_JUMPKEY, key);
        intent.putExtras(bundle);

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 跳转到登录页
     *
     * @param context
     */
    public static void toLogin(Context context) {
        Intent intent = new Intent(context, FotaLoginActivity.class);
        context.startActivity(intent);
    }
}
