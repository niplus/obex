package com.fota.android.utils

import android.content.Context
import android.widget.Toast
import com.fota.android.app.FotaApplication

object ToastUtils {
    private var toast: Toast? = null

    fun showToast(msg: String){
        if (toast == null)
            toast = Toast.makeText(FotaApplication.getContext(), msg, Toast.LENGTH_SHORT)
        else
            toast!!.setText(msg)

        toast!!.show()
    }
}