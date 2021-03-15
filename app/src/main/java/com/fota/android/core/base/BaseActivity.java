package com.fota.android.core.base;

/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.IConstant;
import com.fota.android.commonlib.app.delegate.ActivityDelegate;
import com.fota.android.commonlib.app.delegate.AppContextDelegate;
import com.fota.android.commonlib.app.delegate.AppContextDelegateImp;
import com.fota.android.commonlib.app.delegate.ApplicationDelegate;
import com.fota.android.commonlib.app.delegate.FragmentManagerDelegate;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.base.BaseView;
import com.fota.android.commonlib.base.MyActivityManager;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.cancel.RxApiManager;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.ScreenUtils;
import com.fota.android.commonlib.utils.ToastUitl;
import com.fota.android.core.anim.ViewWrapper;
import com.fota.android.core.base.proxy.BtbBaseProxy;
import com.fota.android.core.base.proxy.IBtbBaseProxy;
import com.fota.android.core.dialog.DialogModel;
import com.fota.android.core.dialog.DialogUtils;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventSubscriber;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.moudles.main.MainActivity;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.utils.StatusBarUtil;
import com.fota.android.utils.UserLoginUtil;
import com.fota.android.widget.TitleLayout;
import com.fota.android.widget.btbwidget.FotaButton;
import com.fota.android.widget.btbwidget.FotaTextWatch;
import com.gyf.barlibrary.ImmersionBar;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;


