package com.fota.android.moudles.wallet.transfer;

import android.view.View;

import com.fota.android.R;
import com.fota.android.common.bean.wallet.TransferListItemBean;
import com.fota.android.core.base.list.MvpListFragment;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.ViewHolder;

public class TransferListFragment extends MvpListFragment<TransferListPresenter> {
    private boolean isFromContract;

    @Override
    protected TransferListPresenter createPresenter() {
        return new TransferListPresenter(this);
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        getPresenter().setFromContract(isFromContract);
        onRefresh();
    }

    @Override
    protected int getContainerLayout() {
        return R.layout.recyclerview_notitle_xml;
    }

    @Override
    protected void initMainAdapter() {
        adapter = new EasyAdapter<TransferListItemBean, ViewHolder>(getContext(), R.layout.item_transfer_record) {

            @Override
            public void convert(ViewHolder holder, final TransferListItemBean model, int position) {
                holder.setText(R.id.txt_money_to, model.getToType() == 2 ? getString(R.string.transfer_to_contract) : getString(R.string.transfer_to_wallet));
                holder.setText(R.id.txt_transfer_amount, model.getAmount());
                holder.setText(R.id.txt_date_time, model.getFormatTime());
            }
        };
    }

    protected boolean setLoadEnable() {
        return true;
    }

    public void setFromContract(boolean fromContract) {
        isFromContract = fromContract;
    }
}
