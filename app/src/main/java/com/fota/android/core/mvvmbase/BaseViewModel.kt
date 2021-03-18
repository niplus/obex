package com.fota.android.core.mvvmbase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception

class BaseViewModel: ViewModel() {
    private val error by lazy { MutableLiveData<Exception>() }

    fun launchUI(block: CoroutineScope.() -> Unit) = viewModelScope.launch {
        try {
            block()
        }catch (e: Exception){
            error.value = e
        }
    }
}