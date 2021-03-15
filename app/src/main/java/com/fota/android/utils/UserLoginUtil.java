package com.fota.android.utils;

import android.content.Context;
import android.text.TextUtils;

import com.fota.android.http.Http;
import com.fota.android.app.FotaApplication;
import com.fota.android.app.SocketKey;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;
import com.fota.android.moudles.mine.login.bean.LoginBean;
import com.fota.android.socket.WebSocketClient;
import com.fota.option.OptionManager;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * 登录信息工具类
 */
public class UserLoginUtil {

    public static void saveUser(LoginBean loginBean) {
        if (loginBean == null)
            return;
        AppConfigs.notifyDataChanged();
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_TOKEN, loginBean.getToken());
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_ID, loginBean.getId() + "");
        if (loginBean.getId() <= 0)
            return;
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_PHONE + loginBean.getId(), loginBean.getPhone());
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_EMAIL + loginBean.getId(), loginBean.getEmail());
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_REGISTTYPE + loginBean.getId(), loginBean.getRegisterType());
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_PHONECOUNTRYCODE + loginBean.getId(), loginBean.getPhoneCountryCode());
//        JPushInterface.setAlias(FotaApplication.getInstance(), StringFormatUtils.getIntId(loginBean.getId() + ""), loginBean.getId() + "");
//        setJpushTag();
    }

    /**
     * 删除登录用户
     */
    public static void delUser() {
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_TOKEN, "");
        OptionManager.logOut();
        if (TextUtils.isEmpty(getId())) {
            return;
        }
        JPushInterface.deleteAlias(FotaApplication.getInstance(), StringFormatUtils.getIntId(getId()));
