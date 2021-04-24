package com.fota.android.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("image")
fun ImageView.setImage(res: Int){
    setImageResource(res)
//    Glide.with(this).load(res).into(this)
}