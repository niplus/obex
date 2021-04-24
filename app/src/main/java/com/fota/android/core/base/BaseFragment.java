
package com.fota.android.core.base;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.fota.android.R;
import com.fota.android.app.IConstant;
import com.fota.android.commonlib.app.delegate.ActivityDelegate;
import com.fota.android.commonlib.app.delegate.AppContextDelegate;
import com.fota.android.commonlib.app.delegate.AppContextDelegateImp;
import com.fota.android.commonlib.app.delegate.ApplicationDelegate;
import com.fota.android.commonlib.app.delegate.FragmentManagerDelegate;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.base.BaseView;
import com.fota.android.commonlib.base.INetWork;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.exception.ErrorType;
import com.fota.android.commonlib.http.rx.cancel.RxApiManager;
import com.fota.android.commonlib.utils.CommonUtils;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.ToastUitl;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.anim.ViewWrapper;
import com.fota.android.core.base.proxy.BtbBaseProxy;
import com.fota.android.core.base.proxy.IBtbBaseProxy;
import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventSubscriber;
import com.fota.android.core.event.EventWrapper;
import com.fota.android.utils.FtRounts;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.utils.StatusBarUtil;
import com.fota.android.widget.TitleLayout;
import com.fota.android.widget.btbwidget.FotaButton;
import com.fota.android.widget.btbwidget.FotaTextWatch;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Base Fragment
 * ��fragment
 *
 * @author Luki
 * @version 1.0 2014-1-3
 * @since 1.0
 */
