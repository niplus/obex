package com.fota.android.moudles.option;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.commonlib.utils.ValueUtil;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.databinding.FragmentOptionWelcomeBinding;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.UserLoginUtil;
import com.fota.option.OptionActivity;
import com.fota.option.OptionConfig;
import com.fota.option.OptionManager;
import com.fota.option.ShareMenuItem;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.List;

import static com.fota.android.commonlib.app.AppVariables.getApplicationContext;

/**
 * 柱子距顶部距离 1：190  2：219   3：167    4：196   5：181   6：243
 * 7：209   8：179   9：195   10：238   11：205   12：165
 * 文字背景距顶部距离：267
 * 内容1（文字+圆点+虚线）：delay 0.14s从下向上位移30px 时长0.4s 先快后慢  透明度 0---1 先快后慢
 * 内容2（实线）：delay 0.04s 同上
 * 内容3（文字2+圆点2+虚线2）：delay 0.04s 同上
 * 按钮：delay 0.56s 缩放0---1 ； 透明度0---1 时长0.3s
 */
public class OptionWelcomeFragment extends BaseFragment implements View.OnClickListener {


    private FragmentOptionWelcomeBinding mBinding;

    private int IMV_TOP_START;

    private int IMV_TOP_FINAL1;
    private int IMV_TOP_FINAL2;
    private int IMV_TOP_FINAL3;
    private int IMV_TOP_FINAL4;
    private int IMV_TOP_FINAL5;
    private int IMV_TOP_FINAL6;
    private int IMV_TOP_FINAL7;
    private int IMV_TOP_FINAL8;
    private int IMV_TOP_FINAL9;
    private int IMV_TOP_FINAL10;
    private int IMV_TOP_FINAL11;
    private int IMV_TOP_FINAL12;


    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_option_welcome, container, false);
        mBinding.setView(this);
        return mBinding.getRoot();
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        mBinding.dashLine.setDefaultColor(Pub.getColor(getContext(), R.attr.main_color));
        mBinding.dashLine2.setDefaultColor(Pub.getColor(getContext(), R.attr.main_color));
        if (AppConfigs.getTheme() == 0) {
            mBinding.viewLinebg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_optionwel_line_black));
        } else {
            mBinding.viewLinebg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_optionwel_line));
        }
        IMV_TOP_START = UIUtil.dip2px(mContext, 267);

        IMV_TOP_FINAL1 = UIUtil.dip2px(mContext, 190);
        IMV_TOP_FINAL2 = UIUtil.dip2px(mContext, 219);
        IMV_TOP_FINAL3 = UIUtil.dip2px(mContext, 167);
        IMV_TOP_FINAL4 = UIUtil.dip2px(mContext, 196);
        IMV_TOP_FINAL5 = UIUtil.dip2px(mContext, 181);
        IMV_TOP_FINAL6 = UIUtil.dip2px(mContext, 243);
        IMV_TOP_FINAL7 = UIUtil.dip2px(mContext, 209);
        IMV_TOP_FINAL8 = UIUtil.dip2px(mContext, 179);
        IMV_TOP_FINAL9 = UIUtil.dip2px(mContext, 195);
        IMV_TOP_FINAL10 = UIUtil.dip2px(mContext, 238);
        IMV_TOP_FINAL11 = UIUtil.dip2px(mContext, 205);
        IMV_TOP_FINAL12 = UIUtil.dip2px(mContext, 165);
        setBg();
        mTitleLayout.llLeftGoBack.setVisibility(View.GONE);
        mTitleLayout.setBackgroundColor(0x00000000);
