package com.fota.android.moudles.mine.tradeset;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.core.base.BaseListView;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.databinding.FragmentTradeleverBinding;
import com.fota.android.moudles.mine.bean.ContractLevelBean;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

/**
 * 合约杠杆调整
 */
public class TradeLeverFragment extends MvpListFragment<TradeLeverPresenter> implements BaseListView {
    FragmentTradeleverBinding mBinding;
    private ContractLeverPopup popupWindow;
    ContractLevelBean contractLevelBean;
    TextView tvLever;

    @Override
    protected TradeLeverPresenter createPresenter() {
        return new TradeLeverPresenter(this);
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.tradelever_title);
    }

    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_xml;
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
    }

    @Override
    protected boolean setRefreshEnable() {
        return false;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        onRefresh();
    }

    /**
     * list coins
     */
    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<ContractLevelBean, ViewHolder>(getContext(), R.layout.item_tradelever) {

            @Override
            public void convert(final ViewHolder holder, final ContractLevelBean model, final int position) {

                if (TextUtils.isEmpty(model.getAssetName())) {
                    holder.setText(R.id.tv_name, mContext.getResources().getString(R.string.mine_hengxian));
                } else {
                    holder.setText(R.id.tv_name, model.getAssetName() + getString(R.string.tradeset_nameadd));
                }
                if (model.getLever() <= 0) {
                    holder.setText(R.id.tv_lever, mContext.getResources().getString(R.string.mine_hengxian));
                } else {
                    holder.setText(R.id.tv_lever, model.getLever() + " " + "x");
                }
                holder.setOnClickListener(R.id.tv_lever, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvLever = holder.getView(R.id.tv_lever);
                        showPopWindow(model.getLever(), position);
                        contractLevelBean = model;

                    }
                });
            }
        };
    }

    /**
     * 杠杆选择弹窗
     *
     * @param lever
     * @param position
     */
    protected void showPopWindow(int lever, int position) {

        popupWindow = new ContractLeverPopup(getContext(), lever, position, new ContractLeverPopup.OnPopClickListener() {
            @Override
            public void onPopClick(int lever, int position) {
                if (lever < 1) {
                    lever = 1;
                }
                if (lever > 100) {
                    lever = 100;
                }
                if (contractLevelBean != null) {
                    getPresenter().setLever(contractLevelBean.getAssetId(), contractLevelBean.getAssetName(), lever);
                }
            }
        });
        popupWindow.show(tvLever);
    }
}
