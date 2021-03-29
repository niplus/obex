package com.fota.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.fota.android.R
import com.fota.android.utils.dp
import kotlin.math.roundToInt

class FuturesProgressBar: View {

    var isLever = true
    set(value) {
        field = value
        divideString =  if (isLever) arrayOf("1X", "25X", "50X", "75X", "100X")
        else
            arrayOf("0%", "25%", "50%", "75%", "100%")
        invalidate()
    }

    private val paddingLeft = 5.dp
    private val paddingRight = 5.dp

    //选中文字背景高度
    private val selectTextRectHeight = 17.dp
    private val selectTextRectWidth = 30.dp

    //底部文字
    private var divideString = if (isLever) arrayOf("1X", "25X", "50X", "75X", "100X")
    else
        arrayOf("0%", "25%", "50%", "75%", "100%")
    private val bottomTextColor = 0xFF999999.toInt()
    private var bottomTextHeight = 17.dp
    private var bottomText2Bar = 10.dp
    private var bottomTextSize = 11.dp


    //默认bar的颜色
    private var defaultColor: Int = 0xFFD9DADD.toInt()
    private var barHeight = 2.dp
    private var selectBarHeight = 2.dp

    private var bar2SelectText = 10.dp
    //bar的y位置
    private var barPositionY = selectTextRectHeight + bar2SelectText + barHeight / 2

    private var barWidth = 0f

    var progress = 0
    set(value) {
        field = if (value > 100)
            100
        else
            value
        invalidate()
    }

    var lever = 0

    var leverChangeListener : ((Int)->Unit)? = null
    var progressChangeListener: ((Int) -> Unit)? = null

    private var selectCircleRadius = 5.dp
    private var divideTextSize = 0
    private var divideTextString = 0

    private var selectPointPosition = paddingLeft + selectCircleRadius
    private var showSelectText = false

    private var background = 0
    private var selectSmallColor = 0xFFFFFFFF.toInt()

