package com.fota.android.moudles.wallet.recharge;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fota.android.MyViewHolder;
import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.common.bean.wallet.WalletBean;
import com.fota.android.common.bean.wallet.WalletItem;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.SpannableStringUtils;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.commonlib.utils.URLUtils;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.BtbMap;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.databinding.FragmentRechargeMoneyBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.wallet.history.WithDrawHistoryFragment;
import com.fota.android.utils.ZXingUtils;
import com.fota.android.widget.TitleLayout;
import com.fota.android.widget.popwin.SelectItemDialog;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Dell on 2018/4/24.
 * <p>
 * 充币页面
 */

public class RechargeMoneyFragment extends BaseFragment implements View.OnClickListener {

    private FragmentRechargeMoneyBinding mBinding;
    private ClipboardManager myClipboard;
    Bitmap qrBitmap;
    private List<WalletItem> items;

    public List<String> netData;

    private String selectNet = "";

    public static RechargeMoneyFragment newInstance(WalletItem walletItem) {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.MODEL, walletItem);
        RechargeMoneyFragment fragment = new RechargeMoneyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        model = (WalletItem) bundle.getSerializable(BundleKeys.MODEL);
    }

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recharge_money, container, false);
        mBinding.setView(this);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        netData = new ArrayList<>();
        mBinding.tvCopy.setCircleButtonStyle(true);
        myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        getWallet();

        if (AppConfigs.getTheme() == 0) {
            mTitleLayout.setRightIcon(R.mipmap.wallet_his_black);
        } else {
            mTitleLayout.setRightIcon(R.mipmap.wallet_his_white);
        }
        mTitleLayout.setRightIconSize(UIUtil.dip2px(getContext(), 18),
                UIUtil.dip2px(getContext(), 19.5));
        mTitleLayout.setOnRightButtonClickListener(new TitleLayout.OnRightButtonClickListener() {
            @Override
            public void onRightButtonClick(View v) {
                if (model == null) {
                    return;
                }
                addFragment(WithDrawHistoryFragment.newInstance(model.getAssetId()));
            }
        });

        mBinding.rvNet.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        mBinding.rvNet.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdraw_recharge_net, null, false);
