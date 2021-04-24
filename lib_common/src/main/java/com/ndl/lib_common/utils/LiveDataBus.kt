package com.ndl.lib_common.utils

import androidx.lifecycle.MutableLiveData

object LiveDataBus {

    private val bus = mutableMapOf<String, MutableLiveData<*>>()

    fun<T> getBus(key: String): MutableLiveData<T>{
        if (!bus.containsKey(key)){
            val liveData = MutableLiveData<T>()
            bus[key] = liveData
        }

        return bus[key]!! as MutableLiveData<T>
    }
}