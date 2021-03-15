package com.fota.android.moudles.mine.commission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.ToastUitl;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.moudles.mine.bean.CommissionBean;
import com.fota.android.utils.ZXingUtils;
import com.fota.android.utils.umeng.FotaShareBoardConfig;
import com.fota.android.utils.umeng.FotaSocializeMenuAdapter;
import com.fota.android.utils.umeng.ShareHelperUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 返佣分享弹窗
 */
public class CommissionPopup extends PopupWindow implements View.OnClickListener {
    Activity context;
    LayoutInflater inflater;
    com.umeng.socialize.shareboard.widgets.SocializeViewPager viewpager;
    LinearLayout ll_shot;
    TextView btn_shot;
    ImageView imv_close;
    RelativeLayout rl_parent;
    CardView cv_code;
    ImageView imv_qrcode, imv_title;
    TextView tv_msg1;
    TextView tv_commissioncode;
    //    TextView tv_qrorvcode;
    TextView tv_user_title;
    CardView cv_btn;
    RelativeLayout rl_msg;
    LinearLayout ll_msg, ll_code, ll_qrcode;
    RelativeLayout rl_bg;
    TextView tv_commissioncode_qrcode,tv_title_user;

    private UMShareListener mShareListener;
    CommissionBean commissionBean;

    public CommissionPopup(final Activity context, CommissionBean commissionBean) {
        this.commissionBean = commissionBean;
        this.context = context;

        init();
    }

    private void init() {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_commission, null);
        viewpager = view.findViewById(R.id.viewpager);
        ll_shot = view.findViewById(R.id.ll_shot);
        btn_shot = view.findViewById(R.id.btn_shot);
        rl_parent = view.findViewById(R.id.rl_parent);
        imv_close = view.findViewById(R.id.imv_close);
        cv_code = view.findViewById(R.id.cv_code);
        imv_qrcode = view.findViewById(R.id.imv_qrcode);
        imv_title = view.findViewById(R.id.imv_title);
        tv_msg1 = view.findViewById(R.id.tv_msg1);
        tv_commissioncode = view.findViewById(R.id.tv_commissioncode);
//        tv_qrorvcode = view.findViewById(R.id.tv_qrorvcode);
        tv_user_title = view.findViewById(R.id.tv_user_title);
        cv_btn = view.findViewById(R.id.cv_btn);
        ll_msg = view.findViewById(R.id.ll_msg);
        rl_msg = view.findViewById(R.id.rl_msg);
        rl_bg = view.findViewById(R.id.rl_bg);
        ll_code = view.findViewById(R.id.ll_code);
        ll_qrcode = view.findViewById(R.id.ll_qrcode);
        tv_commissioncode_qrcode = view.findViewById(R.id.tv_commissioncode_qrcode);
        tv_title_user = view.findViewById(R.id.tv_title_user);
        btn_shot.setOnClickListener(this);
        rl_parent.setOnClickListener(this);
        imv_close.setOnClickListener(this);
        setContentView(view);
        setFocusable(true);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//        setFocusable(true);
//        setOutsideTouchable(false);
//        setClippingEnabled(false);
 
        initViewPager();
        if (AppConfigs.getLanguegeInt() == 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cv_btn.getLayoutParams();
            params.setMargins(0, 0, 0, UIUtil.dip2px(context, 40));
            cv_btn.setLayoutParams(params);
            imv_title.setImageResource(R.mipmap.commission_title_cn);
            tv_title_user.setTextSize(9.6f);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cv_btn.getLayoutParams();
            params.setMargins(0, 0, 0, UIUtil.dip2px(context, 30));
            cv_btn.setLayoutParams(params);
            imv_title.setImageResource(R.mipmap.commission_title_en);
            tv_title_user.setTextSize(17f);

        }

