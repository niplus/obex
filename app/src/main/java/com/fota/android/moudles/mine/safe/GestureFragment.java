package com.fota.android.moudles.mine.safe;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.FragmentGestureBinding;
import com.fota.android.utils.UserLoginUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 手势密码
 */
public class GestureFragment extends BaseFragment implements View.OnClickListener {
    FragmentGestureBinding mBinding;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_gesture, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safe_gesture_title);
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        String gesture = UserLoginUtil.getLoginedGesture();
        if (TextUtils.isEmpty(gesture)) {
            mBinding.switchGest.setChecked(false);
            mBinding.tvChangeGesture.setVisibility(View.GONE);
        } else {
            mBinding.switchGest.setChecked(true);
            mBinding.tvChangeGesture.setVisibility(View.VISIBLE);

        }
        mTitleLayout.setAppBackground(getColor(R.color.transparent));
        mBinding.tvChangeGesture.setOnClickListener(this);
        mBinding.switchGest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String gesture = UserLoginUtil.getLoginedGesture();
                if (TextUtils.isEmpty(gesture)) {
                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.GestureSetFragment);
                    mBinding.switchGest.setChecked(false);
                } else {
                    UserLoginUtil.clearLoginedGesture();
                    mBinding.switchGest.setChecked(false);
                    mBinding.tvChangeGesture.setVisibility(View.GONE);
                }

            }
        });

    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.gestureset_closepre:
                mBinding.tvChangeGesture.setVisibility(View.VISIBLE);
                mBinding.switchGest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    }
                });
                mBinding.switchGest.setChecked(true);
                mBinding.switchGest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        String gesture = UserLoginUtil.getLoginedGesture();
                        if (TextUtils.isEmpty(gesture)) {
                            SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.GestureSetFragment);
                            mBinding.switchGest.setChecked(false);
                        } else {
                            UserLoginUtil.clearLoginedGesture();
                            mBinding.switchGest.setChecked(false);
                            mBinding.tvChangeGesture.setVisibility(View.GONE);
                        }

                    }
                });
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_change_gesture:
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.GestureSetFragment);
                break;
        }
    }
}
