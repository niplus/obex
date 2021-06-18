package com.fota.android.utils

import android.widget.Toast
import com.fota.android.app.FotaApplication

object ToastUtils {
    private var toast: Toast? = null

    fun showToast(msg: String){

//        Snackbar.make()
        if (toast == null)
            toast = Toast.makeText(FotaApplication.getContext(), msg, Toast.LENGTH_SHORT)
        else
            toast!!.setText(msg)

        toast!!.show()
    }
}