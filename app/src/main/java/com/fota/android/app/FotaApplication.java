package com.fota.android.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.Utils;
import com.fota.android.R;
import com.fota.android.common.bean.home.DepthBean;
import com.fota.android.commonlib.app.AppVariables;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.base.BaseApplication;
import com.fota.android.commonlib.utils.ErrorCodeUtil;
import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.moudles.main.BottomMenuItem;
import com.fota.android.moudles.market.bean.ChartLineEntity;
import com.fota.android.moudles.market.bean.FutureItemEntity;
import com.fota.android.moudles.market.bean.HoldingEntity;
import com.fota.android.moudles.market.bean.MarketLineBean;
import com.fota.android.moudles.market.bean.MarketTimeLineBean;
import com.fota.android.receiver.NetScreenReceiver;
import com.fota.android.socket.IWebSocketSubject;
import com.fota.android.socket.WebSocketClient;
import com.fota.android.socket.WebSocketEntity;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.utils.apputils.DiffTimeUtils;
import com.fota.option.OptionConfig;
import com.fota.option.OptionManager;
import com.fota.option.websocket.data.AccountInfo;
import com.ndl.lib_common.log.NLog;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;
import com.tencent.mmkv.MMKV;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by Administrator on 2018/3/27.
 */

public class FotaApplication extends BaseApplication {

    IWebSocketSubject client;

    static List<BottomMenuItem> tabbar;

    public static void setTabbar(List<BottomMenuItem> tabbar) {
        FotaApplication.tabbar = tabbar;
    }

    public IWebSocketSubject getClient() {
        return client;
    }

    private static FotaApplication applicationInstance;

    public static FotaApplication getInstance() {
        return applicationInstance;
    }

    private static boolean islogin = false;//用户是否登录的标志，程序初始都是未登录
    public static String registrationId = "";//极光设备id


    @Override
    public void onCreate() {
        super.onCreate();
        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                builder.detectFileUriExposure();
            }
        }
        applicationInstance = this;
        SharedPreferencesUtil.init(this);
        Utils.init(this);
        //初始化友盟统计
        //初始化友盟统计
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        setBugyInfo();
        //Bugly.init(getApplicationContext(), Constants.BUGLY_ID, Constants.DEBUG);

        initWebSocket();
        setLoginStatus(false);
//        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        registrationId = JPushInterface.getRegistrationID(this);

        initialServiceGet();
        initNetAndScreenReceiver();
        OptionConfig config = getOptionConfig();
        OptionManager.setBrokerId(Constants.BROKER_ID);
        //OptionManager.init("appSecret", config, this);
        OptionManager.setConfig(config);
        OptionManager.setApplication(this);
        AppVariables.put("option-socket", "ws-client");

        MMKV.initialize(this);
