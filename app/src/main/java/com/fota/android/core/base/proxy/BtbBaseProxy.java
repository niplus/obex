package com.fota.android.core.base.proxy;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fota.android.core.event.Event;
import com.fota.android.core.event.EventSubscriber;
import com.fota.android.core.event.EventWrapper;
import com.umeng.analytics.MobclickAgent;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * 由于Base类逻辑过于臃肿  故分离这里逻辑出来
 * 暂时用 无接口 静态方法的模式 抽离
 * 后期按需优化
 * Created by fjw on 2018/5/25.
 */
public class BtbBaseProxy {


    /**
     * Activity OnCreate调用
     * Fragment OnAttach调用
     */
    public void onAttach() {
        if (eventEnable() && mEventSubscriber != null) {
            EventWrapper.register(mEventSubscriber);
        }
    }

    /**
     * onAttach对应
     */
    public void onDetach() {
        if (eventEnable() && mEventSubscriber != null) {
            EventWrapper.unregister(mEventSubscriber);
        }
    }

    public void onStop() {
    }


    EventSubscriber mEventSubscriber;
    private Fragment fragment;
    private Activity activity;

    private IBtbBaseProxy mIProxy;

    public BtbBaseProxy(Activity activity) {
        this.activity = activity;
        mIProxy = (IBtbBaseProxy) activity;
        mEventSubscriber = (EventSubscriber) activity;
    }


    public BtbBaseProxy(Activity activity, Fragment fragment) {
        this.fragment = fragment;
        mIProxy = (IBtbBaseProxy) fragment;
        mEventSubscriber = (EventSubscriber) fragment;
        this.activity = activity;
    }


    /**
     * 初始化Activity
     */
    public static BtbBaseProxy with(@NonNull Activity activity) {
        if (activity == null)
            throw new IllegalArgumentException("Activity不能为null");
        return new BtbBaseProxy(activity);
    }

    /**
     * 初始化Activity Fragment
     */
    public static BtbBaseProxy with(@NonNull Activity activity, @NonNull Fragment fragment) {
        if (activity == null)
            throw new IllegalArgumentException("Activity不能为null");
        if (fragment == null)
            throw new IllegalArgumentException("Fragment不能为null");
        return new BtbBaseProxy(activity, fragment);
    }


    private boolean eventEnable() {
        return mIProxy.eventEnable();
    }

    /**
     * 默认隐藏软键盘
     */
    protected void hideKeyBoard() {
        if (activity == null) {
            return;
        }
        try {
            final View v = activity.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void notify(int action) {
        Event event = Event.create(action);
        event.putParam(Integer.class, action);
        EventWrapper.post(event);
    }

    public void onResume() {
        if (fragment != null) {
            MobclickAgent.onPageStart(fragment.getClass().getSimpleName());
        }
        if (activity != null) {
            MobclickAgent.onResume(activity);
        }

    }

    public void onPause() {
        if (fragment != null) {
            MobclickAgent.onPageEnd(fragment.getClass().getSimpleName());
        }
        if (activity != null) {
            MobclickAgent.onPause(activity);
        }
    }
}
