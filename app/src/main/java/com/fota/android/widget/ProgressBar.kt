package com.fota.android.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.fota.android.R
import com.fota.android.utils.dp
import kotlin.math.roundToInt


class ProgressBar: View {
    private val paddingLeft = 16.dp
    private val paddingRight = 16.dp

    //选中文字背景高度
    private val selectTextRectHeight = 31.dp

    //底部文字
    private val divideString = arrayOf("1X", "20X", "40X", "60X", "80X", "100X")
    private var bottomTextColor = 0xFF394C9E.toInt()
    private var bottomTextHeight = 17.dp
    private var bottomText2Bar = 10.dp
    private var bottomTextSize = 12.dp


    //默认bar的颜色
    private var defaultColor: Int = 0xFF9BA5CE.toInt()
    private var selectColor: Int = 0xFF3D50A4.toInt()


    init {
        val attrs = intArrayOf(R.attr.bar_default, R.attr.bar_select_color, R.attr.progress_bottom_text_color)
        val typedArray = context.obtainStyledAttributes(attrs)
        defaultColor =  typedArray.getColor(0, 0xFF9BA5CE.toInt())
        selectColor = typedArray.getColor(1, 0xFF3D50A4.toInt())
        bottomTextColor = typedArray.getColor(2, 0xFF394C9E.toInt())
        typedArray.recycle()
    }
    private var barHeight = 2.dp
    private var selectBarHeight = 4.dp

    private var bar2SelectText = 10.dp
    //bar的y位置
    private var barPositionY = selectTextRectHeight + bar2SelectText + barHeight / 2

    private var barWidth = 0f


    var leverChangeListener : ((Int)->Unit)? = null

    private var selectCircleRadius = 8.dp
    private var divideTextSize = 0
    private var divideTextString = 0

    private var selectPointPosition = paddingLeft + selectCircleRadius
    private var showSelectText = false

    private val barPaint = Paint().apply {
        color = 0xFF9BA5CE.toInt()
        strokeWidth = barHeight
        isAntiAlias = true
    }

    private val defaultCirclePaint = Paint().apply {
        color = 0xFF9BA5CE.toInt()
        isAntiAlias = true
    }

    private val bottomTextPaint = Paint().apply {
        textSize = bottomTextSize
        color = bottomTextColor
        textAlign = Paint.Align.CENTER
    }

    init {
//        val rect = Rect()
//        bottomTextPaint.getTextBounds("100X", 0, "100X".length, rect)
//        bottomTextHeight = rect.height()

    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mHeightMeasureSpec = MeasureSpec.makeMeasureSpec((selectTextRectHeight + bar2SelectText + barHeight + bottomTextHeight + bottomText2Bar).toInt(),
                MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {

        //底部bar
        barPaint.strokeWidth = barHeight
        barPaint.color = defaultColor
        canvas.drawLine(paddingLeft + selectCircleRadius, barPositionY, width - selectCircleRadius - paddingRight, barPositionY, barPaint)
        barPaint.strokeWidth = selectBarHeight
        barPaint.color = selectColor
        canvas.drawLine(paddingLeft + selectCircleRadius, barPositionY, selectPointPosition, barPositionY, barPaint)

        defaultCirclePaint.color = defaultColor
        barWidth = width - selectCircleRadius * 2 - paddingLeft - paddingRight
        for (i in 0..5){
            val circleX = paddingLeft + selectCircleRadius + barWidth / 5 * i
            if (circleX <= selectPointPosition)
                defaultCirclePaint.color = selectColor
            else
                defaultCirclePaint.color = defaultColor
            canvas.drawCircle(circleX, barPositionY, 5.dp, defaultCirclePaint)
        }

        //拖动circle
//        if (selectPointPosition == 0f){
//            defaultCirclePaint.color = 0xFF3D50A4.toInt()
//            canvas.drawCircle(paddingLeft + selectCircleRadius, barPositionY, selectCircleRadius, defaultCirclePaint)
//            defaultCirclePaint.color = Color.WHITE
//            canvas.drawCircle(paddingLeft + selectCircleRadius, barPositionY, 5.dp, defaultCirclePaint)
//        }else{
            defaultCirclePaint.color = selectColor
            canvas.drawCircle(selectPointPosition, barPositionY, selectCircleRadius, defaultCirclePaint)
            defaultCirclePaint.color = defaultColor
            canvas.drawCircle(selectPointPosition, barPositionY, 5.dp, defaultCirclePaint)
//        }


//        if (showSelectText)
        drawSelectText(canvas)
        drawBottomText(canvas)
    }

    fun drawSelectText(canvas: Canvas){
        defaultCirclePaint.color = 0xFF303133.toInt()
        canvas.drawRoundRect(selectPointPosition - 15.dp, 0f, selectPointPosition + 15.dp, 31.dp, 10f, 10f, defaultCirclePaint)

        val path = Path()
        path.moveTo(selectPointPosition - 2.dp, 31.dp)
        path.lineTo(selectPointPosition, 33.dp)
        path.lineTo(selectPointPosition + 2.dp, 31.dp)
        path.close()

        canvas.drawPath(path, defaultCirclePaint)

        val baseLine = getTextBaseline(15.dp, bottomTextPaint)
        bottomTextPaint.color = 0xFFFFFFFF.toInt()

        val selectPosition = selectPointPosition - (paddingLeft + selectCircleRadius)
        val text = when(selectPosition){
            0f -> {
                leverChangeListener?.invoke(1)
                "1X"
            }
            barWidth -> {
                leverChangeListener?.invoke(100)
                "100X"
            }
            else ->{
                val lever = (selectPosition / barWidth * 100).roundToInt()
                leverChangeListener?.invoke(lever)
                "${lever}X"
            }
        }
        canvas.drawText(text, selectPointPosition, baseLine, bottomTextPaint)
    }

    fun drawBottomText(canvas: Canvas){
        val baseline = getTextBaseline(61.dp, bottomTextPaint)
        bottomTextPaint.color = bottomTextColor
        divideString.forEachIndexed{index, text ->
            canvas.drawText(text, paddingLeft + selectCircleRadius + barWidth / 5 * index, baseline, bottomTextPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked){
            MotionEvent.ACTION_DOWN -> {
                val downX = event.x
                val downY = event.y

                if (downY > selectTextRectHeight && downY < selectTextRectHeight + 22.dp) {
                    showSelectText = true

                    selectPointPosition = event.x
                    if (selectPointPosition < paddingLeft + selectCircleRadius){
                        selectPointPosition = paddingLeft + selectCircleRadius
                    }else if(selectPointPosition > width - paddingRight - selectCircleRadius){
                        selectPointPosition =  width - paddingRight - selectCircleRadius
                    }
                    invalidate()
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                selectPointPosition = event.x
                if (selectPointPosition < paddingLeft + selectCircleRadius){
                    selectPointPosition = paddingLeft + selectCircleRadius
                }else if(selectPointPosition > width - paddingRight - selectCircleRadius){
                    selectPointPosition =  width - paddingRight - selectCircleRadius
                }
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                showSelectText = false
                invalidate()
            }
        }

        return false
    }

    fun getTextBaseline(centerY: Float, paint: Paint): Float{
        val fontMetrics: Paint.FontMetrics = Paint.FontMetrics()
        paint.getFontMetrics(fontMetrics)
        val offset = (fontMetrics.descent + fontMetrics.ascent) / 2
        return centerY - offset
    }
}