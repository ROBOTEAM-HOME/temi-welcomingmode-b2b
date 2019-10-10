package com.robotemi.welcomingbtob.tips;


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterViewFlipper
import android.widget.RelativeLayout
import com.robotemi.sdk.Robot
import com.robotemi.welcomingbtob.R
import com.robotemi.welcomingbtob.utils.AnimationUtils

class TipsViewImpl : RelativeLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init()
    }

    companion object {
        const val TAG: String = "TipsViewImpl"

        private const val ALEXA = "Alexa"

        private const val TEMI = "Hey temi"
    }

    private lateinit var viewFlipper: AdapterViewFlipper

    private lateinit var leftOutAnimator: ObjectAnimator

    private lateinit var rightInAnimator: ObjectAnimator

    private lateinit var adapter: TipsAdapter

    private val flipperIntervalTimeout: Int = 10000

    private fun loadTips() {
        val wakeupAssistant: String = Robot.getInstance().wakeupWord
        val tips: List<String> = if (ALEXA == wakeupAssistant) {
            resources.getStringArray(R.array.tips_alexa).asList()
        } else {
            resources.getStringArray(R.array.tips_temi).asList()
        }
        adapter.updateList(tips)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (hasWindowFocus) {
            loadTips();
            AnimationUtils.animate(
                viewFlipper,
                R.animator.tips_flipper_slide_in,
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        viewFlipper.inAnimation = rightInAnimator
                        viewFlipper.outAnimation = leftOutAnimator
                    }
                });
        }
    }

    private fun init() {
        val view = inflate(context, R.layout.tips_view, null)
        addView(view)

        val inAlpha = PropertyValuesHolder.ofFloat(ALPHA, 0f, 1f)
        val outAlpha = PropertyValuesHolder.ofFloat(ALPHA, 1f, 0f)
        val rightInTrans = PropertyValuesHolder.ofFloat(TRANSLATION_X, 1000f, 0f)
        val leftOutTrans = PropertyValuesHolder.ofFloat(TRANSLATION_X, 0f, -1000f)
        viewFlipper = view.findViewById(R.id.tips_view_flipper)
        leftOutAnimator = ObjectAnimator.ofPropertyValuesHolder(viewFlipper, outAlpha, leftOutTrans)
        rightInAnimator = ObjectAnimator.ofPropertyValuesHolder(viewFlipper, inAlpha, rightInTrans)

        viewFlipper.flipInterval = flipperIntervalTimeout
        adapter = TipsAdapter(context)
        viewFlipper.adapter = adapter
        viewFlipper.isAutoStart = true
    }

    fun start() {
        onWindowFocusChanged(true)
        viewFlipper.startFlipping()
    }
}