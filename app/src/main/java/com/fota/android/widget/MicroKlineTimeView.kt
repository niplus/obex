package com.fota.android.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min

class MicroKlineTimeView: View {

    private var minValue = 0.0
    private var maxValue = 0.0

    private var heightUnit = 0.0

    var lastTime = ""

    var closePriceList = mutableListOf<Double>()
    set(value) {
        minValue = value.min()?:0.0
        maxValue = value.max()?:0.0
        field.clear()
        field.addAll(value)

        if (width != 0){
            initPath()
        }
        invalidate()
    }

    fun changeLast(price: Double, time: String){
        if (this.lastTime != time){
            closePriceList.add(price)
            minValue = min(minValue, price)
            maxValue = max(maxValue, price)
        }else{
            closePriceList.removeAt(closePriceList.lastIndex)
            closePriceList.add(price)
            minValue = closePriceList.min()?:0.0
            maxValue = closePriceList.max()?:0.0
        }
        invalidate()
    }

    private var strokePath = Path()
    private var colorPath = Path()

    var color = 0
    set(value) {
        isColorChange = true
        field = value
        invalidate()
    }
    var isColorChange = false

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private val strokePaint = Paint().apply{
        isAntiAlias = true
        color = 0xff000000.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        if (strokePath.isEmpty)
            initPath()

        if (closePriceList.isEmpty())
            return

        if (isColorChange){
            strokePaint.color = color
            paint.shader = LinearGradient(
                0f,
                0f,
                0f,
                height.toFloat(),
                color,
                getColorWithAlpha(0f, color),
                Shader.TileMode.CLAMP
            )
            isColorChange = false
        }

        canvas.drawPath(strokePath, strokePaint)
        canvas.drawPath(colorPath, paint)
    }

    private fun getYValue(value: Double): Double{
        if (maxValue == minValue) return  0.0
        //防止多次计算
        if (heightUnit == 0.0)
            heightUnit = height / (maxValue - minValue)

        return height - (value - minValue) * heightUnit
    }

    private fun initPath(){
        if (closePriceList.isEmpty()) {
            return
        }
        strokePath.reset()
        colorPath.reset()
        val widthUnit = width.toFloat() / (closePriceList.size - 1)
        strokePath.moveTo(0f, getYValue(closePriceList[0]).toFloat())

        for (i in 1 until closePriceList.size){
            strokePath.lineTo(i * widthUnit, getYValue(closePriceList[i]).toFloat())
        }

        colorPath.addPath(strokePath)
        colorPath.lineTo((closePriceList.size - 1) * widthUnit, height.toFloat())
        colorPath.lineTo(0f, height.toFloat())
        colorPath.close()

        heightUnit = 0.0
    }

    private fun getColorWithAlpha(alpha: Float, baseColor: Int): Int {
        val a = 255.coerceAtMost(0.coerceAtLeast((alpha * 255).toInt())) shl 24
        val rgb = 0x00ffffff and baseColor
        return (a + rgb).toInt()
    }
}