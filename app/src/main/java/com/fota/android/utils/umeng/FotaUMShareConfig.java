package com.fota.android.utils.umeng;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.Context;
import android.text.TextUtils;

import com.umeng.socialize.Config;
import com.umeng.socialize.interfaces.CompressListener;
import com.umeng.socialize.uploadlog.UMLog;
import com.umeng.socialize.utils.ContextUtil;

public final class FotaUMShareConfig {
    public boolean isNeedAuthOnGetUserInfo;
    public static final int LINED_IN_BASE_PROFILE = 0;
    public static final int LINED_IN_FULL_PROFILE = 1;
    private int linkedInProfileScope;
    private boolean isOpenShareEditActivity = true;
    private String platformName = "";
    private boolean isHideQzoneOnQQFriendList;
    private boolean isOpenWXAnalytics = false;
    public static final int LINKED_IN_FRIEND_SCOPE_ANYONE = 0;
    public static final int LINKED_IN_FRIEND_SCOPE_CONNECTIONS = 1;
    private int linkedInFriendScope;
    private CompressListener compressListener;
    public static final int AUTH_TYPE_SSO = 1;
    public static final int AUTH_TYPE_WEBVIEW = 2;
    private int sinaAuthType;
    private int facebookAuthType;
    public static final int KAKAO_TALK = 0;
    public static final int KAKAO_STORY = 1;
    public static final int KAKAO_ACCOUNT = 2;
    private int kakaoAuthType;

    public FotaUMShareConfig() {
        this.setShareToLinkedInFriendScope(Config.LinkedInShareCode);
        this.setShareToQQFriendQzoneItemHide(Config.QQWITHQZONE == 2);
        this.setShareToQQPlatformName(Config.appName);
        this.setSinaAuthType(1);
        this.setFacebookAuthType(1);
        this.setKaKaoAuthType(Config.KaKaoLoginType);
        this.isNeedAuthOnGetUserInfo(Config.isNeedAuth);
        this.setLinkedInProfileScope(Config.LinkedInProfileScope);
    }

    public FotaUMShareConfig isNeedAuthOnGetUserInfo(boolean var1) {
        this.isNeedAuthOnGetUserInfo = var1;
        return this;
    }

    public FotaUMShareConfig setLinkedInProfileScope(int var1) {
        if (var1 == 0 || var1 == 1) {
            this.linkedInProfileScope = var1;
        }

        return this;
    }

    public void setOpenWXAnalytics(boolean var1) {
        this.isOpenWXAnalytics = var1;
    }

    public boolean getOpenWXAnalytics() {
        return this.isOpenWXAnalytics;
    }

    public FotaUMShareConfig isOpenShareEditActivity(boolean var1) {
        this.isOpenShareEditActivity = var1;
        UMLog.setIsOpenShareEdit(var1);
        return this;
    }

    public FotaUMShareConfig setShareToQQPlatformName(String var1) {
        if (!TextUtils.isEmpty(var1)) {
            this.platformName = var1;
        }

        return this;
    }

    public FotaUMShareConfig setShareToQQFriendQzoneItemHide(boolean var1) {
        this.isHideQzoneOnQQFriendList = var1;
        return this;
    }

    public FotaUMShareConfig setShareToLinkedInFriendScope(int var1) {
        if (var1 == 0 || var1 == 1) {
            this.linkedInFriendScope = var1;
        }

        return this;
    }

    public FotaUMShareConfig setSinaAuthType(int var1) {
        if (var1 == 1 || var1 == 2) {
            this.sinaAuthType = var1;
        }

        return this;
    }

    public FotaUMShareConfig setFacebookAuthType(int var1) {
        if (var1 == 1 || var1 == 2) {
            this.facebookAuthType = var1;
        }

        return this;
    }

    public FotaUMShareConfig setKaKaoAuthType(int var1) {
        if (var1 == 0 || var1 == 2 || var1 == 1) {
            this.kakaoAuthType = var1;
        }

        return this;
    }

    public final String getAppName() {
        if (TextUtils.isEmpty(this.platformName)) {
            Context var1 = ContextUtil.getContext();
            if (var1 != null) {
                CharSequence var2 = var1.getApplicationInfo().loadLabel(var1.getPackageManager());
                if (!TextUtils.isEmpty(var2)) {
                    this.platformName = var2.toString();
                }
            }
        }

        return this.platformName;
    }

    public final boolean isHideQzoneOnQQFriendList() {
        return this.isHideQzoneOnQQFriendList;
    }

    public final boolean isLinkedInShareToAnyone() {
        return this.linkedInFriendScope == 0;
    }

    public final boolean isLinkedInProfileBase() {
        return this.linkedInProfileScope == 0;
    }

    public final boolean isKakaoAuthWithTalk() {
        return this.kakaoAuthType == 0;
    }

    public final boolean isKakaoAuthWithStory() {
        return this.kakaoAuthType == 1;
    }

    public final boolean isKakaoAuthWithAccount() {
        return this.kakaoAuthType == 2;
    }

    public final boolean isSinaAuthWithWebView() {
        return this.sinaAuthType == 2;
    }

    public final boolean isFacebookAuthWithWebView() {
        return this.facebookAuthType == 2;
    }

    public final boolean isNeedAuthOnGetUserInfo() {
        return this.isNeedAuthOnGetUserInfo;
    }

    public void setCompressListener(CompressListener var1) {
        this.compressListener = var1;
    }

    public CompressListener getCompressListener() {
        return this.compressListener;
    }

    public final boolean isOpenShareEditActivity() {
        return this.isOpenShareEditActivity;
    }
}