//        delJpushTag();
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_PHONE + getId(), "");
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_EMAIL + getId(), "");
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_REGISTTYPE + getId(), -1);
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_PHONECOUNTRYCODE + getId(), "");
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_ID, "");

        FotaApplication.getInstance().getClient().removeChannel(SocketKey.LOG_OUT, null);//socket通知服务端退出登录
    }

    /**
     * 是否有用户登录
     *
     * @return
     */
    public static boolean havaUser() {
        String token = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_TOKEN, "");
        if (!TextUtils.isEmpty(token) && FotaApplication.getLoginSrtatus()) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 是否有用户登录
     *
     * @return
     */
    public static void checkLogin(Context context) {
        KeyBoardUtils.closeKeybord(context);
        if (!UserLoginUtil.havaUser()) {
            FtRounts.toQuickLogin(context);
        }
    }


    /**
     * 是否有用户token信息保存
     *
     * @return
     */
    public static boolean havaUserToken() {
        String token = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_TOKEN, "");
        if (!TextUtils.isEmpty(token)) {
            return true;
        } else {
            return false;
        }

    }

    public static String getToken() {
        String token = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_TOKEN, "");
        if (FotaApplication.getLoginSrtatus()) {
            token = token;
        } else {
            token = "";
        }
        return token;

    }

    /**
     * 验证token时获取token
     *
     * @return
     */
    public static String getTokenUnlogin() {
        String token = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_TOKEN, "");
        return token;

    }


    public static String getPhone() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return "";
        }
        String phone = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_PHONE + getId(), "");
        return phone;

    }

    public static String getEmail() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return "";
        }
        String email = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_EMAIL + getId(), "");
        return email;

    }

    public static int getRegistType() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return -1;
        }
        int registType = (int) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_REGISTTYPE + getId(), -1);
        return registType;

    }

    public static String getPhoneCountryCode() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return "";
        }
        String code = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_PHONECOUNTRYCODE + getId(), "");
        return code;

    }

    public static void setPhone(String str) {
        if (TextUtils.isEmpty(str))
            return;
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return;
        }
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_PHONE + getId(), str);
    }

    public static void setEmail(String str) {
        if (TextUtils.isEmpty(str))
            return;
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return;
        }
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_EMAIL + getId(), str);
    }

    /*
     * 返回0代表没有用户id
     */
    public static String getId() {
        String id = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_ID, "");
        return id;

    }


    public static class FTKey {

        //登录成功后的token
        public final static String LOGIN_TOKEN = "login_token_ft";
        public final static String LOGIN_ID = "login_id_ft";
        public final static String LOGIN_PHONE = "login_phone_ft";
        public static final String LOGIN_EMAIL = "login_email_ft";
        public final static String LOGIN_REGISTTYPE = "login_registtype";
        public final static String LOGIN_PHONECOUNTRYCODE = "login_phonecountrycode";

        public static final String LOGIN_ACCOUNT = "login_account";
        public static final String LOGIN_GESTURE = "login_gesture";
        public static final String LOGIN_FINGER = "login_finger";
        public static final String LOGIN_CAPITAL = "login_capital";

        public static final String HOME_MENU = "home_menu";

        public static final String HOME_HIDE = "home_hide";
        public static final String MINE_HIDE = "mine_hide";

        public static final String OPTION_TIP = "option_tip";

    }

    /**
     * 保存登录过的账号
     *
     * @param account
     */
    public static void saveLoginedAccount(String account) {
        if (TextUtils.isEmpty(account))
            return;
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_ACCOUNT, account);
    }


    /**
     * 获取登录过的账号
     *
     * @param
     */
    public static String getLoginedAccount() {
        String account = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_ACCOUNT, "");
        return account;
    }

    /**
     * 获取登录手势验证码
     *
     * @param
     */
    public static String getLoginedGesture() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return "";
        }
        String gesture = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_GESTURE + UserLoginUtil.getId(), "");
        return gesture;
    }

    /**
     * 保存登录手势验证码
     *
     * @param gesture
     */
    public static void saveLoginedGesture(String gesture) {
        if (TextUtils.isEmpty(gesture))
            return;
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return;
        }
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_GESTURE + getId(), gesture);
    }

    /**
     * 清空手势密码
     */
    public static void clearLoginedGesture() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return;
        }

        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_GESTURE + getId(), "");
    }

    /**
     * 设置指纹验证码开启状态
     */
    public static void setFingerOpen(boolean open) {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return;
        }
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_FINGER + getId(), open);
    }

    /**
     * 获取指纹验证码开启状态
     *
     * @param
     */
    public static boolean getFingerOpen() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return false;
        }
        boolean finger = SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_FINGER + getId(), false);
        return finger;
    }

    /**
     * 保存资金密码
     *
     * @param capital
     */
    public static void saveCapital(String capital) {
        if (TextUtils.isEmpty(capital))
            return;
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return;
        }
        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_CAPITAL + getId(), capital);
    }

    /**
     * 清空资金密码
     */
    public static void clearCapital() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return;
        }

        SharedPreferencesUtil.getInstance().put(FTKey.LOGIN_CAPITAL + getId(), "");
    }

    /**
     * 获取登录手势验证码
     *
     * @param
     */
    public static String getCapital() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return "";
        }
        String gesture = (String) SharedPreferencesUtil.getInstance().get(FTKey.LOGIN_CAPITAL + getId(), "");
        return gesture;
    }

    public static void setJpushTag() {
        if (TextUtils.isEmpty(getId()))
            return;
        Set<String> set = new HashSet<>();
        if (AppConfigs.isChinaLanguage()) {
            set.add("zh");
        } else {
            set.add("en");
        }
        JPushInterface.setTags(FotaApplication.getInstance(), StringFormatUtils.getIntId(getId() + ""), set);
    }

    public static void delJpushTag() {
        if (TextUtils.isEmpty(getId()))
            return;
        JPushInterface.cleanTags(FotaApplication.getInstance(), StringFormatUtils.getIntId(getId() + ""));
    }

    /**
     * 设置首页眼睛按钮状态
     *
     * @param show
     */
    public static void setHomeHide(boolean show) {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return;
        }
        SharedPreferencesUtil.getInstance().put(FTKey.HOME_HIDE + getId(), show);
    }

    /**
     * 设置首页眼睛按钮状态
     *
     * @param show
     */
    public static void setOptionTip(boolean show) {
        SharedPreferencesUtil.getInstance().put(FTKey.OPTION_TIP, show);
    }

    /**
     * 获取首页眼睛按钮状态
     *
     * @param
     */
    public static boolean getOptionTip() {
        boolean show = (boolean) SharedPreferencesUtil.getInstance().get(FTKey.OPTION_TIP, false);
        return show;
    }


    /**
     * 获取首页眼睛按钮状态
     *
     * @param
     */
    public static boolean getHomeHide() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return true;
        }
        boolean show = (boolean) SharedPreferencesUtil.getInstance().get(FTKey.HOME_HIDE + getId(), true);
        return show;
    }

    /**
     * 设置我的眼睛按钮状态
     *
     * @param show
     */
    public static void setMineHide(boolean show) {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return;
        }
        SharedPreferencesUtil.getInstance().put(FTKey.MINE_HIDE + getId(), show);
    }

    /**
     * 获取我的眼睛按钮状态
     *
     * @param
     */
    public static boolean getMineHide() {
        if (TextUtils.isEmpty(UserLoginUtil.getId())) {
            return true;
        }
        boolean show = (boolean) SharedPreferencesUtil.getInstance().get(FTKey.MINE_HIDE + getId(), true);
        return show;
    }


    /**
     * Ip设置变更
     *
     * @param
     */
    public static void ipChanged(Context context) {
        Http.clearService();
        WebSocketClient client = (WebSocketClient) FotaApplication.getInstance().getClient();
        client.clearWebSocket(true);
        UserLoginUtil.delUser();
        FtRounts.reStartMain(context);
    }

    /**
     * 是否设置过快速登录
     *
     * @return
     */
    public static boolean haveQuickLogin() {
        if (TextUtils.isEmpty(getLoginedGesture()) && !getFingerOpen()) {
            return false;
        } else {
            return true;
        }
    }
}
