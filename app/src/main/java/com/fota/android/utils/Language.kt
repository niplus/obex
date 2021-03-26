package com.fota.android.utils

import com.tencent.mmkv.MMKV
import java.util.*

fun getLocale(): Locale {
    return when(MMKV.defaultMMKV()?.getString("language", "")){
        "zh" -> Locale.CHINA
        "tw" -> Locale.TAIWAN
        "en" -> Locale.ENGLISH
        else -> Locale.getDefault()
    }
}

fun getLanguageString(): String{
    return if (Locale.getDefault().language.equals("en")){
        "English"
    }else{
        val s = if (Locale.getDefault().country.equals("CN")) {
            "简体中文"
        } else {
            "繁體中文"
        }
        s
    }
}