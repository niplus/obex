package com.ndl.lib_common.utils

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

fun Fragment.requestPermission(permission: String){
    registerForActivityResult(ActivityResultContracts.RequestPermission()){

    }.launch(permission)
}