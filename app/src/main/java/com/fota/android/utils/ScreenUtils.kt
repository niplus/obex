package com.fota.android.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue

fun Context.getScreenWidth(): Int{
    val dm = resources.displayMetrics
    return dm.widthPixels
}

fun Context.getScreenHeight(): Int{
    val dm = resources.displayMetrics
    return dm.heightPixels
}

val Int.dp: Float
    get() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)
    }
