package com.ndl.lib_common.base

class Response<T>(
    val data: T,
    val msg: String?,
    val code: Int
)