//                TextView tvNet = rootView.findViewById(R.id.tv_net);
                return new MyViewHolder(rootView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                TextView view = ((MyViewHolder)holder).itemView.findViewById(R.id.tv_net);
                view.setText(netData.get(position));

                if (netData.get(position).endsWith(selectNet)){
                    view.setTextColor((int)0xFF3C78D7);
                    view.setBackgroundResource(R.drawable.shape_stroke_3c78d7);
                }else {
                    view.setTextColor((int)0xFF999999);
                    view.setBackgroundResource(R.drawable.shape_stroke_cacaca);
                }

                view.setOnClickListener(v -> {
                    selectNet = view.getText().toString();
                    mBinding.rvNet.getAdapter().notifyDataSetChanged();
                    getAddress();
                });

            }

            @Override
            public int getItemCount() {
                return netData.size();
            }
        });
        mBinding.rvNet.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != 0){
                    outRect.left = 30;
                }
            }
        });
    }

    private void setTip() {
        SpannableStringUtils.Builder sb = SpannableStringUtils.getBuilder(
                "1. ");
        sb.append(getString(R.string.wallet_rechange_money_1));
        sb.append(" ");
        sb.append(model.getAssetName());
        sb.append(" ");
        sb.append(getString(R.string.common_full_point));
        sb.append("\n");
        sb.append(getString(R.string.wallet_tip_withdraw_3));
        sb.append(" ");
        sb.append(getString(R.string.wallet_tip_withdraw_history_page))
                .setForegroundColor(Pub.getColor(getContext(), R.attr.main_color));
        sb.append(" ");
        sb.append(getString(R.string.wallet_tip_withdraw_review));

        mBinding.tip.setText(sb.create());
        mBinding.tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model == null) {
                    return;
                }
                addFragment(WithDrawHistoryFragment.newInstance(model.getAssetId()));
            }
        });

    }

    @Override
    protected String setAppTitle() {
        return getXmlString(R.string.wallet_recharge_money);
    }

    private void getAddress() {
        BtbMap map = new BtbMap();
        map.p("assetId", model.getAssetId());
        map.p("assetName", model.getAssetName());
//        if(model.isNetWork()){
            map.p("network", selectNet);
//        }else{
//            map.p("network", model.getAssetName());
//        }


        Http.getWalletService().deposite(map)
                .compose(new CommonTransformer<String>())
                .subscribe(new CommonSubscriber<String>(this) {

                    @Override
                    public void onNext(String list) {
                        setRechargeEntity(list);
                    }

                    @Override
                    protected void onError(ApiException e) {
                        //super.onError(e);
                        setError();
                    }
                });
    }

    private void setRechargeEntity(final String address) {
        if (getContext() == null) {
            return;
        }
        if (address == null) {
            setError();
            return;
        }
        mBinding.tvQrcode.setText(address);
        mBinding.tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyTextToClipBoard(mBinding.tvQrcode.getText().toString());
            }
        });
        try {
            qrBitmap = ZXingUtils.Create2DCode(address, UIUtil.dip2px(getContext(), 200)
                    , UIUtil.dip2px(getContext(), 200));
            if (qrBitmap != null) {
                mBinding.ivQrcode.setImageBitmap(qrBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            qrBitmap = null;
        }
    }

    private void setError() {
        if (AppConfigs.isWhiteTheme()) {
            mBinding.ivQrcode.setImageResource(R.mipmap.common_no_data_white);
        } else {
            mBinding.ivQrcode.setImageResource(R.mipmap.common_no_data);
        }
        mBinding.tvQrcode.setText(R.string.common_data_error);
        mBinding.tvCopy.setText(R.string.common_reload);
        mBinding.tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddress();
            }
        });
        stopProgressDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy:
                copyTextToClipBoard(mBinding.tvQrcode.getText().toString());
                break;
            case R.id.select_asset:
                showSelectDialog();
                break;
        }
    }

    private void showSelectDialog() {
        SelectItemDialog dialog = new SelectItemDialog(getContext(), getXmlString(R.string.select_recharge_asset),
                items, model);
        dialog.setListener(new SelectItemDialog.OnSureClickListener() {

            @Override
            public void onClick(FtKeyValue model) {
                mBinding.tvCopy.setCircleButtonStyle(true);
                mBinding.tvCopy.setText(R.string.wallet_withdraw_copy_address);
                setSelectItem((WalletItem) model);
            }
        });
        dialog.show();
    }

    public boolean copyTextToClipBoard(String str) {
        boolean flag = false;
        if (str == null)
            return flag;
        try {
            ClipData myClip = ClipData.newPlainText("text", str);//str
            myClipboard.setPrimaryClip(myClip);
            mBinding.tvCopy.setText(CommonUtils.getResouceString(getContext(), R.string.copy_success));
            mBinding.tvCopy.setCircleButtonStyle(false);
        } catch (Exception e) {
            mBinding.tvCopy.setText(CommonUtils.getResouceString(getContext(), R.string.copy_fail));
        }
        return flag;
    }

    WalletItem model;

    /**
     * 获取钱包
     */
    public void getWallet() {
        Http.getWalletService().getWallet()
                .compose(new CommonTransformer<WalletBean>())
                .subscribe(new CommonSubscriber<WalletBean>(this) {

                    @Override
                    public void onNext(WalletBean list) {
                        items = new ArrayList<>();
                        for (WalletItem bean : list.getItem()) {
//                            if ("USDT".equals(bean.getAssetName())) {
//                                WalletItem itemOmni = new WalletItem(bean);
//                                itemOmni.OMNI();
//                                WalletItem itemEth = new WalletItem(bean);
//                                itemEth.ETH();
//                                items.add(itemOmni);
//                                items.add(itemEth);
//                            } else {
                                items.add(bean);
//                            }
                        }
                        setSelectItem(items.get(0));
                    }
                });
    }

    private void setSelectItem(WalletItem walletItem) {
        this.model = walletItem;
        setTip();
        mBinding.assetName.setText(model.getAssetName());
        Glide.with(getContext()).load(URLUtils.getFullPath(model.getCoinIconUrl()))
                .apply(new RequestOptions().error(R.mipmap.icon_logo)).into(mBinding.assetIcon);

        netData.clear();
        selectNet = "";
        if (model.getWnetwork() != null) {
            netData.addAll(model.getWnetwork());
            selectNet = model.getWnetwork().get(0);
        }
        mBinding.rvNet.getAdapter().notifyDataSetChanged();
        getAddress();
    }
}
