package com.fota.android.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

inline fun View.showShortSnack(msg: String){
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()
}