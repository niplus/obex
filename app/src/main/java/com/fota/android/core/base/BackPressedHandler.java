package com.fota.android.core.base;

/**
 * desc:拦截返回键
 * author：zg
 * date:16/8/30
 * time:上午11:15
 */
public interface BackPressedHandler {

    /**
     * 如何拦截返回键,需要返回true
     * @return
     */
    boolean onBackHandled();

}
