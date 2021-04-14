package com.ndl.lib_common.log

import android.content.Context

interface ILog {
    fun initLog(context: Context)
    fun i(msg: String)
    fun d(msg: String)
    fun e(msg: String, e: Throwable)
}