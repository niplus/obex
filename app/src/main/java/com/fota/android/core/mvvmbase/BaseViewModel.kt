package com.fota.android.core.mvvmbase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel: ViewModel() {
    val error by lazy { MutableLiveData<String>() }

    fun launchUI(block: suspend () -> Unit) = viewModelScope.launch {
        try {
            block()
        }catch (e: Exception){
            e.printStackTrace()
            error.value = e.message
        }
    }
}