package com.fota.android.moudles.mine.safe;

import android.view.View;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.UserLoginUtil;

public class ForceChangePswFragment extends ChangePswFragment {

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        mTitleLayout.setViewsVisible(0x00);
    }

    @Override
    public boolean onBackHandled() {
        return true;
    }

    @Override
    protected void changeSuccess() {
        EventWrapper.post(Event.create(R.id.safe_finish));
        UserLoginUtil.delUser();
        FtRounts.toQuickLogin(mContext, ConstantsPage.MarketFragment);
        onLeftClick();
    }
}
