package com.fota.android.core.mvvmbase

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.fota.android.R
import com.fota.android.commonlib.base.AppConfigs
import kotlinx.coroutines.GlobalScope

abstract class BaseActivity<T: ViewDataBinding, H: BaseViewModel>: FragmentActivity() {

    val dataBinding: T by lazy {
        DataBindingUtil.setContentView<T>(this, getLayoutId())
    }

    var viewModel: H? = null

    /**
     * 初始化控件
     */
    abstract var initComp: ((T) -> Unit)?

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppConfigs.getTheme() == AppConfigs.THEME_WHITE) {
            //默認是白天主題
            setTheme(R.style.AppTheme_White)
        } else {
            //否则是晚上主題
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        initComp?.invoke(dataBinding)
    }

    abstract fun getLayoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()

    abstract fun createViewModel(): H?
}