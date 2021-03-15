package com.fota.android.moudles.mine.safe;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.databinding.FragmentGesturesetBinding;
import com.fota.android.utils.UserLoginUtil;
import com.takwolf.android.lock9.Lock9View;

import java.util.Arrays;

/**
 * 设置手势密码
 */
public class GestureSetFragment extends BaseFragment {
    FragmentGesturesetBinding mBinding;
    private int[] numbers_first = null;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_gestureset, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.safe_gestureset_title);
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        setJustWhiteBarTxt();
        mBinding.lock.setGestureCallback(new Lock9View.GestureCallback() {

            @Override
            public void onNodeConnected(@NonNull int[] numbers) {
//                ToastUtils.with(NormalActivity.this).show("+ " + numbers[numbers.length - 1]);
            }

            @Override
            public void onGestureFinished(@NonNull int[] numbers) {
                if (numbers.length >= 4) {
                    if (numbers_first == null) {//表示此次为第一次绘制
                        numbers_first = numbers;
                        mBinding.lock.clearStatus();
                        mBinding.tvSet.setText(R.string.safe_gesture_reset);
                        setThum(numbers);
                    } else {
                        if (Arrays.equals(numbers, numbers_first)) {//前后两次手势相同
                            showToast(R.string.safe_gesture_suc);
                            eventchangeStatus();
                            StringBuilder builder = new StringBuilder();
                            for (int number : numbers) {
                                builder.append(number);
                            }
                            UserLoginUtil.saveLoginedGesture(builder.toString());//保存手势验证码
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    onLeftClick();
                                }

                            }, 2000);
                        } else {
//                            showToast(mContext.getResources().getString(R.string.safe_gesture_diff));
                            mBinding.lock.clearStatus();
                            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(mContext,
                                    R.anim.gesture_notmatch);
                            mBinding.tvSet.startAnimation(hyperspaceJumpAnimation);
                            mBinding.tvSet.setTextColor(getColor(R.color.red_up));
                            mBinding.tvSet.setText(R.string.safe_gesture_diff);
                        }
                    }
                } else {
                    showToast(mContext.getResources().getString(R.string.safe_gesture_short));
                    mBinding.lock.clearStatus();
                }

            }

        });
        mTitleLayout.setTitleTextColor(getColor(R.color.font_color_black));
        mTitleLayout.setAppBackground(getColor(R.color.transparent));
        mTitleLayout.setLeftIcon(R.mipmap.icon_back);
    }

    /**
     * 设置缩略图形
     */
    private void setThum(int[] numbers) {
        for (int number : numbers) {
            if (number == 1) {
                mBinding.view1.setEnabled(true);
            } else {
//                mBinding.view1.setEnabled(false);
            }
        }
        for (int number : numbers) {
            if (number == 2) {
                mBinding.view2.setEnabled(true);
            } else {
//                mBinding.view2.setEnabled(false);
            }
        }
        for (int number : numbers) {
            if (number == 3) {
                mBinding.view3.setEnabled(true);
            } else {
//                mBinding.view3.setEnabled(false);
            }
        }
        for (int number : numbers) {
            if (number == 4) {
                mBinding.view4.setEnabled(true);
            } else {
//                mBinding.view4.setEnabled(false);
            }
        }
        for (int number : numbers) {
            if (number == 5) {
                mBinding.view5.setEnabled(true);
            } else {
//                mBinding.view5.setEnabled(false);
            }
        }
        for (int number : numbers) {
            if (number == 6) {
                mBinding.view6.setEnabled(true);
            } else {
//                mBinding.view6.setEnabled(false);
            }
        }
        for (int number : numbers) {
            if (number == 7) {
                mBinding.view7.setEnabled(true);
            } else {
//                mBinding.view7.setEnabled(false);
            }
        }
        for (int number : numbers) {
            if (number == 8) {
                mBinding.view8.setEnabled(true);
            } else {
//                mBinding.view8.setEnabled(false);
            }
        }
        for (int number : numbers) {
            if (number == 9) {
                mBinding.view9.setEnabled(true);
            } else {
//                mBinding.view9.setEnabled(false);
            }
        }

    }

    private void eventchangeStatus() {
        EventWrapper.post(Event.create(R.id.gestureset_closepre));//关闭上一页面
//        EventWrapper.post(Event.create(R.id.mine_refresh));//通知我的页面更新

    }
}
