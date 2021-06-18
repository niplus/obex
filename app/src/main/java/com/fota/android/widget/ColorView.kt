package com.fota.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

class ColorView: View {

    var colorWidth = 0f
    set(value) {
        field = value
        invalidate()
    }
    var color: Int = 0
    set(value) {
        field = value
        paint.color = value
        invalidate()
    }

    private var paint = Paint().apply {
        style = Paint.Style.FILL
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(width - colorWidth, 0f, width.toFloat(), height.toFloat(), paint)
    }
}