package com.ndl.lib_common.widget.banner

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.ndl.lib_common.R
import com.ndl.lib_common.base.BaseAdapter
import com.ndl.lib_common.base.MyViewHolder
import com.ndl.lib_common.databinding.ItemBannerBinding

class Banner : FrameLayout {
    var paths = mutableListOf<String>()
    set(value) {
        paths.clear()
        paths.addAll(value)
        paths.add(0, value[value.size - 1])
        paths.add(value[0])
        bannerAdapter.notifyDataSetChanged()
    }

    var onBannerClick: ((Int)->Unit)? = null

    var imageEngine: ImageEngine? = null

    private val bannerAdapter = object : BaseAdapter<ItemBannerBinding, String>(paths, R.layout.item_banner, 0){
        override fun onBindViewHolder(holder: MyViewHolder<ItemBannerBinding>, position: Int) {
            imageEngine?.loadImage(holder.dataBinding.image, paths[position])
            holder.dataBinding.root.setOnClickListener {
                Log.i("nidongliang", "position: $position, itemCount: $itemCount")
                val clickPosition = when(position){
                    itemCount - 1 -> 0
                    0 -> {itemCount - 3}
                    else -> position - 1
                }
                onBannerClick?.invoke(clickPosition)
            }
        }
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            if (positionOffsetPixels != 0)
                return

            if (position == 0){
                viewpager2.setCurrentItem(paths.size - 2, false)
            }else if(position == paths.size - 1){
                viewpager2.setCurrentItem(1, false)
            }
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    val viewpager2: ViewPager2 = ViewPager2(context).apply {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams = params
        adapter = bannerAdapter
        this@Banner.addView(this)
        registerOnPageChangeCallback(onPageChangeCallback)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}