package com.fota.android.utils

import java.math.BigDecimal

fun String.mul(value: String): String{
    if (this.isEmpty() || value.isEmpty())
        return "0"
    return BigDecimal(this).multiply(BigDecimal(value)).toPlainString()
}

fun String.divide(value: String, scale: Int): String{
    if (this.isEmpty() || value.isEmpty())
        return "0"

    val mValue = BigDecimal(value)
    if (mValue.compareTo(BigDecimal(0)) == 0){
        return "0"
    }

    return BigDecimal(this).divide(mValue, scale, BigDecimal.ROUND_HALF_UP).toPlainString()
}