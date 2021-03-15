package com.fota.android.utils.umeng;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.graphics.Color;
import android.text.TextUtils;
import android.widget.PopupWindow.OnDismissListener;

import com.umeng.socialize.utils.ShareBoardlistener;

public class FotaShareBoardConfig {
    static int SHAREBOARD_POSITION_TOP = 1;
    public static int SHAREBOARD_POSITION_CENTER = 2;
    public static int SHAREBOARD_POSITION_BOTTOM = 3;
    public static int BG_SHAPE_NONE = 0;
    public static int BG_SHAPE_CIRCULAR = 1;
    public static int BG_SHAPE_ROUNDED_SQUARE = 2;
    boolean mTitleVisibility;
    String mTitleText;
    int mTitleTextColor;
    boolean mCancelBtnVisibility;
    String mCancelBtnText;
    int mCancelBtnColor;
    int mCancelBtnBgColor;
    int mCancelBtnBgPressedColor;
    int mShareboardPosition;
    int mShareboardBgColor;
    int mMenuBgShape;
    int mMenuBgShapeAngle;
    int mMenuBgColor;
    int mMenuBgPressedColor;
    int mMenuTextColor;
    int mMenuIconPressedColor;
    int mTopMargin;
    static final int CENTER_MENU_LEFT_PADDING = 36;
    static final int TITLE_TEXT_SIZE_IN_SP = 16;
    static final int TITLE_TOP_MARGIN = 20;
    static final int MENU_TOP_MARGIN = 20;
    static final int VIEW_PAGER_LEFT_MARGIN = 10;
    static final int MENU_ROW_NUM = 2;
    int mMenuColumnNum = 5;
    private static final int MENU_COLUMN_NUM = 4;
    private static final int MENU_COLUMN_NUM_CENTER = 3;
    private static final int MENU_COLUMN_NUM_HORIZONTAL = 6;
    private static final int MENU_COLUMN_NUM_HORIZONTAL_CENTER = 5;
    static final int MENU_ROW_MARGIN = 20;
    static final int INDICATOR_BOTTOM_MARGIN = 20;
    static final int INDICATOR_SIZE = 3;
    static final int INDICATOR_SPACE = 5;
    boolean mIndicatorVisibility;
    int mIndicatorNormalColor;
    int mIndicatorSelectedColor;
    static final int CANCEL_BTN_HEIGHT = 50;
    static final int CANCEL_BTN_TEXT_SIZE_IN_SP = 15;
    private ShareBoardlistener mShareBoardlistener;
    private OnDismissListener mOnDismissListener;

    public FotaShareBoardConfig() {
        this.setDefaultValue();
    }

    private void setDefaultValue() {
        int var1 = Color.parseColor("#575A5C");
        String var2 = "#ffffff";
        String var3 = "#22000000";
        String var4 = "#E9EFF2";
        String var5 = "选择要分享到的平台";
        String var6 = "取消分享";
        this.setShareboardBackgroundColor(Color.parseColor(var4));
        this.setShareboardPostion(SHAREBOARD_POSITION_BOTTOM);
        this.setTitleText(var5);
        this.setTitleTextColor(var1);
        byte var7 = 5;
        this.setMenuItemBackgroundShape(BG_SHAPE_ROUNDED_SQUARE, var7);
        this.setMenuItemBackgroundColor(Color.parseColor(var2), Color.parseColor(var3));
        this.setMenuItemIconPressedColor(Color.parseColor(var3));
        this.setMenuItemTextColor(var1);
        this.setCancelButtonText(var6);
        this.setCancelButtonTextColor(var1);
        this.setCancelButtonBackground(Color.parseColor(var2), Color.parseColor(var3));
        this.setIndicatorColor(Color.parseColor("#C2C9CC"), Color.parseColor("#0086DC"));
    }

    public void setShareBoardlistener(ShareBoardlistener var1) {
        this.mShareBoardlistener = var1;
    }

    ShareBoardlistener getShareBoardlistener() {
        return this.mShareBoardlistener;
    }

    void setOrientation(boolean var1) {
        if (var1) {
            if (this.mShareboardPosition == SHAREBOARD_POSITION_BOTTOM) {
                this.mMenuColumnNum = 6;
            } else if (this.mShareboardPosition == SHAREBOARD_POSITION_CENTER) {
                this.mMenuColumnNum = 5;
            }
        } else if (this.mShareboardPosition == SHAREBOARD_POSITION_BOTTOM) {
            this.mMenuColumnNum = 4;
        } else if (this.mShareboardPosition == SHAREBOARD_POSITION_CENTER) {
            this.mMenuColumnNum = 3;
        }

    }

