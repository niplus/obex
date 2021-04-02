package com.fota.android.moudles.wallet.withdraw;

import com.fota.android.common.bean.wallet.RateBean;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.commonlib.base.BaseView;
import com.fota.android.core.base.BtbMap;

/**
 * Created by Dell on 2018/4/19.
 */

public interface WithdrawView extends BaseView {

    void setSuccess(BtbMap map);

    void setSelectItem(WalletItem walletItem);

    void setRate(RateBean rate);
}
