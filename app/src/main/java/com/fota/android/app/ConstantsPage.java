package com.fota.android.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/28.
 */

public class ConstantsPage {

    /**
     * 下面部分是页面外跳转
     */
    public final static String DOMAIN = "fota://";

    public final static String FotaLoginActivity = "com.fota.android.moudles.mine.login.FotaLoginActivity";
    public final static String FullScreenKlineActivity = "com.fota.android.moudles.market.FullScreenKlineActivity ";
    public final static String MainActivity = "com.fota.android.moudles.main.MainActivity";
    public final static String MarketSearchActivity = "com.fota.android.moudles.market.MarketSearchActivity";
    public final static String MvpActivity = "com.fota.android.core.base.MvpActivity";
    public final static String SimpleFragmentActivity = "com.fota.android.core.base.SimpleFragmentActivity";
    public final static String SplashActivity = "com.fota.android.moudles.welcome.SplashActivity";
    public final static String TradeMarketKlineActivity = "com.fota.android.moudles.market.TradeMarketKlineActivity";
    public final static String TransformActivity = "com.fota.android.moudles.welcome.TransformActivity";
    public final static String WelcomeActivity = "com.fota.android.moudles.welcome.WelcomeActivity";
    public final static String WithdrawActivity = "com.fota.android.moudles.wallet.withdraw.WithdrawActivity";

    public final static Map<String, String> PAGE_ACTIVITY = new HashMap<String, String>() {
        {
            put("fota://goto/login", FotaLoginActivity);
        }
    };

    public final static String AboutFotaFragment = "com.fota.android.moudles.mine.set.AboutFotaFragment";

    public final static String AddAddressFragment = "com.fota.android.common.addressmanger.AddAddressFragment";

    public final static String AddIpAddressFragment = "AddIpAddressFragment";

    public final static String AddressListFragment = "com.fota.android.common.addressmanger.AddressListFragment";

    public final static String BaseExchageChlidFragment = "com.fota.android.moudles.exchange.BaseExchageChlidFragment";

    public final static String BindEmailFragment = "com.fota.android.moudles.mine.safe.BindEmailFragment";

    public final static String BindGoogleFragment = "com.fota.android.moudles.mine.safe.BindGoogleFragment";

    public final static String BindPhoneFragment = "com.fota.android.moudles.mine.safe.BindPhoneFragment";

    public final static String CapitalForgetFragment = "com.fota.android.moudles.mine.safe.CapitalForgetFragment";

    public final static String CapitalResetFragment = "com.fota.android.moudles.mine.safe.CapitalResetFragment";

    public final static String CapitalSetFragment = "com.fota.android.moudles.mine.safe.CapitalSetFragment";

    public final static String CheckCountryFragment = "com.fota.android.moudles.mine.login.CheckCountryFragment";

    public final static String ChengjiaoFragment = "com.fota.android.moudles.mine.tradehistory.ChengjiaoFragment";

    public final static String ContractAccountFragment = "com.fota.android.moudles.wallet.transfer.ContractAccountFragment";

    public final static String ExchangeFragment = "com.fota.android.moudles.exchange.index.ExchangeFragment";

    public final static String ExchangeMoneyListFragment = "com.fota.android.moudles.exchange.money.ExchangeMoneyListFragment";

    public final static String ExchangeOrdersFragment = "com.fota.android.moudles.exchange.orders.ExchangeOrdersFragment";

    public final static String FTForgetpasswordFragment = "com.fota.android.moudles.mine.resetpassword.FTForgetpasswordFragment";

    public final static String FTForgetpasswordNextFragment = "com.fota.android.moudles.mine.resetpassword.FTForgetpasswordNextFragment";

    public final static String FingerFragment = "com.fota.android.moudles.mine.safe.FingerFragment";

    public final static String ForceChangePswFragment = "com.fota.android.moudles.mine.safe.ForceChangePswFragment";

    public final static String FuturesCompleteFragment = "com.fota.android.moudles.futures.complete.FuturesCompleteFragment";

    public final static String FuturesFragment = "com.fota.android.moudles.futures.FuturesFragment";

    public final static String FuturesMoneyListFragment = "com.fota.android.moudles.futures.money.FuturesMoneyListFragment";

    public final static String FuturesOrdersFragment = "com.fota.android.moudles.futures.order.FuturesOrdersFragment";

    public final static String GestureFragment = "com.fota.android.moudles.mine.safe.GestureFragment";

    public final static String GestureSetFragment = "com.fota.android.moudles.mine.safe.GestureSetFragment";

    public final static String HelpFragment = "com.fota.android.moudles.mine.HelpFragment";

    public final static String IdentityFragment = "com.fota.android.moudles.mine.identity.IdentityFragment";

