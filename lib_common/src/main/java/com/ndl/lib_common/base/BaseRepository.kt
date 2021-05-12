package com.ndl.lib_common.base

import com.ndl.lib_common.R
import java.lang.Exception

/**
 * 仓库基类
 */
open class BaseRepository {

    //全局统一处理
    suspend  fun <T> apiCall(call: suspend()->Response<T>): Response<T>{
        val result = call.invoke()
        val msg = when(result.code){
            130001 -> R.string.error_code_130001
            130002 -> R.string.error_code_130002
            120044 -> R.string.error_code_120044
            120045 -> R.string.error_code_120045
            120046 -> R.string.error_code_120046
            120047 -> R.string.error_code_120047
            120048 -> R.string.error_code_120048
            else -> result.msg
        }
        if (result.code != 0){
            if (msg is Int){
                throw HttpCodeEexceptioin(ErrorMessage(false, msg, null))
            }else{
                throw HttpCodeEexceptioin(ErrorMessage(true, null, msg.toString()))
            }
        }
        return result
    }
}