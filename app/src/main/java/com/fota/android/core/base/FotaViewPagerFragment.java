package com.fota.android.core.base;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.databinding.FragmentViewpagerBinding;

import java.util.List;


/**
 *
 * Created by fjw on 2018/3/27.
 */
public abstract class FotaViewPagerFragment extends BaseFragment {

    List<Fragment> mFragments;

    protected FragmentViewpagerBinding mBinding;
    List<String> mTitles;

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_viewpager, container, false);
        return mBinding.getRoot();
    }

    /**
     * 设置title背景颜色
     */
    protected void setTitleBg(int titleBg){
        mBinding.tabs.setBackgroundColor(Pub.getColor(mContext,titleBg));
    }

    /**
     * 设置当前选中tab
     */
    public void setItem(int tab){
        if (mBinding.tabs!=null&&mBinding.tabs.getScrollBarSize()>tab){

        }

    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        //        setvLeftVisbility(View.GONE);
        mFragments = getFragment();
        // 第二步：为ViewPager设置适配器
        mTitles = getTitles();
        BaseFragmentAdapter adapter = new BaseFragmentAdapter(getChildFragmentManager(), mFragments, mTitles);
        mBinding.viewpager.setAdapter(adapter);
        //设置是否tab全屏
        mBinding.tabs.setFullScreen(true);
        //  第三步：将ViewPager与TableLayout 绑定在一起
        mBinding.tabs.setupWithViewPager(mBinding.viewpager, adapter, mTitles);
    }

    public void setCurrentItem(int item){
        mBinding.viewpager.setCurrentItem(item);
    }

    protected abstract List<String> getTitles();

    protected abstract List<Fragment> getFragment();

    public List<Fragment> getmFragments() {
        return mFragments;
    }
}
