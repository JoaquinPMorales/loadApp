package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val circleColor: Int
    private val circleRadius: Float
    private val primaryColor: Int
    private val secondaryColor: Int
    private val textColor: Int

    private val textPaint: Paint
    private val rectanglePaint: Paint
    private val circlePaint: Paint

    private val rect = Rect()
    private lateinit var textClipToShow: String

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0).apply {

            try {
                primaryColor = getColor(R.styleable.LoadingButton_buttonColor,
                    Color.RED)
                secondaryColor = getColor(R.styleable.LoadingButton_progressColor,
                    Color.BLUE)
                textColor = getColor(R.styleable.LoadingButton_textColor,
                    Color.WHITE)
                circleColor = getColor(R.styleable.LoadingButton_circleColor,
                    Color.YELLOW)
                circleRadius = getDimension(R.styleable.LoadingButton_circleProgressRadius,
                    resources.getDimension(R.dimen.circleRadius))
            } finally {
            }
        }

        rectanglePaint = Paint().apply {
            isAntiAlias = true
            color = secondaryColor
            style = Paint.Style.FILL
        }

        textPaint = Paint().apply {
            isAntiAlias = true
            color = textColor
            textSize = resources.getDimension(R.dimen.default_text_size)
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
        }

        circlePaint = Paint().apply {
            isAntiAlias = true
            color = circleColor
            style = Paint.Style.FILL
        }

        setTextClipToShow(context.getString(R.string.button_name))
    }

    private fun setTextClipToShow(value: String) {
        textClipToShow = value
        textPaint.getTextBounds(
            textClipToShow,
            0,
            textClipToShow.length,
            rect
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(primaryColor)
//        if (progress > 0f) {
//            drawRectangle(canvas)
//            drawCircle(canvas)
//        }
        drawText(canvas)
    }

    private fun drawText(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(
            (widthSize / 2).toFloat(),
            (heightSize / 2).toFloat() - rect.centerY()
        )
        canvas?.drawText(
            textClipToShow,
            0f, 0f, textPaint
        )
        canvas?.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}