    public FotaShareBoardConfig setTitleVisibility(boolean var1) {
        this.mTitleVisibility = var1;
        return this;
    }

    public FotaShareBoardConfig setTitleText(String var1) {
        if (TextUtils.isEmpty(var1)) {
            this.setTitleVisibility(false);
        } else {
            this.setTitleVisibility(true);
            this.mTitleText = var1;
        }

        return this;
    }

    public FotaShareBoardConfig setTitleTextColor(int var1) {
        this.mTitleTextColor = var1;
        return this;
    }

    public FotaShareBoardConfig setCancelButtonVisibility(boolean var1) {
        this.mCancelBtnVisibility = var1;
        return this;
    }

    public FotaShareBoardConfig setCancelButtonText(String var1) {
        if (TextUtils.isEmpty(var1)) {
            this.setCancelButtonVisibility(false);
        } else {
            this.setCancelButtonVisibility(true);
            this.mCancelBtnText = var1;
        }

        return this;
    }

    public FotaShareBoardConfig setCancelButtonTextColor(int var1) {
        this.mCancelBtnColor = var1;
        return this;
    }

    public FotaShareBoardConfig setCancelButtonBackground(int var1) {
        this.setCancelButtonBackground(var1, 0);
        return this;
    }

    public FotaShareBoardConfig setCancelButtonBackground(int var1, int var2) {
        this.mCancelBtnBgColor = var1;
        this.mCancelBtnBgPressedColor = var2;
        return this;
    }

    public FotaShareBoardConfig setShareboardBackgroundColor(int var1) {
        this.mShareboardBgColor = var1;
        return this;
    }

    public FotaShareBoardConfig setShareboardPostion(int var1) {
        if (var1 != SHAREBOARD_POSITION_BOTTOM && var1 != SHAREBOARD_POSITION_CENTER && var1 != SHAREBOARD_POSITION_TOP) {
            var1 = SHAREBOARD_POSITION_BOTTOM;
        }

        this.mShareboardPosition = var1;
        return this;
    }

    public FotaShareBoardConfig setMenuItemBackgroundShape(int var1) {
        this.setMenuItemBackgroundShape(var1, 0);
        return this;
    }

    public FotaShareBoardConfig setMenuItemBackgroundShape(int var1, int var2) {
        if (var1 != BG_SHAPE_CIRCULAR && var1 != BG_SHAPE_ROUNDED_SQUARE) {
            var1 = BG_SHAPE_NONE;
        }

        this.mMenuBgShape = var1;
        this.mMenuBgShapeAngle = var2;
        return this;
    }

    public FotaShareBoardConfig setMenuItemBackgroundColor(int var1) {
        this.setMenuItemBackgroundColor(var1, 0);
        return this;
    }

    public FotaShareBoardConfig setMenuItemBackgroundColor(int var1, int var2) {
        this.mMenuBgColor = var1;
        this.mMenuBgPressedColor = var2;
        return this;
    }

    public FotaShareBoardConfig setMenuItemTextColor(int var1) {
        this.mMenuTextColor = var1;
        return this;
    }

    public FotaShareBoardConfig setMenuItemIconPressedColor(int var1) {
        this.mMenuIconPressedColor = var1;
        return this;
    }

    public FotaShareBoardConfig setIndicatorColor(int var1) {
        this.setIndicatorColor(var1, 0);
        return this;
    }

    public FotaShareBoardConfig setIndicatorColor(int var1, int var2) {
        if (var1 != 0) {
            this.mIndicatorNormalColor = var1;
        }

        if (var2 != 0) {
            this.mIndicatorSelectedColor = var2;
        }

        this.setIndicatorVisibility(true);
        return this;
    }

    public FotaShareBoardConfig setIndicatorVisibility(boolean var1) {
        this.mIndicatorVisibility = var1;
        return this;
    }

    public FotaShareBoardConfig setOnDismissListener(OnDismissListener var1) {
        this.mOnDismissListener = var1;
        return this;
    }

    OnDismissListener getOnDismissListener() {
        return this.mOnDismissListener;
    }

    public FotaShareBoardConfig setStatusBarHeight(int var1) {
        this.mTopMargin = var1;
        return this;
    }

    int calculateMenuHeightInDp(int var1) {
        byte var2 = 75;
        byte var3 = 20;
        byte var4 = 20;
        byte var5;
        if (var1 <= this.mMenuColumnNum) {
            var5 = 1;
        } else if (var1 <= this.mMenuColumnNum * 2) {
            var5 = 2;
        } else {
            var5 = 2;
        }

        int var6 = var2 * var5 + var3 * (var5 - 1) + var4;
        return var6;
    }
}

