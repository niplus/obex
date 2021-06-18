package com.fota.android.utils

import android.util.Log
import com.tencent.mmkv.MMKV
import java.util.*

fun getLocale(): Locale {
    return when(MMKV.defaultMMKV()?.getString("language", "")){
        "zh" -> Locale.CHINA
        "tw" -> Locale.TRADITIONAL_CHINESE
        "en" -> Locale.US
        else -> Locale.getDefault()
    }
}

fun getLanguageString(): String{
    return when(MMKV.defaultMMKV()?.getString("language", "")){
        "en" ->  "English"
        "tw" ->  "繁體中文"
        else -> "简体中文"
    }
}