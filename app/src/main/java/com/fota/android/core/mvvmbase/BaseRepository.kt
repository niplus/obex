package com.fota.android.core.mvvmbase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BaseRepository {
    //全局统一处理
    suspend fun apiCall(call: suspend()->Unit){
        call.invoke()
    }
}