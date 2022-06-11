package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    //Set variables for loadingButton
    private val loadingButton = Rect()
    private val paintLoadingButton = Paint(
        Paint.ANTI_ALIAS_FLAG
    )

    //Set variables for text message on loading button
    private val paintTextMessage = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }
    private var textBtnStatus: String
    private val textHeight = paintTextMessage.descent() - paintTextMessage.ascent()
    private val textOffset = (textHeight / 2) - paintTextMessage.descent()

    //Set variables for circle loading on loading button
    private var paintArcLoading = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorAccent)
    }

    private var rectSupForArcLoading = RectF()
    private val arcSize = 50

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            paintLoadingButton.color = getColor(R.styleable.LoadingButton_loading_button_color, 0)
        }
        paintLoadingButton.color = context.getColor(R.color.colorPrimary)
        paintTextMessage.color = context.getColor(R.color.white)
        textBtnStatus = context.getString(R.string.button_download)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        loadingButton.set(10, 10, width - 10, height - 10)
        rectSupForArcLoading.set(
            ((3 * width) / 4f) - arcSize,
            (height / 2f) - arcSize,
            ((3 * width) / 4f) + arcSize,
            (height / 2f) + arcSize
        )
        canvas?.drawRect(loadingButton, paintLoadingButton)
        canvas?.drawText(textBtnStatus, width / 2f, (height / 2f) + textOffset, paintTextMessage)
        canvas?.drawArc(rectSupForArcLoading, 0f, 270f, true, paintArcLoading)
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