//        switchLanguage();
        ErrorCodeUtil.getInstance().setOtherAppCodeUtils(FotaErrorUtils.getInstance());

        NLog.INSTANCE.initLog(this);


    }

    private void initNetAndScreenReceiver() {
        //在代码中实现动态注册的方式
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(new NetScreenReceiver(), filter);
    }

    private void setBugyInfo() {
        setStrictMode();
        // 设置是否开启热更新能力，默认为true
        Beta.enableHotfix = true;
        // 设置是否自动下载补丁
        Beta.canAutoDownloadPatch = true;
        // 设置是否提示用户重启
        Beta.canNotifyUserRestart = true;
        // 设置是否自动合成补丁
        Beta.canAutoPatch = true;
        /**
         *  全量升级状态回调
         */
        Beta.upgradeStateListener = new UpgradeStateListener() {
            @Override
            public void onUpgradeFailed(boolean b) {

            }

            @Override
            public void onUpgradeSuccess(boolean b) {

            }

            @Override
            public void onUpgradeNoVersion(boolean b) {
                //Toast.makeText(getApplicationContext(), "最新版本", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpgrading(boolean b) {
                //Toast.makeText(getApplicationContext(), "onUpgrading", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadCompleted(boolean b) {

            }
        };

        long start = System.currentTimeMillis();
        Bugly.setUserId(this, UserLoginUtil.getTokenUnlogin());
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId,调试时将第三个参数设置为true
        Bugly.init(this, Constants.BUGLY_ID, Constants.DEBUG);
        long end = System.currentTimeMillis();
        Log.e("init time--->", end - start + "ms");
    }

    @TargetApi(9)
    protected void setStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
    }

    private void initialServiceGet() {
        DiffTimeUtils.getTime();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
//        MultiDex.install(base);

        // 安装tinker
        Beta.installTinker();
    }

    /**
     * 设置登录状态
     *
     * @param loginStatus
     */
    public static void setLoginStatus(boolean loginStatus) {
        islogin = loginStatus;
        OptionManager.token = UserLoginUtil.getToken();
        OptionManager.userId = UserLoginUtil.getId();
        if (loginStatus) {//socket通知服务端 登录
            WebSocketEntity entity = new WebSocketEntity();
            entity.setReqType(SocketKey.LOG_OUT);
            entity.setToken(UserLoginUtil.getToken());
            getInstance().getClient().addChannel(entity, null);

            //jiang 1213
            EventWrapper.post(Event.create(R.id.event_market_favor_login));
            //极光tag和别名在登录和快速登录都注册一下
            if (!TextUtils.isEmpty(UserLoginUtil.getId()))
                JPushInterface.setAlias(FotaApplication.getInstance(), StringFormatUtils.getIntId(UserLoginUtil.getId()), UserLoginUtil.getId());
            UserLoginUtil.setJpushTag();
        }
    }

    public static boolean getLoginSrtatus() {
        return islogin;
    }

    private void initWebSocket() {
        client = new WebSocketClient();
    }

    /**
     * 页签分时图数据
     * key -- 为type-id，跟depth一样的key
     * 但是 type不同，type： 1 spot；2 future；3 usdk；(废弃)
     * <p>
     * 以此key为准
     * key 改为-- btc/usdk btc指数 btc0203...
     * <p>
     * value -- timeline data
     */
    private final List<FutureItemEntity> marketsCardsList = new ArrayList<>();

    /**
     * 放大 分时图 数据
     * key -- btc btc/udsk btc0203...
     * 策略变更 只保留3个
     * 某时刻只保留对应当前激活type-id参数的
     * 1 spot 2 future 3 usdt
     * spot 与 future 同期刷新，同时清空
     * 3usdt 独立
     */
    final Map<String, List<ChartLineEntity>> marketsTimesMap = new HashMap<>();

    /**
     * 放大 K线 数据
     * 策略变更 只保留3个
     * * 某时刻只保留对应当前激活type-id参数的
     * 2 future 3 usdt 有1spot的数据
     * spot 与 future 同期刷新，同时清空
     * 3usdt 独立
     */
    final Map<String, List<ChartLineEntity>> marketsKlinesMap = new HashMap<>();

    /**
     * type-id 作为key
     * type： 3 usdk 2 future
     * id - id
     */
    final Map<String, DepthBean> depthMap = new HashMap<>();
    //如果用户登录，持有合约，此处就是展示页面的holding信息

    public HoldingEntity getHoldingEntity() {
        return holdingEntity;
    }

    public void setHoldingEntity(HoldingEntity holdingEntity) {

        this.holdingEntity = holdingEntity;
    }

    HoldingEntity holdingEntity = new HoldingEntity(-1, "");

    public List<FutureItemEntity> getMarketsCardsList() {
        return marketsCardsList;
    }

    public Map<String, List<ChartLineEntity>> getMarketsTimesMap() {
        return marketsTimesMap;
    }

    public Map<String, List<ChartLineEntity>> getMarketsKlinesMap() {
        return marketsKlinesMap;
    }

    public void putMarketsTimes(String key, List<ChartLineEntity> entities) {
        marketsTimesMap.put(key, entities);
    }

    public void putMarketsKlines(String key, List<ChartLineEntity> entities) {
        marketsKlinesMap.put(key, entities);
    }

    public List<ChartLineEntity> getListFromTimesByType(int key) {
        String keyStr = key + "";
        return marketsTimesMap.get(keyStr);
    }

    public List<ChartLineEntity> getListFromKlinesByType(int key) {
        String keyStr = key + "";
        return marketsKlinesMap.get(keyStr);
    }

    public Map<String, DepthBean> getDepthMap() {
        return depthMap;
    }

    public static void exit() {
        MobclickAgent.onKillProcess(getInstance().getApplicationContext());
        System.exit(0);
    }

    public void resetAppKLineData(int type, List<ChartLineEntity> trades, List<ChartLineEntity> indexs) {
        Map<String, List<ChartLineEntity>> caches = getMarketsKlinesMap();

        String key = type + "";
        if (type == 3) {// usdt
            if (trades != null) {
                caches.put(key, trades);
            } else {
                if (caches.get(key) != null)
                    caches.get(key).clear();
            }
        }

        if (type == 2) {
            if (trades != null) {
                caches.put(key, trades);
            } else {
                if (caches.get(key) != null)
                    caches.get(key).clear();
            }
            key = "1";
            if (indexs != null) {
                caches.put(key, indexs);
            } else {
                if (caches.get(key) != null)
                    caches.get(key).clear();
            }
        }
    }

    public void resetAppTimeLineData(int type, List<ChartLineEntity> trades, List<ChartLineEntity> indexs) {
        Map<String, List<ChartLineEntity>> caches = getMarketsTimesMap();

        String key = type + "";
        if (type == 3) {// usdt
            if (trades != null) {
                caches.put(key, trades);
            } else {
                if (caches.get(key) != null)
                    caches.get(key).clear();
            }
        }
        if (type == 1) {
            if (indexs != null) {
                caches.put(key, indexs);
            } else {
                if (caches.get(key) != null)
                    caches.get(key).clear();
            }
        }

        if (type == 2) {
            if (trades != null) {
                caches.put(key, trades);
            } else {
                if (caches.get(key) != null)
                    caches.get(key).clear();
            }
            key = "1";
            if (indexs != null) {
                caches.put(key, indexs);
            } else {
                if (caches.get(key) != null)
                    caches.get(key).clear();
            }
        }
    }

    /**
     * id 2-1003 2-2 2-10 etc
     *
     * @param marketLineBean
     * @param reqType
     */
    public void updateAppHoldingInfo(MarketLineBean marketLineBean, int reqType) {
        String holdingPrice = marketLineBean.getAvgPrice();
        String info = "";
        int decimal = marketLineBean.getDecimal();
        if (reqType == SocketKey.POSITION_LINE || reqType == 0) {
            if (getBaseContext() != null && getResources() != null) {
                String hand = getResources().getString(R.string.market_shou);
                info = marketLineBean.getPositionInfo() + " " + hand;
                String longStr = getResources().getString(R.string.tradehis_duo) + " ";
                String shortStr = getResources().getString(R.string.tradehis_kong) + " ";
                int positionType = marketLineBean.getPositionType();
                if (positionType != 0) {
                    info = positionType == 1 ? shortStr + info : longStr + info;
                }
            }

            if (!TextUtils.isEmpty(holdingPrice)) {
                float value = Pub.GetFloat(holdingPrice, -1);
                holdingEntity.setHoldingPrice(value);
                holdingEntity.setHoldingDescription(info);
            } else {
                holdingEntity.setHoldingPrice(-1);
                holdingEntity.setHoldingDescription("");
            }
        }
        holdingEntity.setDecimal(decimal);
        if (reqType == SocketKey.DELIVERY_TIME_CHANGED || reqType == 0) {
            if (marketLineBean.getType() == 2) {
                if (marketLineBean instanceof MarketTimeLineBean) {
                    MarketTimeLineBean temp = (MarketTimeLineBean) marketLineBean;
                    if (!TextUtils.isEmpty(temp.getDeliveryDate())) {
                        holdingEntity.setFutureLimtDays(temp.getDeliveryDate());
                        holdingEntity.setStatus(temp.getStatus());
                        holdingEntity.setDeliveryType(temp.getDeliveryType());
                    }
                }
            }
        }
        holdingEntity.setId(marketLineBean.getId());
        holdingEntity.setType(marketLineBean.getType());
        holdingEntity.setName(marketLineBean.getName());
//        if(id.contains("-")) {
//            holdingEntity.setId(id);
//        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        switchLanguage();//
    }

