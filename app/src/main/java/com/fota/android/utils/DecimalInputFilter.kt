package com.fota.android.utils

import android.text.InputFilter
import android.text.Spanned

class DecimalInputFilter(private val maxValue: Double): InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toDouble()
            return if (input > maxValue) ""
            else null
        } catch (nfe: NumberFormatException) {
        }
        return ""

    }
}