package com.ndl.lib_common.widget.banner

import android.widget.ImageView

interface ImageEngine {
    fun loadImage(imageView: ImageView, path: String)
}