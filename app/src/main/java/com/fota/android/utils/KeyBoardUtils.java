package com.fota.android.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.fota.android.commonlib.utils.ScreenUtils;
import com.guoziwei.fota.chart.view.fota.FotaBigKLineBarChartView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author fanjianwei
 * @Description
 * @date 20160330
 */

public class KeyBoardUtils {


    /**
     * @param mEditText
     * @param mContext
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
        //
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    /**
     * @param mEditText
     * @param mContext
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public static void closeKeybord(Context mContext) {
        if (mContext == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        }
    }

    public interface OnKeyboardListener {
        void changed(boolean isOpened);
    }

    private static HashMap<View, List<ViewTreeObserver.OnGlobalLayoutListener>> viewOnLayoutChangeListenerHashMap = new HashMap<>();
    private static boolean keyboardVisible = false;

    public static void addOnKeyboardListener(final View rootview, final OnKeyboardListener keyboardListener) {
        if (null == viewOnLayoutChangeListenerHashMap.get(rootview)) {
            List<ViewTreeObserver.OnGlobalLayoutListener> listeners = new ArrayList<>();
            viewOnLayoutChangeListenerHashMap.put(rootview, listeners);
        }

        final int keyHeight = ScreenUtils.getScreenHeight(rootview.getContext()) / 3;
        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootview.getWindowVisibleDisplayFrame(r);

                int heightDiff = rootview.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > keyHeight) { // if more than 100 pixels, its probably a keyboard...
                    if (!keyboardVisible) {
                        keyboardVisible = true;
                        if (keyboardListener != null) {
                            keyboardListener.changed(true);
                        }
                    }
                } else {
                    if (keyboardVisible) {
                        keyboardVisible = false;
                        if (keyboardListener != null) {
                            keyboardListener.changed(false);
                        }
                    }
                }
            }
        };
        viewOnLayoutChangeListenerHashMap.get(rootview).add(listener);
        rootview.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    public static void removeOnKeybroadListener(View rootview) {
        if (null != rootview && null != viewOnLayoutChangeListenerHashMap.get(rootview)) {
            List<ViewTreeObserver.OnGlobalLayoutListener> listeners = viewOnLayoutChangeListenerHashMap.get(rootview);
            for (ViewTreeObserver.OnGlobalLayoutListener listener : listeners) {
                rootview.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
            }
            listeners.clear();
            viewOnLayoutChangeListenerHashMap.remove(rootview);
        }
    }

    /**
     * 设置键盘隐藏机制<br/>
     * 效果：点击输入框外的区域隐藏输入法软键盘<br>
     * 前提，当前界面不能有控件设置onTouchListener
     */
    public static void setupUISoftKeyBoardHideSystem(final View view, final boolean hasFocus) {
        if (view instanceof CheckBox)
            return;
        if (view instanceof TextView)
            return;
        //jiang 巨坑
        if (view instanceof FotaBigKLineBarChartView) {
            return;
        }
        if (hasFocus) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
        }
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    //View editText = ((Activity) v.getContext()).getCurrentFocus();
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        hideKeyboard(v);
                        if (hasFocus) {
                            view.requestFocus();
                        }
                    }
                    return false;
                }

            });
        }

        // If a layout container, iterate over children and seed recursion.

        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUISoftKeyBoardHideSystem(innerView, hasFocus);
            }

        }
    }

    /**
     * 隐藏虚拟键盘
     *
     * @param v
     */
    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Context mContext, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        openKeybord(editText, mContext);
    }
}