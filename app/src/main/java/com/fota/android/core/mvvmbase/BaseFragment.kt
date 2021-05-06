package com.fota.android.core.mvvmbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import me.jessyan.autosize.internal.CustomAdapt

abstract class BaseFragment<T : ViewDataBinding, H : BaseViewModel> : Fragment(), CustomAdapt {

    lateinit var dataBinding: T

    lateinit var viewModel: H

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), getLayoutId(), container,false)
        viewModel = createViewModel()
        initComp()
        initData()
        return dataBinding.root
    }

    abstract fun getLayoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()
    abstract fun initComp()

    abstract fun createViewModel(): H

    override fun isBaseOnWidth(): Boolean {
        return true
    }

    override fun getSizeInDp(): Float {
        return 375f
    }
}