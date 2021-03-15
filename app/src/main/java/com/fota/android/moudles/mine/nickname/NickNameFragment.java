package com.fota.android.moudles.mine.nickname;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.BundleKeys;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.BtbMap;
import com.fota.android.databinding.FragmentSetNickNameBinding;
import com.fota.android.http.Http;

public class NickNameFragment extends BaseFragment implements View.OnClickListener {

    FragmentSetNickNameBinding mBinding;
    private String nickName;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_nick_name, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safesetting_nickname);
    }

    public static final String KEY = "key";

    public static NickNameFragment newInstance(String name) {
        Bundle args = new Bundle();
        args.putString(BundleKeys.KEY, name);
        NickNameFragment fragment = new NickNameFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() == null){
            nickName = "";
        }else {
            nickName = getArguments().getString(BundleKeys.KEY);
        }
    }

    @Override
    protected boolean viewGroupFocused() {
        return false;
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        if (!Pub.isStringEmpty(nickName)) {
            mBinding.etNickname.setText(nickName);
        }
        mBinding.btnSure.setOnClickListener(this);
        bindValid(mBinding.btnSure, mBinding.etNickname);
        valid();
        edtFocus(mBinding.etNickname, mBinding.viewNickname);
    }


    @Override
    protected boolean customerValid() {
        String nickName = mBinding.etNickname.getText().toString().replace(" ", "");
        if (Pub.isStringEmpty(nickName)) {
            mBinding.nicknameError.setText(R.string.set_nick_name);
            return false;
        }
        if (nickName.length() < 2 || nickName.length() > 14) {
            mBinding.nicknameError.setText(R.string.nick_name_long);
            return false;
        }
        mBinding.nicknameError.setText("");
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                String nickName = mBinding.etNickname.getText().toString().replace(" ", "");
                putNickName(nickName);
                //account/user/username
                break;
        }
    }

    /**
     * 设置昵称
     *
     * @param nickName
     */
    private void putNickName(String nickName) {
        BtbMap map = new BtbMap();
        map.p("userName", nickName);
        Http.getHttpService().setNickName(map)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(BaseHttpEntity object) {
                        getHoldingActivity().finish();
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                    }
                });
    }
}