public class BaseActivity extends FragmentActivity
        implements IConstant,
        EventSubscriber, IBtbBaseProxy, BaseView, AppContextDelegate {

    protected TextView vTitle, vSubTitle;
    protected View vRight;
    protected FragmentManager fragmentManager;

    protected String mRequestCode;

    private LinearLayout layout;
    private LayoutInflater inflater;
    protected ImmersionBar mImmersionBar;
    private BtbBaseProxy proxy;
    private AppContextDelegate appContextDelegate;
    protected TitleLayout mTitleLayout;

    private Dialog mLoadingDialog;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        proxy = BtbBaseProxy.with(this);
        initData(getIntent().getExtras());
        MobclickAgent.onResume(this);
        switchLanguage();
        if (AppConfigs.getTheme() == AppConfigs.THEME_WHITE) {
            //默認是白天主題
            setTheme(R.style.AppTheme_White);
        } else {
            //否则是晚上主題
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        highApiEffects();
        inflater = LayoutInflater.from(getContext());
        fragmentManager = getSupportFragmentManager();
        proxy.onAttach();
        appContextDelegate = AppContextDelegateImp.create(this);
        mRequestCode = AppConfigs.getRequestCode();
        onDateBinding();
        onInitView(null);
        KeyBoardUtils.setupUISoftKeyBoardHideSystem(getWindow().getDecorView(), viewGroupFocused());//点击空白区域关闭软键盘
        MyActivityManager.getInstance().addActivity(this);
    }

    protected boolean viewGroupFocused() {
        return false;
    }


    /**
     * 创建布局
     */
    protected void onDateBinding() {

    }

    /**
     * @param view view
     */
    protected void onInitView(View view) {
        findViewById();
        initHeadLayout();
    }

    /**
     * 定制头部样式
     */
    protected void initHeadLayout() {
        if (mTitleLayout == null) {
            return;
        }
        mTitleLayout.setAppTitle(getAppTitle());
        mTitleLayout.setOnLeftButtonClickListener(new TitleLayout.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onLeftClick();
            }
        });
        StatusBarUtil.setPaddingSmart(getContext(), mTitleLayout);
    }

    private void onLeftClick() {
        onBackPressed();
    }

    /**
     */
    public String getAppTitle() {
        return "";
    }


    protected void findViewById() {
        mTitleLayout = findViewById(R.id.app_title_layout);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void highApiEffects() {
        getWindow().getDecorView().setFitsSystemWindows(true);
        //透明状态栏 @顶部
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏 @底部 这一句不要加，目的是防止沉浸式状态栏和部分底部自带虚拟按键的手机（比如华为）发生冲突，注释掉就好了
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    /**
     * 切换语言
     */
    private void switchLanguage() {
        Locale lacale = AppConfigs.getLanguege();
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(lacale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleList localeList = new LocaleList(lacale);
                LocaleList.setDefault(localeList);
                config.setLocales(localeList);
                getApplicationContext().createConfigurationContext(config);
            }
        } else {
            config.locale = lacale;
        }
        resources.updateConfiguration(config, dm);
    }

    protected Activity getActivity() {
        return BaseActivity.this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (proxy != null) {
            proxy.onStop();
        }

    }


    @Override
    public AppContextDelegate getAppContextDelegate() {
        return appContextDelegate;
    }

    @Override
    public BaseActivity getHoldingActivity() {
        return this;
    }

    @Override
    public ActivityDelegate getActivityDelegate() {
        return appContextDelegate.getActivityDelegate();
    }

    @Override
    public ApplicationDelegate getApplicationDelegate() {
        return appContextDelegate.getApplicationDelegate();
    }

    @Override
    public FragmentManagerDelegate getChildFragmentManagerDelegate(Fragment fragment) {
        return appContextDelegate.getChildFragmentManagerDelegate(fragment);
    }

    @Override
    public FragmentManagerDelegate getFragmentManagerDelegate() {
        return appContextDelegate.getFragmentManagerDelegate();
    }

    public void addFragment(Fragment fragment) {
        getFragmentManagerDelegate().addFragment(getFragmentContainerId(), fragment, true);
    }

    public void replaceFragment(Fragment fragment) {
        getFragmentManagerDelegate().replaceFragment(getFragmentContainerId(), fragment);
    }

    public void switchFragment(Fragment fragment) {
        getFragmentManagerDelegate().switchFragment(getFragmentContainerId(), fragment);
    }

    @Override
    protected void onDestroy() {
        if (proxy != null) {
            proxy.onDetach();
        }
        proxy = null;
        getApplicationDelegate().popTaskStack(this);
        appContextDelegate.destroy();
        if (eventEnable()) {
            EventWrapper.unregister(this);
        }

        //防止内存泄漏
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = null;
        MyActivityManager.getInstance().removeActivity(this);

        try {
            RxApiManager.get().cancel(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public boolean eventEnable() {
        return false;
    }

    @Override
    public void destroy() {

    }

    /**
     * 获取当前上下文
     */
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(createLanguageContext(newBase));
    }

    public static Context createLanguageContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context) {
        Resources resources = context.getResources();
        Locale locale = AppConfigs.getLanguege();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData(intent.getExtras());
    }

    protected static int NO_HEAD_ID = 1;
    protected static int DEFAULT_HEAD_ID = 0;

    /**
     * 添加头部页面
     *
     * @param layoutResID
     * @return
     */
    private View addHeadView(int layoutResID) {
        if (DEFAULT_HEAD_ID == layoutResID) {
            View actionBarView = inflater.inflate(R.layout.fragment_header_title, null);
            return actionBarView;
        } else if (NO_HEAD_ID == layoutResID) {
            return null;
        } else {
            return inflater.inflate(layoutResID, null);
        }
    }

    protected void setContentView(View bodyView, int headlayoutResID) {
        layout = new LinearLayout(this);
        layout.setBackgroundColor(Pub.getColor(getContext(), R.attr.bg_color));
        layout.setOrientation(LinearLayout.VERTICAL);
        View headView = addHeadView(headlayoutResID);
        if (headView != null) {
            layout.addView(headView, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dipToPx(getContext(), 50)));
        }

        layout.addView(bodyView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(layout);
    }

    @Override
    protected void onResume() {
        if (proxy != null) {
            proxy.onResume();
        }
        KeyBoardUtils.closeKeybord(getContext());
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (proxy != null) {
            proxy.onPause();
        }
        MobclickAgent.onPause(this);
        super.onPause();
    }

    protected void setContentView(View bodyView, View view) {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        if (view != null) {
            layout.addView(view, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        layout.addView(bodyView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(layout);
    }

    /**
     * 根据相应的id创建页面
     *
     * @param bodyLayoutResID
     * @param headlayoutResID
     */
    protected void setContentView(int bodyLayoutResID, int headlayoutResID) {
        View bodyView = inflater.inflate(bodyLayoutResID, null);
        setContentView(bodyView, headlayoutResID);
    }

    /**
     * Show toast .
     *
     * @param s s
     */
    public final void showToast(String s) {
        if (!TextUtils.isEmpty(s)) {
            ToastUitl.showShort(s);
        }
    }

    /**
     * Show toast .
     */
    public final void showToast(@StringRes final int resId) {
        ToastUitl.showShort(resId);
    }

    /**
     *
     */
    protected void initData(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        getExtras(bundle);
        onInitData(bundle);
    }

    /**
     * 获取传过来的数据
     *
     * @param bundle
     */
    protected void getExtras(Bundle bundle) {
    }

    protected final <T> T getParams(String key) {
        if (getIntent() == null) {
            return null;
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || !bundle.containsKey(key)) {
            return null;
        }
        return (T) bundle.getSerializable(key);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initSystemBar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initSystemBar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initSystemBar();
    }

    protected void initSystemBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        mImmersionBar.statusBarDarkFont(true, 0.2f);
//        mImmersionBar.init();
        if (AppConfigs.getTheme() == 0) {
//            mImmersionBar.statusfon(true, 0.2f);
            mImmersionBar.statusBarDarkFont(false, 0.2f);
        } else {//白色主题设置黑色状态栏字体
            mImmersionBar.statusBarDarkFont(true, 0.2f);
        }
        mImmersionBar.init();
    }


    /**
     * @param bundle
     */
    protected void onInitData(Bundle bundle) {

    }

    @Override
    public void finish() {
        KeyBoardUtils.closeKeybord(getContext());
        super.finish();
    }

    //判断当前的主界面是否存在
    @SuppressWarnings("unused")
    boolean haveMain() {
        return isActivityExists(MainActivity.class);
    }

    @SuppressWarnings("deprecation")
    boolean isActivityExists(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        ComponentName cmpName = intent.resolveActivity(getPackageManager());
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    return true;
                }
            }
        }
        return false;
    }

    //activity的这套题
    public void setTitle(String title) {
        if (vTitle != null) {
            vTitle.setText(title);
        }
    }

    @SuppressWarnings("unused")
    public void setSubTitle(String viceTitle) {
        if (vSubTitle != null) {
            vSubTitle.setText(viceTitle);
            vSubTitle.setVisibility(View.VISIBLE);
        }
    }

    public void setRightVisibility(boolean visible) {
        if (vRight == null) {
            return;
        }
        if (visible) {
            vRight.setVisibility(View.VISIBLE);
        } else {
            vRight.setVisibility(View.GONE);
        }
    }

    protected int getContainerId() {
        return R.id.fragment_container;
    }

    protected BaseFragment addFragment(int id, BaseFragment fragment, boolean isAnim) {
        getFragmentManagerDelegate().addFragment(id, fragment, isAnim);
        return fragment;
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @Override
    public void onEventAsync(Event event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onEventMainThread(Event event) {

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    @Override
    public void onEventBackgroundThread(Event event) {

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {

    }

    /**
     * 通知其他界面刷新
     *
     * @param action
     */
    protected void notify(int action) {
        proxy.notify(action);
    }

    @Override
    public String getXmlString(int id) {
        if (getResources() == null) {
            return "";
        }
        return getResources().getString(id);
    }

    @Override
    public void onBackPressed() {
        boolean handled = false;
        List<Fragment> fragments = getFragmentManagerDelegate().fragmentManager.getFragments();
        if (null != fragments) {
            for (int i = 0; i < fragments.size(); i++) {
                if (fragments.get(i) instanceof BackPressedHandler) {
                    if (((BackPressedHandler) fragments.get(i)).onBackHandled()) {
                        handled = true;
                        break;
                    }
                }
            }
        }
        if (!handled) {
            if (getContext() == null || isFinishing())
                return;
            if (checkFinishSlef()) {
                finish();
            } else {
                super.onBackPressed();
            }
            //checkFinishSlef();
        }
    }


    public boolean checkFinishSlef() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0 && finishWhenNoFragment()) {
            return true;
        }
        return false;
    }

    public boolean finishWhenNoFragment() {
        return false;
    }

    /**
     * @desc 调用{@link #replaceFragment(Fragment)}和{@link #addFragment(Fragment)}
     * 时,会使用此方法返回的id
     * <p/>
     */
    @Deprecated
    public int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    public void doAnim(View view, String po, int value) {
        ViewWrapper viewWrapper = new ViewWrapper(view);
        ObjectAnimator.ofInt(viewWrapper, po, value).setDuration(1500).start();
    }


    @Override
    public void showdata() {

    }

    @Override
    public void showNoNetWork() {

    }

    @Override
    public void showNoData() {

    }

    @Override
    public void showFailer(String msg, ApiException e) {
        if (!TextUtils.isEmpty(msg))
            showToast(msg);
    }

    private View[] mViews;
    private FotaButton btSure;

    public void bindValid(FotaButton btSure, View... views) {
        this.mViews = views;
        this.btSure = btSure;
        if (views == null && views.length == 0) {
            return;
        }
        for (int i = 0; i < views.length; i++) {
            if (views[i] instanceof TextView) {
                ((TextView) views[i]).addTextChangedListener(new FotaTextWatch() {
                    @Override
                    protected void onTextChanged(String s) {
                        valid();
                    }
                });
            }

            if (views[i] instanceof CheckBox) {
                ((CheckBox) views[i]).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        valid();
                    }
                });
            }
        }
        valid();
    }


    /**
     * 验证
     *
     * @return
     */
    final public void valid() {
        btSure.setBtbEnabled(systemValid() && customerValid());
    }

    /**
     * 系统认证
     *
     * @return
     */
    private boolean systemValid() {
        if (mViews == null || mViews.length == 0) {
            return true;
        }
        for (int i = 0; i < mViews.length; i++) {
            //只有可见的 才验证
            if (mViews[i].getVisibility() == View.VISIBLE) {
                if (mViews[i] instanceof TextView && !(mViews[i] instanceof CheckBox)) {
                    String text = ((TextView) mViews[i]).getText().toString().trim();
                    if (TextUtils.isEmpty(text)) {
                        return false;
                    }
                }

                if (mViews[i] instanceof CheckBox) {
                    boolean checked = ((CheckBox) mViews[i]).isChecked();
                    if (!checked) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 开启浮动加载进度条
     */
    @Override
    public void startProgressDialog() {
        if (isFinishing())
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed()) {
                return;
            }
        }

        if (null != mLoadingDialog) {
            if (!mLoadingDialog.isShowing())
                mLoadingDialog.show();
        } else {
            initLoadingDialog();
            if (null != mLoadingDialog && !mLoadingDialog.isShowing()) {
                mLoadingDialog.show();
            }
        }
    }

    /**
     * 停止浮动加载进度条
     */
    @Override
    public void stopProgressDialog() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (isDestroyed()) {
                    return;
                }
            }
            mLoadingDialog.cancel();
        }
    }

    /**
     * 用户补充认证
     *
     * @return
     */
    protected boolean customerValid() {
        return true;
    }

    @Override
    public void notifyFromPresenter(int action) {

    }

    @Override
    public void notifyFromPresenter(int action, String data) {

    }

    private void initLoadingDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
