package com.fota.android.utils.apputils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.fota.android.R;
import com.fota.android.http.Http;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.utils.FileUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BtbMap;
import com.fota.android.moudles.main.BottomMenuItem;
import com.fota.android.moudles.main.BottomMenuItemInfo;
import com.fota.android.utils.DeviceUtils;
import com.fota.android.utils.UserLoginUtil;
import com.google.gson.Gson;

public class MenuUtils {

    public final static int MENU_HOME = 1001;
    public final static int MENU_MARKET = 1002;
    public final static int MENU_TRADE = 1003;
    public final static int MENU_USDK = 1004;
    public final static int MENU_ME = 1005;
    public final static int MENU_OPTION = 1007;


    public static void getAppBar(final Context content) {

        //下次生效
        String areas = FileUtils.ReadDayDayString(content, "menu.json");
        final BottomMenuItemInfo info = new Gson().fromJson(areas, BottomMenuItemInfo.class);
        //没有网络图片 直接保存
        SharedPreferencesUtil.getInstance().put(UserLoginUtil.FTKey.HOME_MENU,
                new Gson().toJson(info)
        );

        readNewInfo(info, content);

        BtbMap map = new BtbMap();
        map.p("version", DeviceUtils.getVersonName(FotaApplication.getInstance()));
        map.p("platform", "2");
        Http.getHttpService().appTabbar(map)
                .compose(new CommonTransformer<BottomMenuItemInfo>())
                .subscribe(new CommonSubscriber<BottomMenuItemInfo>() {
                    @Override
                    public void onNext(BottomMenuItemInfo info) {
                        readNewInfo(info, content);
                    }

                    @Override
                    protected void onError(ApiException e) {

                    }

                });
    }

    public static void readNewInfo(final BottomMenuItemInfo info, final Context content) {
        //下次生效
//        String areas = FileUtils.ReadDayDayString(content, "menu2.json");
//        final BottomMenuItemInfo info = new Gson().fromJson(areas, BottomMenuItemInfo.class);
        int count = 0;
        if (!Pub.isListExists(info.getTabbar())) {
            return;
        }
        for (BottomMenuItem menuItem : info.getTabbar()) {
            if (!Pub.isStringEmpty(menuItem.getIconUrlBlack())) {
                count += 4;
            }
        }
        //说明存在网络图片
        if (count > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean isOk = true;
                    try {
                        for (final BottomMenuItem menuItem : info.getTabbar()) {
                            if (!isOk) {
                                break;
                            }
                            if (content == null) {
                                return;
                            }

                            if (!TextUtils.isEmpty(menuItem.getIconUrlBlack())) {
                                Bitmap myBitmap = Glide.with(content)
                                        .asBitmap()
                                        .load(menuItem.getIconUrlBlack())
                                        .submit(UIUtil.dip2px(content, 17),
                                                UIUtil.dip2px(content, 21))
                                        .get();
                                Bitmap myBitmap2 = Glide.with(content)
                                        .asBitmap()
                                        .load(menuItem.getIconUrlWhite())
                                        .submit(UIUtil.dip2px(content, 17),
                                                UIUtil.dip2px(content, 21))
                                        .get();
                                Bitmap myBitmap3 = Glide.with(content)
                                        .asBitmap()
                                        .load(menuItem.getSelectIconUrlBlack())
                                        .submit(UIUtil.dip2px(content, 17),
                                                UIUtil.dip2px(content, 21))
                                        .get();
                                Bitmap myBitmap4 = Glide.with(content)
                                        .asBitmap()
                                        .load(menuItem.getSelectIconUrlWhite())
                                        .submit(UIUtil.dip2px(content, 17),
                                                UIUtil.dip2px(content, 21))
                                        .get();

                                if (myBitmap == null
                                        || myBitmap2 == null
                                        || myBitmap3 == null
                                        || myBitmap4 == null
                                        ) {
                                    isOk = false;
                                }
                            }
                        }
                    } catch (Exception e) {
                        isOk = false;
                        e.printStackTrace();
                    }
                    if (isOk) {
                        SharedPreferencesUtil.getInstance().put(UserLoginUtil.FTKey.HOME_MENU,
                                new Gson().toJson(info)
                        );
                    }
                }
            }).start();
        } else {
            //没有网络图片 直接保存
            SharedPreferencesUtil.getInstance().put(UserLoginUtil.FTKey.HOME_MENU,
                    new Gson().toJson(info)
            );
        }
    }


    public static int getCheckImage(BottomMenuItem model, int position) {
        boolean isWhite = AppConfigs.isWhiteTheme();
        switch (Pub.GetInt(model.getCode())) {
            case MENU_HOME:
                return R.mipmap.icon_home_fill;
            case MENU_MARKET:
                return isWhite ? R.mipmap.icon_market_fill_white : R.mipmap.icon_market_fill_black;
            case MENU_TRADE:
                return isWhite ? R.mipmap.icon_trade_fill_white : R.mipmap.icon_trade_fill_black;
            case MENU_USDK:
                return isWhite ? R.mipmap.icon_usdk_fill_white : R.mipmap.icon_usdk_fill_black;
            case MENU_ME:
                return isWhite ? R.mipmap.icon_me_fill_white : R.mipmap.icon_me_fill_black;
            case MENU_OPTION:
                return isWhite ? R.mipmap.icon_option_fill_white : R.mipmap.icon_option_fill_black;
        }
        return 0;
    }

    public static String getCheckImageString(BottomMenuItem model, int position) {
        boolean isWhite = AppConfigs.isWhiteTheme();
        if (!Pub.isStringEmpty(model.getIconUrlBlack())) {
            return isWhite ? model.getSelectIconUrlWhite() : model.getSelectIconUrlBlack();
        }
        return "";
    }


    /**
     * @param model
     * @param position
     * @return
     */
    public static int getImage(BottomMenuItem model, int position) {
        boolean isWhite = AppConfigs.isWhiteTheme();
        switch (Pub.GetInt(model.getCode())) {
            case MENU_HOME:
                return isWhite ? R.mipmap.icon_home_grey_white : R.mipmap.icon_home_grey;
            case MENU_MARKET:
                return isWhite ? R.mipmap.icon_market_grey_white : R.mipmap.icon_market_grey_black;
            case MENU_TRADE:
                return isWhite ? R.mipmap.icon_trade_grey_white : R.mipmap.icon_trade_grey_black;
            case MENU_USDK:
                return isWhite ? R.mipmap.icon_usdk_grey_white : R.mipmap.icon_usdk_grey_black;
            case MENU_ME:
                return isWhite ? R.mipmap.icon_me_grey_white : R.mipmap.icon_me_grey_black;
            case MENU_OPTION:
                return isWhite ? R.mipmap.icon_option_grey_white : R.mipmap.icon_option_grey_black;
        }
        return 0;
    }

    /**
     * @param model
     * @param position
     * @return
     */
    public static String getImageString(BottomMenuItem model, int position) {
        boolean isWhite = AppConfigs.isWhiteTheme();
        if (!Pub.isStringEmpty(model.getIconUrlBlack())) {
            return isWhite ? model.getIconUrlWhite() : model.getIconUrlBlack();
        }
        return "";
    }
}
