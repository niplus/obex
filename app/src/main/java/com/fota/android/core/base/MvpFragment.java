package com.fota.android.core.base;

/**
 * Created by stone on 2018/3/31.
 */


import android.os.Bundle;
import android.view.View;

/**
 * Created by stone on 2016/7/26.
 */
public abstract class MvpFragment<P extends BasePresenter> extends BaseFragment {


    protected P mvpPresenter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mvpPresenter = createPresenter();
        if (mvpPresenter != null && getArguments() != null) {
            mvpPresenter.getExtras(getArguments());
        }
    }

    protected abstract P createPresenter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mvpPresenter != null) {
            mvpPresenter.detachView();
        }
    }

    @Override
    public void onPause() {
        if(mvpPresenter != null) {
            mvpPresenter.onHide();
        }
        super.onPause();
    }

    @Override
    public void onHide() {
        if(mvpPresenter != null) {
            mvpPresenter.onHide();
        }
    }

    public P getPresenter() {
        if(mvpPresenter == null){
            mvpPresenter = createPresenter();
        }
        return mvpPresenter;
    }

    @Override
    public void onDestroy() {
//        OkHttp3Utils.cancelTag(this);
        super.onDestroy();
    }
}