//        TextView loadingText = (TextView) view.findViewById(R.id.id_tv_loading_dialog_text);
//        loadingText.setText("加载中...");
        mLoadingDialog = new Dialog(this, R.style.CustomProgressDialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void aotoLoginFromReq() {
        if (UserLoginUtil.havaUser()) {
            DialogUtils.showDialog(getContext(), new DialogModel()
                            //.setView(DialogUtils.getDefaultStyleMsgTV(getContext(), getString(R.string.mine_logout)))
                            .setMessage(this.getResources().getString(R.string.login_tokenerror))
                            .setSureText(getString(R.string.login_tokenerror_login))
                            .setCancelText(getString(R.string.cancel))
                            .setCancelable(false)
                            .setCanCancelOnTouchOutside(false)
                            .setSureClickListen(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                getPresenter().logOut();
                                    dialogInterface.dismiss();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("toLogin", true);
                                    FtRounts.toMain(getContext(), ConstantsPage.MainActivity, bundle);
                                }
                            }).setCancelClickListen(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                getPresenter().logOut();
                                    dialogInterface.dismiss();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("toLogin", false);
                                    FtRounts.toMain(getContext(), ConstantsPage.MainActivity, bundle);
                                }
                            })
            );
        }
        UserLoginUtil.delUser();
