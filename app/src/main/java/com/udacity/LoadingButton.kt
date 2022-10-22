package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var textButton = resources.getString(R.string.button_name)

    private val buttonColor: Int
    private val backgroundColor: Int
    private val textColor: Int
    private val circleColor: Int

    // Paint object for button colour
    private lateinit var paintForButton : Paint

    // Paint object for button background colour when it's downloading
    private lateinit var paintForButtonBackgroundDark : Paint

    //Paint object for button text
    private lateinit var paintForText : Paint

    //Paint object for circle
    private lateinit var paintForCircle : Paint

    // Properties for animation stuff
    var buttonWidth = 0f
    private var circleAnimator = ValueAnimator()
    private var buttonAnimator = ValueAnimator()
    private var circleAngleProgress = 0f

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when(new)
        {
            ButtonState.Loading -> {
                //Change button name
                textButton = resources.getString(R.string.button_loading)

                // Circle animation
                circleAnimator = ValueAnimator.ofFloat(0f, 360f)
                    .apply {
                        duration = 1000
                        repeatCount = ValueAnimator.INFINITE
                        repeatMode = ValueAnimator.RESTART
                        interpolator = AccelerateInterpolator(1f)

                        addUpdateListener {
                            circleAngleProgress = animatedValue as Float
                            invalidate()
                        }
                    }

                // Button animation
                buttonAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                    .apply {
                        duration = 1000
                        repeatCount = ValueAnimator.INFINITE
                        repeatMode = ValueAnimator.RESTART
                        interpolator = AccelerateInterpolator(1f)


                        addUpdateListener {
                            buttonWidth = animatedValue as Float
                            invalidate()
                        }
                    }
                buttonAnimator.start()
                circleAnimator.start()

            }
            ButtonState.Completed -> {
                //Change name button
                textButton = resources.getString(R.string.button_name)
                buttonWidth = 0f
                circleAngleProgress = 0f
                circleAnimator.end()
                buttonAnimator.end()
                invalidate()
            }
        }
    }

    var progress: Float by Delegates.observable(0f) { _, _, _ ->
        invalidate()
    }

    init {
        buttonState = ButtonState.Clicked
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomButton,
            0, 0).apply {
            buttonColor = getColor(R.styleable.CustomButton_buttonColor, 0)
            textColor = getColor(R.styleable.CustomButton_textColor, 0)
            circleColor = getColor(R.styleable.CustomButton_circleColor, 0)
            backgroundColor = getColor(R.styleable.CustomButton_backgroundColor, 0)
        }

        paintForButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.colorPrimary)
        }

        paintForButtonBackgroundDark = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.colorPrimaryDark)
        }

        paintForText = Paint().apply {
            isAntiAlias = true
            color = textColor
            textSize = resources.getDimension(R.dimen.default_text_size)
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
        }

        paintForCircle = Paint().apply {
            isAntiAlias = true
            color = circleColor
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw button
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintForButton)

        // Only draws loading animation when ButtonState.Loading
        if (buttonState == ButtonState.Loading) {
            super.onDraw(canvas)

            // Draws animated button filled during download process
            canvas?.drawRect(0f, 0f, widthSize.toFloat() * buttonWidth / 100, heightSize.toFloat(), paintForButtonBackgroundDark)

            // Draws animated circle filled during download process
            canvas?.drawArc(
                widthSize - 145f, heightSize / 2 - 35f,
                widthSize - 75f, heightSize / 2 + 35f,
                0f, circleAngleProgress, true, paintForCircle)
        }

        // Centres text button
        val textHeight: Float = paintForText.descent() - paintForText.ascent()
        val textOffset: Float = textHeight / 2 - paintForText.descent()

        // Draw text button
        canvas?.drawText(
            textButton,
            widthSize.toFloat() / 2,
            heightSize.toFloat() / 2 + textOffset,
            paintForText
        )
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