package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_main.view.*
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
    private val paintOverlappedRec = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorPrimaryDark)
    }
    private var progressLevel = 0f

    //Set variables for text message on loading button
    private val paintTextMessage = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }
    private var textBtnStatus: String
    private val textHeight = paintTextMessage.descent() - paintTextMessage.ascent()
    private val textOffset = (textHeight / 2) - paintTextMessage.descent()

    //Set variables for circle loading on loading button
    private var paintArcLoading = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressAngle = 0F

    private var rectSupForArcLoading = RectF()
    private val arcSize = 50

    //Set animators for custom views
    private lateinit var circleAnimation: ValueAnimator
    private lateinit var loadingButtonAnimation: ValueAnimator

    /*
    Got some help from:
    https://github.com/AntonLearnsCS/AnimationPracticeApp/blob/master/starter/app/src/main/java/com/udacity/LoadingButton.kt
    https://github.com/jiangxiaocn/LoadApp/blob/main/starter/app/src/main/java/com/udacity/LoadingButton.kt
     */

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                textBtnStatus = context.getString(R.string.button_loading)
                /* Since width can vary based on device display,
                    we can animate the width as a fraction of it's calculated width.
                    Hence we will need animated values from 0 to 1 fraction */
                loadingButtonAnimation = ObjectAnimator.ofFloat(0f, 1f)
                loadingButtonAnimation.apply {
                    duration = 2000
                    repeatMode = ObjectAnimator.REVERSE
                    repeatCount = ObjectAnimator.INFINITE
                    addUpdateListener {
                        progressLevel = animatedValue as Float
                        this@LoadingButton.postInvalidate()
                    }
                    disableViewDuringAnimation(custom_button)
                    start()
                }

                /* Since the arc is mainly drawn with radius and sweep angle
                    and in our case we want to animate the angle, it should go
                    from 0 to full 360 degrees */
                circleAnimation = ObjectAnimator.ofFloat(0f, 360f)
                circleAnimation.apply {
                    duration = 2000
                    repeatMode = ObjectAnimator.REVERSE
                    repeatCount = ObjectAnimator.INFINITE
                    addUpdateListener {
                        progressAngle = animatedValue as Float
                        this@LoadingButton.postInvalidate()
                    }
                    start()
                }
            }

            ButtonState.Completed -> {
                textBtnStatus = context.getString(R.string.button_completed)
                loadingButtonAnimation.end()
                circleAnimation.end()
            }

            else -> {
                ButtonState.Pending
                textBtnStatus = context.getString(R.string.button_download)
            }
        }
    }

    private fun ValueAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }


    init {
        isClickable = true
        buttonState = ButtonState.Pending
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            paintLoadingButton.color = getColor(R.styleable.LoadingButton_loading_button_color, 0)
            paintArcLoading.color = getColor(R.styleable.LoadingButton_circle_loading_color, 0)
        }
        paintLoadingButton.color = context.getColor(R.color.colorPrimary)
        paintTextMessage.color = context.getColor(R.color.white)
        textBtnStatus = context.getString(R.string.button_download)
    }

    override fun performClick(): Boolean {
        //buttonState = ButtonState.Loading
        postInvalidate()
        if (super.performClick()) return true
        return true
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
        canvas?.drawRect(
            0f,
            10f,
            progressLevel * width.toFloat() - 10,
            height.toFloat() - 10,
            paintOverlappedRec
        )
        canvas?.drawText(textBtnStatus, width / 2f, (height / 2f) + textOffset, paintTextMessage)
        canvas?.drawArc(rectSupForArcLoading, 0f, progressAngle, true, paintArcLoading)
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