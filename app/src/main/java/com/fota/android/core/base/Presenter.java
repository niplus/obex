package com.fota.android.core.base;

/**
 * Created by stone on 2016/7/25.
 */
public interface Presenter<V> {
    void attachView(V view);

    void detachView();

    /**
     * fragment or activity view
     * 传递过来的onHide
     */
    void onHide();

    void finishView();
}