        if (commissionBean == null)
            return;
        tv_user_title.setText(commissionBean.getRegisterAccount());
        if (!commissionBean.getOriginal()) {
            ll_code.setVisibility(View.GONE);
            ll_qrcode.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(commissionBean.getUrl())) {
                Bitmap bitmap = null;
                bitmap = ZXingUtils.Create2DCode(commissionBean.getUrl(), UIUtil.dip2px(context, 90), UIUtil.dip2px(context, 90));
                imv_qrcode.setImageBitmap(bitmap);
            }
            if (!TextUtils.isEmpty(commissionBean.getInviteCode())) {
                tv_commissioncode_qrcode.setText(commissionBean.getInviteCode());
            }
//            tv_qrorvcode.setText(context.getResources().getString(R.string.commission_qrcode_spot));


        } else {
            ll_code.setVisibility(View.VISIBLE);
            ll_qrcode.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(commissionBean.getInviteCode())) {
                tv_commissioncode.setText(commissionBean.getInviteCode());
            }
//            tv_qrorvcode.setText(context.getResources().getString(R.string.commission_code_spot));

        }
        String commissionStr = commissionBean.getRebateRatioSum();
        String commissionFullStr = String.format(context.getResources().getString(R.string.commission_msg1), commissionStr);
//        ShareHelperUtil.checkSearchContent(tv_msg1, commissionStr, commissionFullStr);
        tv_msg1.setText(commissionFullStr);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                int shotHeigh = ll_msg.getMeasuredHeight();
                int parentHeigh = rl_msg.getMeasuredHeight();
                if (parentHeigh - shotHeigh > UIUtil.dip2px(context, 165)) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_bg.getLayoutParams();
                    params.topMargin = UIUtil.dip2px(context, 40);
                    params.bottomMargin = UIUtil.dip2px(context, 50);
                    rl_bg.setLayoutParams(params);
                } else if (parentHeigh - shotHeigh > UIUtil.dip2px(context, 145)) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_bg.getLayoutParams();
                    params.topMargin = UIUtil.dip2px(context, 30);
                    params.bottomMargin = UIUtil.dip2px(context, 40);
                    rl_bg.setLayoutParams(params);
                }
            }

        }, 100);


    }

    public void show() {
        if (this.isShowing()) {
            return;
        }
        this.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_close:
            case R.id.rl_parent:
                if (isShowing())
                    dismiss();
                break;
            case R.id.btn_shot:
                if (!permissionCheck())
                    return;
                Bitmap bitmap = ShareHelperUtil.createViewBitmap(ll_shot);
                ShareHelperUtil.saveImageToGallery(context, bitmap, "fota");
                break;
        }
    }

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<Activity> mActivity;

        private CustomShareListener(Activity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
//                Toast.makeText(platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST

                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
//                    ToastUitl.showLong("suc");
//                    Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                ToastUitl.showLong("fail");

//                Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUitl.showLong("cancel");

//            Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViewPager() {
        mShareListener = new CustomShareListener(context);
        FotaShareBoardConfig fotaShareBoardConfig = new FotaShareBoardConfig();
        fotaShareBoardConfig.setShareBoardlistener(new ShareBoardlistener() {
            @Override
            public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
//                if (!permissionCheck())
//                    return;
                if (share_media == SHARE_MEDIA.QQ || share_media == SHARE_MEDIA.QZONE || share_media == SHARE_MEDIA.FACEBOOK || share_media == SHARE_MEDIA.FACEBOOK_MESSAGER || share_media == SHARE_MEDIA.WHATSAPP || share_media == SHARE_MEDIA.TWITTER) {
                    if (!permissionCheck())
                        return;
                }
                if (share_media == SHARE_MEDIA.TWITTER) {
                    if (!UMShareAPI.get(context).isAuthorize(context, SHARE_MEDIA.TWITTER)) {
                        UMShareAPI.get(context).doOauthVerify(context, share_media, authListener);
                        return;
                    }
                }

                Bitmap bitmap = ShareHelperUtil.createViewBitmap(ll_shot);
                if (bitmap == null)
                    return;
                UMImage imagelocal = new UMImage(context, bitmap);
//                imagelocal.setThumb(new UMImage(context, R.drawable.thumb));
                new ShareAction(context).withMedia(imagelocal)
                        .setPlatform(share_media)
                        .setCallback(shareListener).share();

            }
        });
        fotaShareBoardConfig.setMenuItemBackgroundShape(FotaShareBoardConfig.BG_SHAPE_CIRCULAR);
        UMShareAPI umShareAPI = UMShareAPI.get(context);
        List<SHARE_MEDIA> medias = new ArrayList<>();
        if (umShareAPI.isInstall(context, SHARE_MEDIA.FACEBOOK)) {
            medias.add(SHARE_MEDIA.FACEBOOK);
        }
        if (CommonUtils.isAppAvilible(FotaApplication.getInstance(), "com.facebook.orca")) {
            medias.add(SHARE_MEDIA.FACEBOOK_MESSAGER);
        }
