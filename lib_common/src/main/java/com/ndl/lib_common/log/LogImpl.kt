package com.ndl.lib_common.log

import android.content.Context
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog

class LogImpl: ILog {
    val tag = "CB_LOG"
    override fun initLog(context: Context) {
        System.loadLibrary("c++_shared")
        System.loadLibrary("marsxlog")

        val path = context.filesDir.absolutePath
        val logPath = "$path/xlog"

        val xlog = Xlog()
        Log.setLogImp(xlog)
        Log.setConsoleLogOpen(true)
        Log.appenderOpen(Xlog.LEVEL_VERBOSE, Xlog.AppednerModeAsync, "", logPath, "CB_LOG", 0)
    }


    override fun i(msg: String) {
        Log.i(tag, msg)
    }

    override fun d(msg: String) {
        Log.i(tag, msg)
    }

    override fun e(msg: String, e: Throwable) {
        Log.i(tag, "$msg, ${e.message}")
    }
}