    init {

        val attrs = intArrayOf(R.attr.lever_dialog_bg, R.attr.lever_bar_default_color, R.attr.lever_bar_select_small_color)
        val typedArray = context.obtainStyledAttributes(attrs)
//        background = typedArray.getColor(0, 0xFFFFFFFF.toInt())
        background = (getBackground() as ColorDrawable).color
        defaultColor = typedArray.getColor(1, 0xFFD9DADD.toInt())
        selectSmallColor = typedArray.getColor(2, 0xFFFFFFFF.toInt())
        typedArray.recycle()
    }

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
//        val mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + 10.dp.toInt(), MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)

    }


    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(l - 5.dp.toInt(), t, r + 5.dp.toInt(), b)
    }

    override fun onDraw(canvas: Canvas) {
        barWidth = width - selectCircleRadius * 2 - paddingLeft - paddingRight
        if (progress != 0){
            selectPointPosition = if (progress == 1){
                paddingLeft + selectCircleRadius
            }else{
                barWidth * (progress.toFloat() / 100f) + paddingLeft + selectCircleRadius
            }
            progress = 0
        }
        //底部bar
        barPaint.strokeWidth = barHeight
        barPaint.color = defaultColor
        canvas.drawLine(paddingLeft + selectCircleRadius, barPositionY, width - selectCircleRadius - paddingRight, barPositionY, barPaint)
        barPaint.strokeWidth = selectBarHeight
        barPaint.color = 0xFF3C78D7.toInt()
        canvas.drawLine(paddingLeft + selectCircleRadius, barPositionY, selectPointPosition, barPositionY, barPaint)

        defaultCirclePaint.color =defaultColor

        for (i in divideString.indices){
            val circleX = paddingLeft + selectCircleRadius + barWidth / (divideString.size - 1) * i

            defaultCirclePaint.color = background
            canvas.drawRect(circleX - selectCircleRadius - 3, barPositionY - selectCircleRadius - 3, circleX + selectCircleRadius + 3, barPositionY + selectCircleRadius + 3, defaultCirclePaint)
            defaultCirclePaint.color = defaultColor
            canvas.drawCircle(circleX, barPositionY, 3.dp, defaultCirclePaint)

            if (circleX <= selectPointPosition) {
                defaultCirclePaint.color = 0xFF3C78D7.toInt()
                canvas.drawCircle(circleX, barPositionY, selectCircleRadius, defaultCirclePaint)
                defaultCirclePaint.color = selectSmallColor
                canvas.drawCircle(circleX, barPositionY, 3.dp, defaultCirclePaint)
            }
        }

        defaultCirclePaint.color = 0xFF3C78D7.toInt()
        canvas.drawCircle(selectPointPosition, barPositionY, selectCircleRadius, defaultCirclePaint)
        defaultCirclePaint.color = selectSmallColor
        canvas.drawCircle(selectPointPosition, barPositionY, 3.dp, defaultCirclePaint)

        if (showSelectText)
            drawSelectText(canvas)
        drawBottomText(canvas)
    }

    fun drawSelectText(canvas: Canvas){
        defaultCirclePaint.color = 0xC0303133.toInt()
        canvas.drawRoundRect(selectPointPosition -selectTextRectWidth / 2, 0f, selectPointPosition + selectTextRectWidth / 2, selectTextRectHeight, 5f, 5f, defaultCirclePaint)

        val path = Path()
        path.moveTo(selectPointPosition - 2.dp, selectTextRectHeight)
        path.lineTo(selectPointPosition, selectTextRectHeight + 2.dp)
        path.lineTo(selectPointPosition + 2.dp, selectTextRectHeight)
        path.close()

        canvas.drawPath(path, defaultCirclePaint)

        val baseLine = getTextBaseline(selectTextRectHeight / 2, bottomTextPaint)
        bottomTextPaint.color = 0xFFFFFFFF.toInt()


        bottomTextPaint.textAlign = Paint.Align.CENTER
        if (isLever)
            canvas.drawText("${lever}X", selectPointPosition, baseLine, bottomTextPaint)
        else
            canvas.drawText("${lever}%", selectPointPosition, baseLine, bottomTextPaint)
    }

    fun drawBottomText(canvas: Canvas){
        val baseline = getTextBaseline(45.dp, bottomTextPaint)
        bottomTextPaint.color = bottomTextColor
        divideString.forEachIndexed{index, text ->
            when (index) {
                0 -> {
                    bottomTextPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText(text, paddingLeft, baseline, bottomTextPaint)
                }
                divideString.size - 1 -> {
                    bottomTextPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(text, width - paddingRight, baseline, bottomTextPaint)
                }
                else -> {
                    bottomTextPaint.textAlign = Paint.Align.CENTER
                    canvas.drawText(text, paddingLeft + selectCircleRadius + barWidth / (divideString.size - 1) * index, baseline, bottomTextPaint)
                }
            }

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
                    getCurrentLever()
                    invalidate()
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (!touchEnable){
                    selectPointPosition = paddingLeft + selectCircleRadius
                    invalidate()
                    return true
                }
                selectPointPosition = event.x
                if (selectPointPosition < paddingLeft + selectCircleRadius){
                    selectPointPosition = paddingLeft + selectCircleRadius
                }else if(selectPointPosition > width - paddingRight - selectCircleRadius){
                    selectPointPosition =  width - paddingRight - selectCircleRadius
                }

                getCurrentLever()
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                showSelectText = false
                invalidate()

                if (!touchEnable){
                    touchEnable = true
                    invalidate()
                    return true
                }
            }
        }

        return false
    }

    fun getCurrentLever(){
        val selectPosition = selectPointPosition - (paddingLeft + selectCircleRadius)
        lever = when(selectPosition){
            0f -> {
                if (isLever)
                    1
                else
                    0
            }
            barWidth -> {
                100
            }
            else ->{
                lever = (selectPosition / barWidth * 100).roundToInt()
                lever
            }
        }

        leverChangeListener?.invoke(lever)
    }

    fun getTextBaseline(centerY: Float, paint: Paint): Float{
        val fontMetrics: Paint.FontMetrics = Paint.FontMetrics()
        paint.getFontMetrics(fontMetrics)
        val offset = (fontMetrics.descent + fontMetrics.ascent) / 2
        return centerY - offset
    }

    var touchEnable = true
    fun init(enableTouch: Boolean){
        touchEnable = enableTouch
        selectPointPosition = paddingLeft + selectCircleRadius
        invalidate()
    }

}