//        if (umShareAPI.isInstall(context, SHARE_MEDIA.TWITTER)) {
//            medias.add(SHARE_MEDIA.TWITTER);
//        }
        medias.add(SHARE_MEDIA.TWITTER);

        if (umShareAPI.isInstall(context, SHARE_MEDIA.WHATSAPP)) {
            medias.add(SHARE_MEDIA.WHATSAPP);
        }
        if (umShareAPI.isInstall(context, SHARE_MEDIA.WEIXIN)) {
            medias.add(SHARE_MEDIA.WEIXIN);
            medias.add(SHARE_MEDIA.WEIXIN_CIRCLE);
        }
        if (umShareAPI.isInstall(context, SHARE_MEDIA.SINA)) {
            medias.add(SHARE_MEDIA.SINA);
        }
        if (umShareAPI.isInstall(context, SHARE_MEDIA.QQ)) {
            medias.add(SHARE_MEDIA.QQ);
            medias.add(SHARE_MEDIA.QZONE);
        } else if (umShareAPI.isInstall(context, SHARE_MEDIA.QZONE)) {
            medias.add(SHARE_MEDIA.QZONE);
        }
        medias.add(SHARE_MEDIA.SMS);
        medias.add(SHARE_MEDIA.EMAIL);

        FotaSocializeMenuAdapter var12 = new FotaSocializeMenuAdapter(context, fotaShareBoardConfig, medias);
//        var12.setData(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
//                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ);
        viewpager.setAdapter(var12);
    }

    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
//            SocializeUtils.safeShowDialog(dialog);
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(context, "成功了", Toast.LENGTH_LONG).show();
//            SocializeUtils.safeCloseDialog(dialog);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            SocializeUtils.safeCloseDialog(dialog);
//            Toast.makeText(context, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            SocializeUtils.safeCloseDialog(dialog);
//            Toast.makeText(context, "取消了", Toast.LENGTH_LONG).show();

        }
    };

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
//            Log.d("state == ", "state ==onStart");
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
//            Log.d("state == ", "state ==onComplete");
            if (UMShareAPI.get(context).isAuthorize(context, SHARE_MEDIA.TWITTER)) {
                Bitmap bitmap = ShareHelperUtil.createViewBitmap(ll_shot);
                if (bitmap == null)
                    return;
                UMImage imagelocal = new UMImage(context, bitmap);
//                imagelocal.setThumb(new UMImage(context, R.drawable.thumb));
                new ShareAction(context).withMedia(imagelocal)
                        .setPlatform(SHARE_MEDIA.TWITTER)
                        .setCallback(shareListener).share();
            }
//            Toast.makeText(this, "成功了", Toast.LENGTH_LONG).show();
//            notifyDataSetChanged();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
//            Log.d("state == ", "state ==onError " + t.getMessage());
//            Toast.makeText(this, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
//            Log.d("state == ", "state ==onCancel");
//            Toast.makeText(this, "取消了", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 存储权限判断
     *
     * @return
     */
    private boolean permissionCheck() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)

        {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return true;
            } else {
                ToastUitl.showShort(R.string.commission_store_permission);
//                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                return false;
            }
        }
        return true;
    }

    public boolean checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }


}
