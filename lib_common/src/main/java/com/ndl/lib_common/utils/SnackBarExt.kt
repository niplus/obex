package com.ndl.lib_common.utils

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackMsg(msg: String){
    view?.showSnackMsg(msg)
}

fun Activity.showSnackMsg(msg: String){
    window.decorView.showSnackMsg(msg)
}

fun View.showSnackMsg(msg: String){
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT)
}