public abstract class BaseFragment extends Fragment implements IConstant, EventSubscriber, IBtbBaseProxy, INetWork,
        BaseView, BackPressedHandler, AppContextDelegate {
    //jiang 1013
    //default is true，主要用于标记MainActivity下的几个基本Fragment是否处于选中并显示的状态
    //ExchangeFragment FutureFragment MarketFragemnt HomeFragment
    protected boolean isSelected = true;

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private int dataStatue;

    private static final String REFRESH_TAG = "REFRESH_TAG";
    private static final Handler handler = new Handler();

    private String TAG;
    protected Activity mActivity;
    protected Context mContext;

    private String mRefreshTag;

    protected String mRequestCode;

    private BtbBaseProxy proxy;
    private AppContextDelegate appContextDelegate;
    protected TitleLayout mTitleLayout;
    protected View emptyTopMargin;
//    private ImmersionBar mImmersionBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(getTAG(), "onCreate");
        dataStatue = AppConfigs.getAppFlag();
        mContext = mActivity = getActivity();
        String tag = getClass().getSimpleName();
        if (TextUtils.isEmpty(TAG)) {
            TAG = tag;
        }
        initData();
        mRequestCode = AppConfigs.getRequestCode();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        mRefreshTag = bundle.getString(REFRESH_TAG);
        mRefreshTag = mRefreshTag == null ? "" : mRefreshTag;
        onInitData(bundle);
    }

    /**
     * initialize data from last page
     *
     * @param bundle bundle
     */
    protected void onInitData(Bundle bundle) {
    }

    protected void onLeftClick() {
        if (mActivity != null) {
            mActivity.onBackPressed();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        View view = getView();
        if (view != null) {
            view.setClickable(true);
            onInitView(view);
        }
        KeyBoardUtils.setupUISoftKeyBoardHideSystem(this.getView(), viewGroupFocused());
        super.onActivityCreated(savedInstanceState);
    }

    protected boolean viewGroupFocused() {
        return false;
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(getTAG(), "onCreateView");
        View view;

        view = onCreateFragmentView(inflater, container, savedInstanceState);
        return view;
    }

    protected abstract View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


    public final <T extends View> T findViewById(int id) {
        if (getView() == null) {
            return null;
        } else {
            return (T) getView().findViewById(id);
        }

    }

    /**
     * @param view view
     */
    protected void onInitView(View view) {
        findViewById();
        initHeadLayout();
//        mImmersionBar = ImmersionBar.with(this);
    }

    protected void findViewById() {
        mTitleLayout = findViewById(R.id.app_title_layout);
    }

    /**
     * 定制头部样式
     */
    protected void initHeadLayout() {
        if (mTitleLayout == null) {
            return;
        }
        mTitleLayout.setAppTitle(setAppTitle());
        mTitleLayout.setOnLeftButtonClickListener(new TitleLayout.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onLeftClick();
            }
        });
        StatusBarUtil.setPaddingSmart(getContext(), mTitleLayout);
    }

    protected String setAppTitle() {
        return "";
    }

    /**
     * Show toast .
     *
     * @param s
     */
    public void showToast(String s) {
        if (!TextUtils.isEmpty(s)) {
            ToastUitl.showShort(s);
        }
    }

    /**
     * Show toast .
     */
    public final void showToast(@StringRes final int resId) {
        if (mContext == null)
            return;
        ToastUitl.showShort(mContext.getResources().getString(resId));
    }


    final String getTAG() {
        return TAG;
    }

    public void finish() {
        if (mActivity != null) {
            mActivity.onBackPressed();
        }
    }

    /**
     * Back to the specified tag page
     *
     * @param tag Null is back to the bottom of the stack.
     */
    @SuppressWarnings("unused")
    public final void popBackStackImmediate(String tag) {
        BaseFragment fragment = (BaseFragment) getFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            getFragmentManager().beginTransaction().show(fragment).commit();
        }
        getFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void postDelayed(Runnable r, long delay) {
        handler.postDelayed(r, delay);
    }

    @Override
    public void onDestroyView() {
        try {
            RxApiManager.get().cancel(this);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (proxy != null) {
            proxy.onStop();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appContextDelegate = AppContextDelegateImp.create(getActivity());
        proxy = BtbBaseProxy.with(getHoldingActivity(), this);
        proxy.onAttach();
    }


    @Override
    public void onDetach() {
        if (proxy != null) {
            proxy.onDetach();
        }
        proxy = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        if (proxy != null) {
            proxy.onResume();
        }
        super.onResume();
        /**
         * 系统状态改变就刷新
         */
        if (AppConfigs.dataChanged(dataStatue) && isSelected) {
            onLoginChanged();
        }
        dataStatue = AppConfigs.getAppFlag();
        MobclickAgent.onPageStart("MainScreen"); //统计页面("OpenPage"为页面名称，可自定义)
    }

    protected void onLoginChanged() {
        onRefresh();
    }

    @Override
    public void onPause() {
        if (proxy != null) {
            proxy.onPause();
        }
        MobclickAgent.onPageEnd("MainScreen"); //统计页面("OpenPage"为页面名称，可自定义)
        super.onPause();
    }

    @Override
    public void aotoLoginFromReq() {
        if (getHoldingActivity() != null && getHoldingActivity() instanceof BaseView) {
            ((BaseView) getHoldingActivity()).aotoLoginFromReq();
        }
    }

    /**
     * 通知其他界面刷新
     *
     * @param action
     */
    public void notify(int action) {
        Event event = Event.create(action);
        event.putParam(Integer.class, action);
        EventWrapper.post(event);
    }

    @Override
    public boolean eventEnable() {
        return false;
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

    View emptyLayout;
    ImageView emptyImage;
    TextView emptyText;
    protected View emptyContainer;
    private TextView emptyButton;
    public boolean hasData;

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public View getEmpterLayout() {
        if (emptyLayout == null) {
            if (emptyContainer != null) {
                emptyLayout = emptyContainer.findViewById(R.id.empty_layout);
            } else {
                emptyLayout = findViewById(R.id.empty_layout);
            }
        }
        return emptyLayout;
    }

    public View getEmptyTopMargin() {
        if (emptyTopMargin == null) {
            if (emptyContainer != null) {
                emptyTopMargin = emptyContainer.findViewById(R.id.empty_top_margin);
            } else {
                emptyTopMargin = findViewById(R.id.empty_top_margin);
            }
        }
        return emptyTopMargin;
    }

    public ImageView getEmpterImage() {
        if (emptyImage == null) {
            if (emptyContainer != null) {
                emptyImage = emptyContainer.findViewById(R.id.empty_image);
            } else {
                emptyImage = findViewById(R.id.empty_image);
            }
        }
        return emptyImage;
    }

    public TextView getEmpterText() {
        if (emptyText == null) {
            if (emptyContainer != null) {
                emptyText = emptyContainer.findViewById(R.id.empty_text);
            } else {
                emptyText = findViewById(R.id.empty_text);
            }
        }
        return emptyText;
    }

    public TextView getEmpterButton() {
        if (emptyButton == null) {
            if (emptyContainer != null) {
                emptyButton = emptyContainer.findViewById(R.id.empty_button);
            } else {
                emptyButton = findViewById(R.id.empty_button);
            }
        }
        return emptyButton;
    }

    @Override
    public void showdata() {
        setHasData(true);
        showEmpty(false);
    }

    @Override
    public void showNoNetWork() {
        resetEmpty();
        if (hasData) {
            showToast(getXmlString(R.string.common_no_network));
            return;
        }
        UIUtil.setOnClickListener(getEmpterButton(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptyButtonReloadEvent();
            }
        });
        showEmpty(true, true);
        UIUtil.setText(getEmpterText(), getXmlString(R.string.common_no_network));
//        UIUtil.setImageResource(getEmpterImage(), R.mipmap.common_data_error);
        UIUtil.setImageResource(getEmpterImage(), Pub.getThemeResource(getContext(), R.attr.common_no_data));
    }

    @Override
    public void showNoData() {
        resetEmpty();
        setHasData(false);
        showEmpty(true);
        UIUtil.setText(getEmpterText(), getString(R.string.common_data_empty));
        UIUtil.setImageResource(getEmpterImage(), Pub.getThemeResource(getContext(), R.attr.common_no_data));
    }

    @Override
    public void showFailer(String msg, ApiException e) {
        switch (e.code) {
            case ErrorType.NEED_LOGIN:
                showEmptyLogin();
                break;
            default:
                showFailer(msg);
                break;
        }


    }

    /**
     * 显示错误信息
     *
     * @param msg
     */
    private void showFailer(String msg) {
        resetEmpty();
        if (Pub.isStringEmpty(msg)) {
            msg = CommonUtils.getResouceString(getContext(), R.string.common_data_empty);
        }
        showToast(msg);
        if (hasData) {
            return;
        }
        showEmpty(true, true);
        UIUtil.setText(getEmpterText(), msg);
//        UIUtil.setImageResource(getEmpterImage(), R.mipmap.common_data_error);
        UIUtil.setImageResource(getEmpterImage(), Pub.getThemeResource(getContext(), R.attr.common_no_data));
    }

    protected void resetEmpty() {
        UIUtil.setText(getEmpterText(), getXmlString(R.string.common_data_empty));
        UIUtil.setText(getEmpterButton(), getXmlString(R.string.common_reload));
        UIUtil.setOnClickListener(getEmpterButton(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptyButtonReloadEvent();
            }
        });
    }

    protected void showEmptyLogin() {
        resetEmpty();
        if (hasData) {
            return;
        }
        UIUtil.setOnClickListener(getEmpterButton(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FtRounts.toQuickLogin(getContext());
            }
        });
        showEmpty(true, true);
        UIUtil.setText(getEmpterText(), getXmlString(R.string.common_see_after_login));
        UIUtil.setText(getEmpterButton(), getString(R.string.common_see_after_login));
        UIUtil.setImageResource(getEmpterImage(), Pub.getThemeResource(getContext(), R.attr.common_no_data));

    }

    /**
     * emptyButton的回调事件
     * 为什么添加这个方法不直接调用 onrefresh
     * 这里方便小白重写
     */
    protected void emptyButtonReloadEvent() {
        //默认调用的是onrefresh
        onRefresh();
    }

    protected void showEmpty(boolean visibility, boolean buttonVisibility) {
        UIUtil.setVisibility(getEmpterLayout(), visibility);
        UIUtil.setVisibility(getEmpterButton(), buttonVisibility);
        //重置
        if (getEmpterText() != null) {
            if (getContext() != null) {
                getEmpterText().setTextColor(Pub.getColor(getContext(), R.attr.font_color4));
            }
            getEmpterText().setOnClickListener(null);
        }
    }

    protected void showEmpty(boolean visibility) {
        showEmpty(visibility, false);
    }

    @Override
    public void onRefresh() {

    }

    /**
     * 根RootFragment
     * 在Tab切换的时候需要获取到
     * 被隐藏的信息，以处理socket等的事件
     * 不使用Fragment的onHiddenChange
     * 是因为Fragment的onHiddenChange
     * 总是再不合适的时机被调用无法准确判断
     */
    public void onHide() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public String getXmlString(int id) {
        if (getContext() == null || isDetached()) {
            return "";
        }
        return getContext().getResources().getString(id);
    }

    @Override
    public boolean onBackHandled() {
        return false;
    }


    @Override
    public AppContextDelegate getAppContextDelegate() {
        return appContextDelegate;
    }

    @Override
    public FragmentManagerDelegate getChildFragmentManagerDelegate(Fragment fragment) {
        return appContextDelegate.getChildFragmentManagerDelegate(fragment);
    }

    @Override
    public FragmentManagerDelegate getFragmentManagerDelegate() {
        return appContextDelegate.getFragmentManagerDelegate();
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
    public Activity getHoldingActivity() {
        return appContextDelegate.getHoldingActivity();
    }

    public void addChildFragment(Fragment fragment) {
        addChildFragment(fragment, false);
    }

    public void addChildFragment(Fragment fragment, boolean addToBackStack) {
        getChildFragmentManagerDelegate(this).addFragment(getChildFragmentContainerId(), fragment, addToBackStack);
    }

    public void replaceChildFragment(Fragment fragment) {
        replaceChildFragment(fragment, false);
    }

    public void replaceChildFragment(Fragment fragment, boolean addToBackStack) {
        getChildFragmentManagerDelegate(this).replaceFragment(getChildFragmentContainerId(), fragment, addToBackStack, true);
    }

    public void addFragment(Fragment fragment) {
        getFragmentManagerDelegate().addFragment(fragment);
        //getHoldingActivity().addFragment(fragment);
    }

    public void replaceFragment(Fragment fragment) {
        getFragmentManagerDelegate().replaceFragment(getFragmentContainerId(), fragment);
    }

    /**
     * @desc 调用{@link #replaceFragment(Fragment)}和{@link #addFragment(Fragment)}
     * 时,会使用此方法返回的id
     * <p/>
     */
    @Override
    public int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    /**
     * @desc 调用{}{@link #addChildFragment(Fragment, boolean)}和{@link #addFragment(Fragment)}
     * 时,会使用此方法返回的id
     * <p/>
     */
    protected int getChildFragmentContainerId() {
        return 0;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void setTitle(String title) {
        if (mTitleLayout != null) {
            mTitleLayout.setAppTitle(title);
        }
    }

    @Override
    public void destroy() {

    }

    public void doAnim(View view, String po, int value) {
        ViewWrapper viewWrapper = new ViewWrapper(view);
        ObjectAnimator.ofInt(viewWrapper, po, value).setDuration(1500).start();
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
        btSure.setBtbEnabled(customerValid() && systemValid());
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

    public int getColor(int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    /**
     * 开启浮动加载进度条
     */
    @Override
    public void startProgressDialog() {
        //jiang nullException
//        if (mActivity != null) {
//            mActivity.startProgressDialog();
//        }
    }

    /**
     * 停止浮动加载进度条
     */
    @Override
    public void stopProgressDialog() {
        //jiang
//        if (mActivity != null) {
//            mActivity.stopProgressDialog();
//        }
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

    /**
     * 获取主题色
     *
     * @param attr
     * @return
     */
    protected int getThemeColor(int attr) {
        return Pub.getColor(getContext(), attr);
    }

    protected int getDefColor(int color) {
        return CommonUtils.getColor(getContext(), color);
    }


    @Override
    public Object getCancelTag() {
        return this;
    }

    /**
     * 设置状态栏只有白色字体
     */
    public void setJustWhiteBarTxt() {
//        if (mImmersionBar == null)
//            return;
//        mImmersionBar.statusBarDarkFont(false, 0.2f);
//        mImmersionBar.statusBarColor(android.R.color.transparent);
//        mImmersionBar.init();
    }

    public int getDataStatue() {
        return dataStatue;
    }

    public void setDataStatue(int dataStatue) {
        this.dataStatue = dataStatue;
    }

}