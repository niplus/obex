package com.ndl.lib_common.base

/**
 * 仓库基类
 */
open class BaseRepository {

    //全局统一处理
    suspend  fun <T> apiCall(call: suspend()->Response<T>): Response<T>{
        val result = call.invoke()

        return result
    }
}