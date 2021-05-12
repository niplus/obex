package com.fota.android.core.mvvmbase

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.fota.android.R
import com.ndl.lib_common.utils.showSnackMsg
import me.jessyan.autosize.internal.CustomAdapt

abstract class BaseFragment<T : ViewDataBinding, H : BaseViewModel> : Fragment(), CustomAdapt {

    lateinit var dataBinding: T

    lateinit var viewModel: H

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            getLayoutId(),
            container,
            false
        )
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_loading, null)
        loadDialog = Dialog(requireContext(), R.style.CustomProgressDialog)
        loadDialog.setCancelable(true)
        loadDialog.setCanceledOnTouchOutside(false)
        loadDialog.setContentView(
            view,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        viewModel = createViewModel()
        viewModel.error.observe(this, Observer {
            hideLoadDialog()
            if (it != null) {
                if (it.isString){
                    showSnackMsg(it.msg!!)
                }else{
                    showSnackMsg(getString(it.resId!!))
                }
            }
        })
//        viewModel.error.observe(this, Observer {
//
//        })
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

    private lateinit var loadDialog: Dialog
    fun showLoadDialog(){
        loadDialog.show()
    }

    fun hideLoadDialog(){
        loadDialog.dismiss()
    }
}