    public final static String IpSettingFragment = "IpSettingFragment";

    public final static String WsIpSettingFragment = "WsIpSettingFragment";

    public final static String LanguageFragment = "com.fota.android.moudles.mine.LanguageFragment";

    public final static String NickNameFragment = "com.fota.android.moudles.mine.nickname.NickNameFragment";

    public final static String MarketFragment = "com.fota.android.moudles.market.MarketFragment";

    public final static String MarketFavorListFragment = "com.fota.android.moudles.market.MarketFavorListFragment";

    public final static String MarketListFragment = "com.fota.android.moudles.market.MarketListFragment";

    public final static String MineFragment = "com.fota.android.moudles.mine.MineFragment";

    public final static String MvpFragment = "com.fota.android.core.base.MvpFragment";

    public final static String MvpListFragment = "com.fota.android.core.base.list.MvpListFragment";

    public final static String NoticeCenterFragment = "com.fota.android.moudles.mine.notice.NoticeCenterFragment";

    public final static String OptionWelcomeFragment = "com.fota.android.moudles.option.OptionWelcomeFragment";

    public final static String QuickCheckCapitalFragment = "com.fota.android.moudles.mine.safe.QuickCheckCapitalFragment";

    public final static String QuickLoginFragment = "com.fota.android.moudles.mine.login.QuickLoginFragment";

    public final static String RechargeMoneyFragment = "com.fota.android.moudles.wallet.recharge.RechargeMoneyFragment";

    public final static String RegistFragment = "com.fota.android.moudles.mine.login.RegistFragment";

    public final static String SafeSettingFragment = "com.fota.android.moudles.mine.safe.SafeSettingFragment";

    public final static String SettingFragment = "com.fota.android.moudles.mine.set.SettingFragment";

    public final static String ThemeFragment = "com.fota.android.moudles.mine.set.ThemeFragment";

    public final static String TradeHistoryFragment = "com.fota.android.moudles.mine.tradehistory.TradeHistoryFragment";

    public final static String AllHistoryFragment = "com.fota.android.moudles.mine.tradehistory.AllHistoryFragment";

    public final static String SpotHistoryFragment = "com.fota.android.moudles.mine.tradehistory.SpotHistoryFragment";

    public final static String TradeLeverFragment = "com.fota.android.moudles.mine.tradeset.TradeLeverFragment";

    public final static String TransferHistoryFragment = "com.fota.android.moudles.wallet.transfer.TransferHistoryFragment";

    public final static String TransferListFragment = "com.fota.android.moudles.wallet.transfer.TransferListFragment";

    public final static String UNBindGoogleFragment = "com.fota.android.moudles.mine.safe.UNBindGoogleFragment";

    public final static String UsdtContractTransferFragment = "com.fota.android.moudles.wallet.transfer.UsdtContractTransferFragment";

    public final static String WalletDetailsFragment = "com.fota.android.moudles.wallet.index.WalletDetailsFragment";

    public final static String WalletFragment = "com.fota.android.moudles.wallet.index.WalletFragment";

    public final static String WebPowerfulFragment = "com.fota.android.moudles.common.WebPowerfulFragment";

    public final static String WeituoFragment = "com.fota.android.moudles.mine.tradehistory.WeituoFragment";

    public final static String WithDrawHistoryFragment = "com.fota.android.moudles.wallet.history.WithDrawHistoryFragment";

    public final static String ChangePswFragment = "com.fota.android.moudles.mine.safe.ChangePswFragment";

    public final static Map<String, String> PAGE_MAP = new HashMap<String, String>() {
        {
            put("fota://goto/market", MarketFragment);
            put("fota://goto/contract", FuturesFragment);
            put("fota://goto/coin", ExchangeFragment);
            put("fota://goto/mine", MineFragment);
            put("fota://goto/minereview", MineFragment);
            put("fota://goto/notification", NoticeCenterFragment);
            put("fota://goto/option", OptionWelcomeFragment);
            put("fota://goto/login", QuickLoginFragment);
        }
    };

    public static List<String> withoutLoginFragment = Arrays.asList(
            ExchangeFragment,
            FuturesFragment,
            MarketFragment,
            MineFragment,
            SettingFragment,
            HelpFragment,
            SettingFragment,
            AboutFotaFragment,
            CheckCountryFragment,
            RegistFragment,
            FTForgetpasswordFragment,
            FTForgetpasswordNextFragment,
            WebPowerfulFragment,
            LanguageFragment,
            ThemeFragment,
            QuickLoginFragment,
            QuickCheckCapitalFragment,
            IpSettingFragment,
            WsIpSettingFragment,
            AddIpAddressFragment
    );
}