//        FtRounts.toLogin(getContext(), ConstantsPage.HomeFragment);


    }


    /**
     * 选中输入框下划线加粗
     *
     * @param editText
     * @param lineView
     */
    public void edtFocus(EditText editText, final View lineView) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    lineView.setBackgroundColor(Pub.getColor(getContext(), R.attr.line_color_focus));
                } else {
                    lineView.setBackgroundColor(Pub.getColor(getContext(), R.attr.line_color));
                }
            }
        });
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//
//            View v = getCurrentFocus();
//
//            if (isShouldHideKeyboard(v, ev)) {
//
//                v.clearFocus();//清除Edittext的焦点从而让光标消失
//
//                hideKeyboard(v.getWindowToken()); 
//
//            }
//
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    /**
     * 输入框密码隐藏
     *
     * @param editText
     * @param checkBox
     */
    public void edtHide(final EditText editText, CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                // 光标的位置
                int index = editText.getText().toString().length();
                editText.setSelection(index);
            }
        });
    }

    @Override
    public Object getCancelTag() {
        return this;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    /**
     * 设置状态栏只有白色字体
     */
    public void setJustWhiteBarTxt() {
        if (mImmersionBar == null)
            return;
        mImmersionBar.statusBarDarkFont(false, 0.2f);
        mImmersionBar.statusBarColor(android.R.color.transparent);
        mImmersionBar.init();
    }

    /**
     * 使子fragment可以获取activityresult结果，分享使用
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            if (fm.getFragments() == null || index < 0
                    || index >= fm.getFragments().size()) {
//                Log.w(TAG, "Activity result fragment index out of range: 0x"
//                        + Integer.toHexString(requestCode));
                return;
            }
            Fragment frag = fm.getFragments().get(index);
            if (frag == null) {
//                Log.w(TAG, "Activity result no fragment exists for index: 0x"
//                        + Integer.toHexString(requestCode));
            } else {
                handleResult(frag, requestCode, resultCode, data);
            }
            return;
        }

    }

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleResult(Fragment frag, int requestCode, int resultCode,
                              Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }


}
