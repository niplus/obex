package com.fota.android.utils

import android.content.Context
import android.util.DisplayMetrics

fun Context.getScreenWidth(): Int{
    val dm = resources.displayMetrics
    return dm.widthPixels
}

fun Context.getScreenHeight(): Int{
    val dm = resources.displayMetrics
    return dm.heightPixels
}