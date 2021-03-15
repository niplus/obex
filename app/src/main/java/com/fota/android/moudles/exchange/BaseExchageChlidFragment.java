package com.fota.android.moudles.exchange;

import android.view.View;
import android.view.ViewGroup;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BasePresenter;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.utils.StatusBarUtil;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.widget.recyclerview.ViewHolder;

public abstract class BaseExchageChlidFragment<P extends BasePresenter> extends MvpListFragment<P> {

    @Override
    protected boolean setLoadEnable() {
        return true;
    }

    @Override
    protected void onInitView(final View view) {
        super.onInitView(view);
        //构建手势探测器
        UIUtil.setVisibility(getEmpterButton(), false);
        UIUtil.setVisibility(getEmpterImage(), false);
        UIUtil.setVisibility(getEmptyTopMargin(), false);
        if (getEmpterText() != null) {
            getEmpterText().post(new Runnable() {
                @Override
                public void run() {
                    if (getContext() == null) {
                        return;
                    }
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
                    float y = location[1];
                    int all = UIUtil.getScreenHeigh(getContext());
                    //update by fjw 按理底部是50像素  这个30像素是哪里来的 需要研究, 后期这个80需要优化
                    float empty = all - y - UIUtil.dip2px(getContext(), 40) - StatusBarUtil.getStatusBarHeight(getContext());
                    if (empty < UIUtil.dip2px(getContext(), 23)) {
                        empty = UIUtil.dip2px(getContext(), 23);
                    }
                    ViewGroup.LayoutParams para1 = getEmpterText().getLayoutParams();
                    para1.height = (int) empty;
                    getEmpterText().setLayoutParams(para1);
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        if (UserLoginUtil.havaUser()) {
            super.onRefresh();
        } else {
            setDataList(null);
        }
    }

    protected void setHoldBg(ViewHolder holder) {
        if (AppConfigs.isWhiteTheme()) {
            holder.setBackgroundRes(R.id.order_main, R.drawable.ft2_corner_mian_color);
        } else {
            holder.setBackgroundRes(R.id.order_main, R.drawable.ft_corner_mian_color);
        }
    }

    @Override
    public void setHasData(boolean hasData) {
        super.setHasData(hasData);
        if (getParentFragment() instanceof IExchangeFragment) {
            ((IExchangeFragment) getParentFragment()).setHasData(hasData, this);
        }
    }
}
