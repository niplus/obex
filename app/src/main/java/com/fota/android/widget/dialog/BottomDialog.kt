package com.fota.android.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.fota.android.R

open class BottomDialog(context: Context): Dialog(context, R.style.BottomDialog) {

    init {
        val window = window
        window!!.decorView.setPadding(0, 0, 0, 0)
        // 获取Window的LayoutParams
        // 获取Window的LayoutParams
        val attributes = window.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.gravity = Gravity.BOTTOM
        // 一定要重新设置, 才能生效
        // 一定要重新设置, 才能生效
        window.attributes = attributes
//        view = LayoutInflater.from(context).inflate(layoutRes, null)
//        setContentView(view)
    }
}