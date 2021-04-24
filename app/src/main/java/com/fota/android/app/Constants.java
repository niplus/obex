package com.fota.android.app;

import android.text.TextUtils;

import com.fota.android.commonlib.base.AppConfigs;


/**
 * Created by Administrator on 2018/3/28.
 */

public class Constants {

    public static final String BROKER_ID_FOTA = "1";


    public static final String BROKER_ID_ATTEX = "508090";


    //^((?!(\*|//)).)+[\u4e00-\u9fa5]

    public static final String NONE = "— —";//debug 域名

    //public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean DEBUG = true;


    // salt
    public static final String SALT = "NCQ0ImCbGMRMcNphOJWrwOp4";

    //base
    public static final String BROKER_ID = BROKER_ID_FOTA;

    //public static final String BASE_IP_FT = "http://192.168.1.114:8084/";
    public static final String BASE_IP_FT = "https://cboex.com/mapi/";
    public static final String BASE_IP_FT_H5 = "https://cboex.com/mapi/";

    public static final String BASE_IP_FT_REL = "https://cboex.com/";
    public static final String BASE_IP_FT_REL_H5 = "https://cboex.com/";


//    public static final String BASE_WEBSOCKET = "http://bg-dev.yuchains.com/mapi/websocket";
    public static final String BASE_WEBSOCKET = "wss://cboex.com/mapi/websocket";
    public static final String BASE_WEBSOCKET_WSS = "wss://cboex.com/mapi/websocket";

    /**
     * 期货 http
     */
    public final static String HTTP_OPTION = "https://cboex.com/apioption/";
    public final static String HTTP_OPTION_REL = "https://cboex.com/apioption/";

    /**
     * 期权
     */
    public final static String WS_OPTION = "wss://cboex.com/apioption/wsoption";
    public final static String WS_OPTION_REL = "wss://cboex.com/apioption/wsoption";

    //websocket
//    public static final String WEB_BASE_URL = DEBUG ? BASE_WEBSOCKET + BASE_IP_FT + BASE_POSTFIX + "websocket" :
//            BASE_WEBSOCKET_WSS + BASE_IP_FT_REL + BASE_POSTFIX + "websocket";

    //     -  /help 帮助中心（默认进入/help/trade
//            •  /help/safe ：帮助中心-安全相关
//  •  /help/trade： 帮助中心-交易相关
//  •  /help/other：帮助中心-其他模块
//  •  /help/notice： 帮助中心-平台公告
//  •  /help/noticeDetail: 平台公告
//  -  /active：活动
    public static String URL_OTHERS = "help/other?";//  其他模块
    public static String URL_SAFE = "help/safe?";// 安全模块
    public static String URL_TRADE = "help/trade?";// 交易模块

    public static String URL_NOTICE = "help/notice?";// 平台公告--帮助中心
    public static String URL_ACTIVIES = "app/#/active?";// 活动
    public static String URL_AQBZ = "insurance?";// 安全保障
    public static String URL_NEWGUIDE = "minecraft?";// 新手教程

    public static String URL_ABOUTFOTA = URL_OTHERS + "id=0";//关于方塔
    public static String URL_FWTK = URL_OTHERS + "id=2";//服务条款
    public static String URL_MZSM = URL_OTHERS + "id=3";//免责声明
    public static String URL_YSBH = URL_OTHERS + "id=4";//隐私保护


    public static String URL_HELPCENTER_CH = "https://support.cboex.io/hc/zh-sg";//帮助中心中文
    public static String URL_HELPCENTER_EN = "https://support.cboex.io/hc/zh-sg";//帮助中心英文
    public static String URL_SPOT_INDEX_HELP_EN = URL_HELPCENTER_EN + "/articles/360012654214-Contract-Pricing-and-Settlement";
    public static String URL_SPOT_INDEX_HELP_CN = URL_HELPCENTER_CH + "/articles/360012406614-%E5%90%88%E7%BA%A6%E8%AE%A1%E4%BB%B7%E4%B8%8E%E4%BA%A4%E5%89%B2%E7%BB%93%E7%AE%97";
    public static String URL_COMMISSION = "wapa/#/";// 邀请返佣
    public static String URL_FWTK_CH = "https://support.cboex.io/hc/zh-sg/articles/360003348916-%E6%9C%8D%E5%8A%A1%E6%9D%A1%E6%AC%BE";//服务条款
    public static String URL_MZSM_CH = "https://support.cboex.io/hc/zh-sg/articles/360003387115-%E9%A3%8E%E9%99%A9%E6%8A%AB%E9%9C%B2-%E5%85%8D%E8%B4%A3%E5%A3%B0%E6%98%8E-";//免责声明
    public static String URL_YSBH_CH = "https://support.cboex.io/hc/zh-sg/articles/360003348956-%E9%9A%90%E7%A7%81%E6%94%BF%E7%AD%96";//隐私保护
    public static String URL_FWTK_EN = "https://support.cboex.io/hc/zh-sg/articles/360003348916-%E6%9C%8D%E5%8A%A1%E6%9D%A1%E6%AC%BE";//服务条款
    public static String URL_MZSM_EN = "https://support.cboex.io/hc/zh-sg/articles/360003387115-%E9%A3%8E%E9%99%A9%E6%8A%AB%E9%9C%B2-%E5%85%8D%E8%B4%A3%E5%A3%B0%E6%98%8E-";//免责声明
    public static String URL_YSBH_EN = "https://support.cboex.io/hc/zh-sg/articles/360003348956-%E9%9A%90%E7%A7%81%E6%94%BF%E7%AD%96";//隐私保护

    public static String URL_GUIDE = "https://support.cboex.io/hc/zh-sg/sections/360001132775";
    public static String JOIN_COMMUNITY = "https://t.me/CBOEX";
    public static String URL_ACTIVITY = "https://support.cboex.io/hc/zh-sg/sections/360001063076-%E6%B4%BB%E5%8A%A8";

    //Bugly
    public static final String BUGLY_KEY = "97c1baf0-a72e-40ef-890a-4d3419af31ec";
    public static final String BUGLY_ID = "019bd29b6d";

    public static String getHttpUrl() {
        if (DEBUG) {
            return (TextUtils.isEmpty(AppConfigs.getIpAddress()) ?
                    BASE_IP_FT : AppConfigs.getIpAddress());
        } else {
            return BASE_IP_FT_REL;
        }
    }


    public static String getH5BaseUrl() {
        if (DEBUG) {
            return (TextUtils.isEmpty(AppConfigs.getH5IpAddress()) ?
                    BASE_IP_FT_H5 : AppConfigs.getH5IpAddress());
        } else {
            return BASE_IP_FT_REL_H5;
        }
    }

    public static String SALT_PSW_DEBUG = "abc";
    public static String SALT_CAPITAL_DEBUG = "123";

    public static String SALT_PSW_RELEASE = "abc";
    public static String SALT_CAPITAL_RELEASE = "123";

    /**
     * 登录密码加密salt
     *
     * @return
     */
    public static String getSaltPsw() {
        return DEBUG ? SALT_PSW_DEBUG : SALT_PSW_RELEASE;
    }

    /**
     * 资金密码加密salt
     *
     * @return
     */
    public static String getSaltCapital() {
        return DEBUG ? SALT_CAPITAL_DEBUG : SALT_CAPITAL_RELEASE;
    }


}
