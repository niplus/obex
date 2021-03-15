package com.fota.android.socket;

public interface IWebSocketObserver {
//
//    /**
//     * @param gaiLanBean
//     * 概览
//     */
//    void updateOverview(GaiLanBean gaiLanBean);
//
//    /**
//     * @param contracts
//     * 热门合约
//     */
//    void updateHotContract(List<HotContractBean> contracts);
//
//    void updateDepth(DepthBean depthBean);
//
//    void updateMarkets(List<FutureItemEntity> futureItems);
//
//    void updateMarketDetails();

    void updateWebSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity);
}
