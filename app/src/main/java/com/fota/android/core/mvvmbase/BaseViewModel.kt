package com.fota.android.core.mvvmbase

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndl.lib_common.base.ErrorMessage
import com.ndl.lib_common.base.HttpCodeEexceptioin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

open class BaseViewModel: ViewModel() {
    val error by lazy { MutableLiveData<ErrorMessage>() }

    val title = ObservableField<String>()

    var file: File? = null


    fun launchUI(block: suspend () -> Unit) = viewModelScope.launch {
        try {
            block()
        }catch (e: HttpCodeEexceptioin){
            error.value = e.errorMessage
        }catch (e: Exception){
            error.value = ErrorMessage(true, null, e.message)
        }
    }

    fun onBackClick(view: View){
        (view.context as BaseActivity<*, *>).finish()
    }
}