/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fota.android.moudles.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.app.IntentExtra;
import com.fota.android.common.bean.WebToNativeEntity;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;
import com.fota.android.core.base.BaseFragment;
import com.fota.android.moudles.mine.bean.CommissionBean;
import com.fota.android.moudles.mine.bean.CommissionUrlBean;
import com.fota.android.moudles.mine.bean.CommissionUserInfoBean;
import com.fota.android.moudles.mine.bean.CommissionUserInfoBeanItem;
import com.fota.android.moudles.mine.commission.CommissionPopup;
import com.fota.android.utils.DeviceUtils;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.umeng.socialize.UMShareAPI;

import java.util.Map;

/**
 * @author Luki  web页面的fragment
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class WebPowerfulFragment extends BaseFragment {
    protected com.github.lzyzsd.jsbridge.BridgeWebView vWebView;
    protected ProgressBar progressBar;
    protected String mTitle;
    protected String mData;
    protected WebToNativeEntity webToNativeEntity;
    protected final String GETTOKEN = "gettoken";//获取token
    protected String version;

    /* (non-Javadoc)
     * @see cn.stlc.app.BaseFragment#onInitData(android.os.Bundle)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
        mTitle = bundle.getString(IntentExtra.TITLE);
        mData = bundle.getString(IntentExtra.DATA);
    }

    /* (non-javadoc)
     * @see cn.stlc.app.basefragment#oncreatefragmentview(android.view.layoutinflater, android.view.viewgroup, android.os
     * .bundle)
     */
    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewDataBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_web, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        if (AppConfigs.dataChanged(getDataStatue()) && isSelected) {
            vWebView.reload();
        }
        setDataStatue(AppConfigs.getAppFlag());
        super.onResume();
    }

    /* (non-Javadoc)
     * @see cn.stlc.app.BaseFragment#onInitView(android.view.View)
     */
    @SuppressLint({"SetJavaScriptEnabled"})
    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        vWebView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressbar);
        WebSettings settings = vWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);// 设置支持缩放
        settings.setSupportZoom(false);// 不支持缩放
        settings.setUseWideViewPort(false);// 将图片调整到适合webview大小
        settings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        //        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//支持缓存
        settings.setDomStorageEnabled(true);
        String ua = settings.getUserAgentString();

        //更改userAgent，增加app的版本信息
        try {
            version = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String newUserAgent = ua + " |fota/" + version;
        Log.e("newUserAgent", newUserAgent);
        settings.setUserAgentString(newUserAgent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 让网页自适应屏幕宽度
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //允许h5 界面Http 和 https 链接混用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //title设置
        vWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title) && TextUtils.isEmpty(mTitle)) {
//                    mTitle = title;
                    setTitle(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    //加载完毕进度条消失
                    progressBar.setVisibility(View.GONE);
                } else {
                    //更新进度
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        if (mData == null) {
            return;
        }
        mData = addThemelanguage(mData);
        mData = addHttp(mData);

        setCookies(mData);
//        vWebView.setDefaultHandler(new DefaultHandler());
        vWebView.loadUrl(mData);
        registerHandler();
        if (TextUtils.isEmpty(mTitle))
            return;
        setTitle(mTitle);

    }

    private void registerHandler() {

        //获取客户端登录信息
        vWebView.registerHandler("getUserInfo", new BridgeHandler() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                CommissionUserInfoBean commissionUserInfoBean = new CommissionUserInfoBean();
                commissionUserInfoBean.setCode("success");
                if (UserLoginUtil.havaUser()) {
                    CommissionUserInfoBeanItem commissionUserInfoBeanItem = new CommissionUserInfoBeanItem();
                    commissionUserInfoBeanItem.setPhoneCountryCode(UserLoginUtil.getPhoneCountryCode());
                    commissionUserInfoBeanItem.setRegisterType(UserLoginUtil.getRegistType());
                    commissionUserInfoBeanItem.setToken(UserLoginUtil.getToken());
                    commissionUserInfoBeanItem.setNickName(commissionUserInfoBeanItem.getNickName());
                    commissionUserInfoBeanItem.setUdid(DeviceUtils.getUniqueId(FotaApplication.getInstance()));
                    commissionUserInfoBean.setCommissionUserInfoBeanItem(commissionUserInfoBeanItem);
                } else {
                    commissionUserInfoBean.setCommissionUserInfoBeanItem(null);
                }


                String json = new Gson().toJson(commissionUserInfoBean);
                L.a("json = " + json);
                function.onCallBack(json);
            }

        });
        //h5点击生成专属图唤起客户端邀请弹框
        vWebView.registerHandler("createInviteDialog", new BridgeHandler() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                L.a("handler = createInviteDialog, data from web = " + data);
                Map<String, String> map = new ArrayMap<>();
                map.put("code", "success");
                String json = StringFormatUtils.mapToJson(map);


                function.onCallBack(json);

                CommissionBean commissionBean = new Gson().fromJson(data, CommissionBean.class);
                if (commissionBean == null)
                    return;
                CommissionPopup commissionPopup = new CommissionPopup(getActivity(), commissionBean);
                commissionPopup.setAnimationStyle(R.style.mypopwindow_anim_style);
                commissionPopup.show();


            }

        });
        //h5跳转客户端页面
        vWebView.registerHandler("jumpToNative", new BridgeHandler() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handler(String data, CallBackFunction function) {
                L.a("handler = jumpToNative, data from web = " + data);
                Map<String, String> map = new ArrayMap<>();
                map.put("code", "success");
                String json = StringFormatUtils.mapToJson(map);


                function.onCallBack(json);

                CommissionUrlBean urlBean = new Gson().fromJson(data, CommissionUrlBean.class);
                if (urlBean == null || TextUtils.isEmpty(urlBean.getUrl()))
                    return;
                FtRounts.getPageFromH5(mContext, urlBean.getUrl());
//                FtRounts.getPageFromH5(getContext(), "fota://goto/login");
            }

        });

    }


    private void setCookies(String data) {

        if (TextUtils.isEmpty(data)) {
            return;
        }
        synCookies(data, "locale=" + AppConfigs.getLanguegeReqString());
    }

    /**
     * 设置Cookie
     *
     * @param url
     * @param cookie 格式：uid=21233 如需设置多个，需要多次调用
     */
    public void synCookies(String url, String cookie) {
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookie);//cookies是在HttpClient中获得的cookie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }

    }

    /**
     * 清除Cookie
     *
     * @param context
     */
    public static void removeCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

    @Override
    protected void onLeftClick() {
        if (vWebView.canGoBack()) {
            vWebView.goBack();//返回上一页面
        } else {
            super.onLeftClick();
        }
    }

    @Override
    public boolean onBackHandled() {
        if (vWebView.canGoBack()) {
            vWebView.goBack();//返回上一页面
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加主题和语言
     *
     * @param url
     * @return
     */
    private String addThemelanguage(String url) {
        if (TextUtils.isEmpty(url))
            return "";
        String language = AppConfigs.isChinaLanguage() ? "zh" : "en";
        String theme = AppConfigs.getTheme() == 0 ? "night" : "light";
        if (!url.contains("?")) {
            return url + "?lang=" + language + "&theme=" + theme;
        } else {
            return url + "&lang=" + language + "&theme=" + theme;
        }

    }

    //以下内容暂时没有调用

    /**
     * 判断不以http开头加http前缀
     *
     * @param url
     * @return
     */
    private String addHttp(String url) {
        if (TextUtils.isEmpty(url))
            return "";
        if (url.startsWith("http")) {
            return url;
        } else {
            return "http://" + url;
        }

    }

    /**
     * 拦截之后调用H5 Method
     *
     * @param webToNativeEntity
     * @param action
     */
    private void H5Method(WebToNativeEntity webToNativeEntity, String action) {
        //获取token
        if (action.equals(GETTOKEN) && webToNativeEntity.status == 1) {
            PushTokenToH5();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vWebView != null) {
            vWebView.destroy();
            vWebView = null;
        }
    }

    /**
     * Token
     */
    private void PushTokenToH5() {
        //调用H5方法传给H5
        String token = SharedPreferencesUtil.getInstance().get(SharedPreferencesUtil.Key.LOGIN_TOKEN, "");
        token = "{" + "token" + ":\"" + token + "\"}";
        LoaderJS(webToNativeEntity, token);
    }

    /**
     * 原生调js
     *
     * @param webToNativeEntity
     * @param data
     */
    private void LoaderJS(final WebToNativeEntity webToNativeEntity, final String data) {
        vWebView.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    vWebView.evaluateJavascript(webToNativeEntity.callback + "(" + "'" + data + "'" + ")", new
                            ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                }
                            });
                } else {
                    vWebView.loadUrl("javascript:" + webToNativeEntity.callback + "(" + "'" + data + "'" + ")");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
    }


}

