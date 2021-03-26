package com.fota.android.widget

import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.widget.NestedScrollView

class FutureScrollView: NestedScrollView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var rectF: Rect = Rect()

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        findProgressBar(this)
    }

    fun findProgressBar(viewGroup: ViewGroup): Boolean{
        for (view in viewGroup.children){
            if (view is FuturesProgressBar){
                val intArray = IntArray(2)
                view.getLocationOnScreen(intArray)
                rectF = Rect(intArray[0], intArray[1], intArray[0] + view.width,intArray[1] + view.height)
                return true
            }else if(view is ViewGroup){
                if (findProgressBar(view)){
                    return true
                }
            }
        }
        return false
    }

    var isDragBar = false
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when(ev.actionMasked){
            MotionEvent.ACTION_DOWN ->{
                val downX = ev.rawX
                val downY = ev.rawY
                if (downX > rectF.left && downX < rectF.right &&
                        downY > rectF.top && downY < rectF.bottom){
                    isDragBar = true
                    return false
                }else{
                    isDragBar = false
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragBar){
                    return false
                }
            }

        }
        return super.onInterceptTouchEvent(ev)
    }



}