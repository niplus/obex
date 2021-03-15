package com.fota.android.moudles.market;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.UserLoginUtil;

public class MarketFavorListFragment extends MarketIndexSpotListFragment {

    public static MarketFavorListFragment newInstance() {
        Bundle args = new Bundle();
//        args.putString("symbol", symbol);
        MarketFavorListFragment fragment = new MarketFavorListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView txtFavorControl;

    protected View setHeadView() {
        View view = View.inflate(getContext(), R.layout.layout_empty_favor, null);
        return view;
    }

    @Override
    protected void setEmptyView() {
        super.setEmptyView();
        txtFavorControl = getEmpterLayout().findViewById(R.id.txt_favor_control);
        setFavorText();
    }

    private void setFavorText() {
        if(txtFavorControl != null) {
            if (!UserLoginUtil.havaUser()) {
                txtFavorControl.setText(getString(R.string.market_deal_sync_self));
            } else {
                txtFavorControl.setText(getString(R.string.market_deal_add_self));
            }
        }
    }

    @Override
    public void onRefresh() {
        setFavorText();
        super.onRefresh();
    }

    @Override
    public void showNoData() {
        doComplete();
        if (getEmpterLayout() == null) {
            return;
        }

        setHasData(false);
        UIUtil.setVisibility(getEmpterLayout(), true);
        UIUtil.setVisibility(getEmpterButton(), false);
        //重置
        if (getEmpterText() != null) {
            if (getContext() != null) {
//                getEmpterText().setTextColor(Pub.getColor(getContext(), R.attr.unable));
                getEmpterText().setVisibility(View.INVISIBLE);
            }
            UIUtil.setText(getEmpterText(), getString(R.string.common_data_empty));
            getEmpterText().setOnClickListener(null);
        }
        if (getEmpterImage() != null) {
            getEmpterImage().setVisibility(View.INVISIBLE);
        }

        if (txtFavorControl != null) {
            txtFavorControl.setVisibility(View.VISIBLE);
            if (!UserLoginUtil.havaUser()) {
                txtFavorControl.setText(getString(R.string.market_deal_sync_self));
                txtFavorControl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FtRounts.toQuickLogin(mContext);
                        //why add this
//                        EventWrapper.post(Event.create(R.id.event_market_reload));
                    }
                });
            } else {
                txtFavorControl.setText(getString(R.string.market_deal_add_self));
                txtFavorControl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventWrapper.post(Event.create(R.id.event_market_favor_add));
                    }
                });
            }
        }

        //no data need change bg_color
        noDataError = true;
        int recycleBg = Pub.getColor(getContext(), R.attr.reverse_bg);
//        int recycleBg = Pub.getColor(getContext(), R.attr.bg_color);
        mRecyclerView.setBackgroundColor(recycleBg);
        if (adapter != null) {
            adapter.putList(null);
        }
    }

    @Override
    public void showNoNetWork() {
        super.showNoNetWork();

        if (getEmpterLayout() == null) {
            return;
        }
        TextView favorText = getEmpterLayout().findViewById(R.id.txt_favor_control);
        if (favorText != null) {
            favorText.setVisibility(View.GONE);
        }
    }

    @Override
    public void showFailer(String msg, ApiException e) {
        super.showFailer(msg, e);
        if (getEmpterLayout() == null) {
            return;
        }
        TextView favorText = getEmpterLayout().findViewById(R.id.txt_favor_control);
        if (favorText != null) {
            favorText.setVisibility(View.GONE);
        }
    }

    @Override
    public void emptyButtonReloadEvent() {
        EventWrapper.post(Event.create(R.id.event_market_reload));
    }

}
