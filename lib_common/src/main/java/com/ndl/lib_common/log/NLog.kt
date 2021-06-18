package com.ndl.lib_common.log

import android.content.Context

object NLog {
    private val iLog: ILog = LogImpl()
    fun initLog(context: Context){
        iLog.initLog(context)
    }
    fun i(msg: String){
        iLog.i(msg)
    }
    fun d(msg: String){
        iLog.d(msg)
    }

    fun e(msg: String, e: Throwable){
        iLog.e(msg, e)
    }
}