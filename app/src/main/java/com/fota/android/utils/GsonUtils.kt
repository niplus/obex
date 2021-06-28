package com.fota.android.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GsonUtils {
    val gson = Gson()

    fun <T> parseToList(jsonString: String, className: Class<Array<T>>): List<T>{
        val array = gson.fromJson(jsonString, className)
        return array.toList()
    }
}