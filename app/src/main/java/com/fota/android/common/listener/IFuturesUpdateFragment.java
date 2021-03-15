package com.fota.android.common.listener;

import com.fota.android.moudles.futures.FutureContractBean;

public interface IFuturesUpdateFragment {

    void updateInstance(String coin, FutureContractBean bundleForFuture, boolean isBuy);

}
