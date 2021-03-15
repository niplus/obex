package com.fota.android.app;

public class SocketKey {

    public final static int HomePageAccountReqType = 10000;//首页账号概要 -- deprecated

    public final static int TradeEquityReqType = 2;//交易权益可用保证金率 -- 废弃 --deprecated

    public final static int TradeWeiTuoReqType = 1403;//交易委托(全部) o

    public final static int TradeDealReqType = 1103;//成交(个人) l o

    public final static int HangQingKaPianReqType = 1000;//行情卡片(自选热门所有) u

    public final static int HangQingFenShiTuZheXianTuReqType = 1400;//行情分时图交易折线图 u

    public final static int HangQingNewlyPriceReqType = 1405;//行情盘口上的最新成交价 o

    public final static int HangQingTradeDetailReqType = 1404;//行情页面上的成交列表 o

    public final static int HangQingKlinePushReqType = 1401;//行情K线图推送 u

    public final static int MinePositionReqType = 1101;//持仓（个人）l o

    public final static int MineEntrustReqType = 1200;//现货个人委托 l o

    public final static int MineDealReqType = 1202;//现货 成交(个人) l o

    public final static int MineMsgCenterHasUnreadPushReqType = 1300;//我的界面的消息中心铃铛是否显示小红点 --deprecated

    public final static int MineActivityCenterHasUnreadPushReqType = 13;//我的界面的活动中心铃铛是否显示小红点 --deprecated

    public final static int TradeSuccessNotiification = 1402;//交易成功推送通知

    public final static int HomePageHotContractReqType = 10100;//首页热门合约 --deprecated

    public final static int MineAssetReqType = 1201;//资产 o

    public final static int LOG_OUT = 1407;//退出登录，通知socket u

    public final static int DELIVERY_TIME_CHANGED = 1105;//交割日期 u

    public final static int POSITION_LINE = 1106;//持仓 多空几手 持仓线 u

    public final static int MineEntrustReqType_CONTRACT = 1102;//合约个人委托 l o

    public final static int FUTURE_TOP = 1100;//合约顶部的概要信息

    public final static int MARKET_SPOTINDEX = 1408;//现货指数信息
}