//        setColumnMargin();
        bgAnim();
        columnAnimStart();
        enterAnim();
        txtAnim();
        if (AppConfigs.getTheme() == 0) {
            mBinding.animationView.setAnimation("data.json");
            mBinding.imvBg.setImageResource(R.mipmap.option_welcome_black);
            mBinding.optionIv1.setImageResource(R.mipmap.column_black1);
            mBinding.optionIv2.setImageResource(R.mipmap.column_black2);
            mBinding.optionIv3.setImageResource(R.mipmap.column_black3);
            mBinding.optionIv4.setImageResource(R.mipmap.column_black4);
            mBinding.optionIv5.setImageResource(R.mipmap.column_black5);
            mBinding.optionIv6.setImageResource(R.mipmap.column_black6);
            mBinding.optionIv7.setImageResource(R.mipmap.column_black7);
            mBinding.optionIv8.setImageResource(R.mipmap.column_black8);
            mBinding.optionIv9.setImageResource(R.mipmap.column_black9);
            mBinding.optionIv10.setImageResource(R.mipmap.column_black10);
            mBinding.optionIv11.setImageResource(R.mipmap.column_black11);
            mBinding.optionIv12.setImageResource(R.mipmap.column_black12);

        } else {
            mBinding.animationView.setAnimation("data_blue.json");
            mBinding.imvBg.setImageResource(R.mipmap.option_welcome_white);
            mBinding.optionIv1.setImageResource(R.mipmap.column_white1);
            mBinding.optionIv2.setImageResource(R.mipmap.column_white2);
            mBinding.optionIv3.setImageResource(R.mipmap.column_white3);
            mBinding.optionIv4.setImageResource(R.mipmap.column_white4);
            mBinding.optionIv5.setImageResource(R.mipmap.column_white5);
            mBinding.optionIv6.setImageResource(R.mipmap.column_white6);
            mBinding.optionIv7.setImageResource(R.mipmap.column_white7);
            mBinding.optionIv8.setImageResource(R.mipmap.column_white8);
            mBinding.optionIv9.setImageResource(R.mipmap.column_white9);
            mBinding.optionIv10.setImageResource(R.mipmap.column_white10);
            mBinding.optionIv11.setImageResource(R.mipmap.column_white11);
            mBinding.optionIv12.setImageResource(R.mipmap.column_white12);
        }

    }

    @SuppressLint("WrongConstant")
    private void setBg() {
        float[] floats = {
                UIUtil.dip2px(getContext(), 10),
                UIUtil.dip2px(getContext(), 10),
                UIUtil.dip2px(getContext(), 10),
                UIUtil.dip2px(getContext(), 10),
                UIUtil.dip2px(getContext(), 0),
                UIUtil.dip2px(getContext(), 0),
                0,
                0};
        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(Pub.getColor(getContext(), R.attr.bg_color));
        //gd.setColor(0xFFFF00FF);
        gd.setGradientType(GradientDrawable.OVAL);
        gd.setCornerRadii(floats);
        UIUtil.setBackgroundDrawable(mBinding.bottomLl, gd);

        UIUtil.setCircleBorderBg(mBinding.outCircle, 0xFF5683DD);
        UIUtil.setCircleBorderBg(mBinding.outCircle2, 0xFF5683DD);

        UIUtil.setCircleBg(mBinding.inCircle, 0xFF5683DD);
        UIUtil.setCircleBg(mBinding.inCircle2, 0xFF5683DD);
    }

    @Override
    protected String setAppTitle() {
        return getResources().getString(R.string.optionwelcome_title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_sure:
                openOption();
                break;
        }
    }

    private void openOption() {
        UMShareAPI umShareAPI = UMShareAPI.get(getContext());
        List<ShareMenuItem> shareList = new ArrayList<>();
        if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.WEIXIN)) {
            shareList.add(new ShareMenuItem(R.mipmap.umeng_wechat, getXmlString(R.string.share_wechat)));
            shareList.add(new ShareMenuItem(R.mipmap.umeng_we_circle, getXmlString(R.string.share_circle)));
        }
        if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.SINA)) {
            shareList.add(new ShareMenuItem(R.mipmap.umeng_sina, getXmlString(R.string.share_sina)));
        }
        if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.QQ)) {
            shareList.add(new ShareMenuItem(R.mipmap.umeng_qq, getXmlString(R.string.share_qq)));
        } else if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.TWITTER)) {
            shareList.add(new ShareMenuItem(R.mipmap.umeng_twitter, getXmlString(R.string.share_twitter)));
        }
        OptionManager.getConfig().setShareMenuList(shareList);
        OptionManager.getConfig().setShareMenuListener(new OptionConfig.OnClickShareMenuListener() {

            @Override
            public void onClickShareMenu(Activity activity, int position, Bitmap bitmap) {
                SHARE_MEDIA share_media = null;
                UMImage imageLocal = new UMImage(activity, bitmap);
                ShareMenuItem item = OptionManager.getConfig().getShareMenuList().get(position);
                if (getXmlString(R.string.share_wechat).equals(item.getMenuString())) {
                    share_media = SHARE_MEDIA.WEIXIN;
                }
                if (getXmlString(R.string.share_circle).equals(item.getMenuString())) {
                    share_media = SHARE_MEDIA.WEIXIN_CIRCLE;
                }
                if (getXmlString(R.string.share_sina).equals(item.getMenuString())) {
                    share_media = SHARE_MEDIA.SINA;
                }
                if (getXmlString(R.string.share_qq).equals(item.getMenuString())) {
                    share_media = SHARE_MEDIA.QQ;
                }
                if (getXmlString(R.string.share_twitter).equals(item.getMenuString())) {
                    share_media = SHARE_MEDIA.TWITTER;
                }
                new ShareAction(activity).withMedia(imageLocal).setPlatform(share_media).share();
            }
        });
        OptionManager.setUserIdAndToken(UserLoginUtil.getId(), UserLoginUtil.getToken());
        FtRounts.toNextActivity(getContext(), OptionActivity.class);
    }

    /**
     * 柱子动画
     *
     * @param duration
     */
    private void setConlumnAnim(final View view, int duration, final int fromTop, final int toTop) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                final int viewTop = ValueUtil.evalute(value, fromTop, toTop);
                setMarginTop(view, viewTop);


            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());

        valueAnimator.setDuration(duration);

        valueAnimator.start();
    }

    /**
     * 透明度动画
     *
     * @param duration
     */
    private void setAlphaAnim(final View view, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                view.setAlpha(value);
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());

        valueAnimator.setDuration(duration);

        valueAnimator.start();
    }

    /**
     * 设置距离顶部距离
     *
     * @param view
     * @param t
     */
    private void setMarginTop(View view, int t) {
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        UIUtil.setMargins(view, p.leftMargin, t, p.rightMargin, p.bottomMargin);
    }

    /**
     * 1：190  2：219   3：167    4：196   5：181   6：243
     * * 7：209   8：179   9：195   10：238   11：205   12：165
     */
    private void setColumnMargin() {
        setMarginTop(mBinding.optionIv1, UIUtil.dip2px(mContext, 190));
        setMarginTop(mBinding.optionIv2, UIUtil.dip2px(mContext, 219));
        setMarginTop(mBinding.optionIv3, UIUtil.dip2px(mContext, 167));
        setMarginTop(mBinding.optionIv4, UIUtil.dip2px(mContext, 196));
        setMarginTop(mBinding.optionIv5, UIUtil.dip2px(mContext, 181));
        setMarginTop(mBinding.optionIv6, UIUtil.dip2px(mContext, 243));
        setMarginTop(mBinding.optionIv7, UIUtil.dip2px(mContext, 209));
        setMarginTop(mBinding.optionIv8, UIUtil.dip2px(mContext, 179));
        setMarginTop(mBinding.optionIv9, UIUtil.dip2px(mContext, 195));
        setMarginTop(mBinding.optionIv10, UIUtil.dip2px(mContext, 238));
        setMarginTop(mBinding.optionIv11, UIUtil.dip2px(mContext, 205));
        setMarginTop(mBinding.optionIv12, UIUtil.dip2px(mContext, 165));

    }

    private Handler handler = new Handler();

    /**
     * 柱子动画
     * 1. 柱子组一（1，4，6，9）：delay 0.32s 从下向上位移，时长0.5s
     * 2. 柱子组二（2，5，8，11）：delay 0.05s 同上
     * 3. 柱子组三（3，7，10，12）：delay 0.05s 同上
     */
    private void columnAnimStart() {


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.optionIv1.setVisibility(View.VISIBLE);
                mBinding.optionIv2.setVisibility(View.VISIBLE);
                mBinding.optionIv3.setVisibility(View.VISIBLE);
                mBinding.optionIv4.setVisibility(View.VISIBLE);
                mBinding.optionIv5.setVisibility(View.VISIBLE);
                mBinding.optionIv6.setVisibility(View.VISIBLE);
                mBinding.optionIv7.setVisibility(View.VISIBLE);
                mBinding.optionIv8.setVisibility(View.VISIBLE);
                mBinding.optionIv9.setVisibility(View.VISIBLE);
                mBinding.optionIv10.setVisibility(View.VISIBLE);
                mBinding.optionIv11.setVisibility(View.VISIBLE);
                mBinding.optionIv12.setVisibility(View.VISIBLE);
            }
        }, 320);
        //1469
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setConlumnAnim(mBinding.optionIv1, 500, IMV_TOP_START, IMV_TOP_FINAL1);
                setConlumnAnim(mBinding.optionIv4, 500, IMV_TOP_START, IMV_TOP_FINAL4);
                setConlumnAnim(mBinding.optionIv6, 500, IMV_TOP_START, IMV_TOP_FINAL6);
                setConlumnAnim(mBinding.optionIv9, 500, IMV_TOP_START, IMV_TOP_FINAL9);
            }
        }, 320);
        //258,11
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setConlumnAnim(mBinding.optionIv2, 500, IMV_TOP_START, IMV_TOP_FINAL2);
                setConlumnAnim(mBinding.optionIv5, 500, IMV_TOP_START, IMV_TOP_FINAL5);
                setConlumnAnim(mBinding.optionIv8, 500, IMV_TOP_START, IMV_TOP_FINAL8);
                setConlumnAnim(mBinding.optionIv11, 500, IMV_TOP_START, IMV_TOP_FINAL11);
            }
        }, 325);
        //3710,12
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setConlumnAnim(mBinding.optionIv3, 500, IMV_TOP_START, IMV_TOP_FINAL3);
                setConlumnAnim(mBinding.optionIv7, 500, IMV_TOP_START, IMV_TOP_FINAL7);
                setConlumnAnim(mBinding.optionIv10, 500, IMV_TOP_START, IMV_TOP_FINAL10);
                setConlumnAnim(mBinding.optionIv12, 500, IMV_TOP_START, IMV_TOP_FINAL12);
            }
        }, 330);
    }

    /**
     * 背景动画
     */
    private void bgAnim() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.bottomLl.setVisibility(View.VISIBLE);
            }
        }, 10);

        handler.post(new Runnable() {
            @Override
            public void run() {
                int from = UIUtil.getScreenHeigh(mContext);
                setConlumnAnim(mBinding.bottomLl, 400, from, IMV_TOP_START);
            }
        });
    }

    /**
     * 进入按钮动画
     */
    private void enterAnim() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimation(mBinding.btSure);
            }
        }, 560);
        ;
    }

    public void startAnimation(View view) {
        @SuppressLint("ResourceType")
        Animator animator = AnimatorInflater.loadAnimator(getApplicationContext(), R.anim.optionenter_show);
        animator.setTarget(view);
        animator.start();
    }

    /**
     * 下面文字动画
     */
    private void txtAnim() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.bottomLl.setVisibility(View.VISIBLE);
            }
        }, 10);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.llLine1.setVisibility(View.VISIBLE);
                mBinding.llTxt1.setVisibility(View.VISIBLE);
            }
        }, 142);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.llLine2.setVisibility(View.VISIBLE);
                mBinding.tvTxt2.setVisibility(View.VISIBLE);
            }
        }, 220);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setConlumnAnim(mBinding.llLine1, 400, UIUtil.dip2px(mContext, 30), 0);
                setConlumnAnim(mBinding.llTxt1, 400, UIUtil.dip2px(mContext, 30), 0);
                setAlphaAnim(mBinding.llLine1, 400);
                setAlphaAnim(mBinding.llTxt1, 400);
            }
        }, 140);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setConlumnAnim(mBinding.llLine2, 400, UIUtil.dip2px(mContext, 104.8), UIUtil.dip2px(mContext, 74.8));
                setConlumnAnim(mBinding.tvTxt2, 400, UIUtil.dip2px(mContext, 103), UIUtil.dip2px(mContext, 73));
                setAlphaAnim(mBinding.llLine2, 400);
                setAlphaAnim(mBinding.tvTxt2, 400);
            }
        }, 220);
    }


}
