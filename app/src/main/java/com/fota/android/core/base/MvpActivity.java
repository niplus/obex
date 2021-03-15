package com.fota.android.core.base;

/**
 * Created by stone on 2018/3/31.
 */


import android.os.Bundle;

import com.fota.android.commonlib.utils.Pub;
import com.fota.android.utils.KeyBoardUtils;

/**
 * 创建对应的Mvp的Activity
 * Created by fjw on 2016/7/26.
 */
public abstract class MvpActivity<P extends BasePresenter> extends BaseActivity {

    protected P mvpPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mvpPresenter = createPresenter();
        if (mvpPresenter != null) {
            mvpPresenter.getExtras(getIntent().getExtras());
        }
        super.onCreate(savedInstanceState);
    }

    protected abstract P createPresenter();

    public P getPresenter() {
        return mvpPresenter;
    }

    @Override
    protected void onDestroy() {
//        OkHttp3Utils.cancelTag(this);
        if (mvpPresenter != null) {
            mvpPresenter.detachView();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if(mvpPresenter != null) {
            mvpPresenter.onHide();
        }
        super.onPause();
    }

    @Override
    public void finish() {
        if (mvpPresenter != null) {
            mvpPresenter.finishView();
        }
        super.finish();
    }

    /**
     * 添加Fragment
     *
     * @param fragment
     * @return
     */
    protected BaseFragment addFragment(BaseFragment fragment) {
        if (Pub.isFastAddFragment()) {
            return null;
        }
        KeyBoardUtils.closeKeybord(getContext());
        return super.addFragment(getContainerId(), fragment, true);
    }

}