//    public void switchLanguage() {
//        if (AppConfigs.getLanguegeInt() == -1) {
//            setLanguage();
//        }
//
//        Locale lacale = AppConfigs.getLanguege();
//        Resources resources = getResources();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        Configuration config = resources.getConfiguration();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            config.setLocale(lacale);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                LocaleList localeList = new LocaleList(lacale);
//                LocaleList.setDefault(localeList);
//                config.setLocales(localeList);
//                getApplicationContext().createConfigurationContext(config);
//            }
//        } else {
//            config.locale = lacale;
//        }
//        resources.updateConfiguration(config, dm);
//    }

    public static int containerToobar(String className) {
        if (Pub.isListExists(tabbar)) {
            for (BottomMenuItem menuItem : tabbar) {
                if (ConstantsPage.PAGE_MAP.containsKey(menuItem.getPath())
                        && ConstantsPage.PAGE_MAP.get(menuItem.getPath()).equals(className)
                        ) {
                    return tabbar.indexOf(menuItem);
                }
            }
        }
        return -1;
    }

    {
        //PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        if ((Constants.DEBUG)) {
            PlatformConfig.setWeixin("wxfb09a2593e70d486", "a19fab1f725b9e14c2c1c8884ffa312f");
        } else {
            PlatformConfig.setWeixin("wxb22bcbeadad1df13", "40178593204d410607914622b5544a7c");
        }
        //豆瓣RENREN平台目前只能在服务器端配置
        PlatformConfig.setSinaWeibo("1506139293", "304e446aa493571a6bdece9b535082d0", "https://www.fota.com");
        PlatformConfig.setQQZone("101532177", "7c10317c3724b086e1651c5ae0bea8ca");
        //PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
        PlatformConfig.setTwitter("zCVjLl5rrTG3kldP2ZHVLOJGk", "jh3OfXdMujBdncQa6CCMEqVUEuC1LRybssGatd9kJCIB76prll");

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
        if ("zh".equals(lang)) {
            AppConfigs.setLanguege(AppConfigs.LANGAUGE_SIMPLE_CHINESE);
        } else {
            AppConfigs.setLanguege(AppConfigs.LANGAUGE_ENGLISH);
        }
    }

    @NonNull
    private OptionConfig getOptionConfig() {
        OptionConfig config = new OptionConfig();
        config.setDepositPageChangeListener(new OptionConfig.DepositPageChangeListener() {
            @Override
            public void gotoDepositPage(Activity activity, AccountInfo accountInfo, boolean b) {
                SimpleFragmentActivity.gotoFragmentActivity(activity, ConstantsPage.WalletFragment);
            }
        });

        config.setAllOrderPageChangeListener(new OptionConfig.AllOrderPageChangeListener() {
            @Override
            public void gotoAllOrderPage(Activity activity, AccountInfo accountInfo, boolean b) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("to_option", true);
                SimpleFragmentActivity.gotoFragmentActivity(activity, ConstantsPage.TradeHistoryFragment, bundle);
            }

        });

        config.setLoginPageChangeListener(new OptionConfig.LoginPageChangeListener() {
            @Override
            public void gotoLoginPage(Activity activity) {
                FtRounts.toQuickLogin(activity);

            }
        });
        config.setLogEnable(false);
        config.setDevelopment(Constants.DEBUG);
//        config.setHttpHost("http://47.98.60.120:8089/apioption/v1/");
//        config.setSocketHost("ws://47.98.60.120:8089/apioption/wsoption");
        config.setHttpHost(Constants.DEBUG ? Constants.HTTP_OPTION : Constants.HTTP_OPTION_REL);
        config.setSocketHost(Constants.DEBUG ? Constants.WS_OPTION : Constants.WS_OPTION_REL);

        config.setShareLogo(R.mipmap.fota_share_logo);
        config.setShareLoadUrl("https://fota.com/mobile/download/index.html#/");
        return config;
    }

}
