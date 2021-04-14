package com.fota.android.core.mvvmbase

class BaseRepository {
    //全局统一处理
    suspend fun apiCall(call: suspend()->Unit){
        call.invoke()
    }
}