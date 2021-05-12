package com.fota.android.core.mvvmbase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndl.lib_common.base.ErrorMessage
import com.ndl.lib_common.base.HttpCodeEexceptioin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception

open class BaseViewModel: ViewModel() {
    val error by lazy { MutableLiveData<ErrorMessage>() }

    fun launchUI(block: suspend () -> Unit) = viewModelScope.launch {
        try {
            block()
        }catch (e: HttpCodeEexceptioin){
            error.value = e.errorMessage
        }catch (e: Exception){
            error.value = ErrorMessage(true, null, e.message)
        }
    }
}