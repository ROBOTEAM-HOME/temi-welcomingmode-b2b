package com.robotemi.welcomingbtob.tips

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
import com.robotemi.welcomingbtob.utils.Constants

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

    private lateinit var viewFlipper: AdapterViewFlipper

    private lateinit var leftOutAnimator: ObjectAnimator

    private lateinit var rightInAnimator: ObjectAnimator

    private lateinit var adapter: TipsAdapter

    private val flipperIntervalTimeout: Int = 10000

    private fun loadTips() {
        val robot = Robot.getInstance()
        val locationForTips = if (robot.locations.size > 1) {
            robot.locations[1]
        } else {
            context.getString(R.string.location_home_base)
        }
        val callForTips = if (robot.adminInfo != null) {
            robot.adminInfo!!.name
        } else {
            context.getString(R.string.admin)
        }
        val tips = mutableListOf<String>()
        when (robot.wakeupWord.toLowerCase()) {
            Constants.WAKEUP_WORD_ALEXA -> {
                tips.clear()
                tips.add(String.format("Try “Alexa, tell my temi to go to %s”", locationForTips))
                tips.add("Try “Alexa, tell my temi to follow me”")
                tips.add(String.format("Try “Alexa, tell my temi to call %s”", callForTips))
                tips.add("Try “Alexa, tell my temi to take a selfie”")
                tips.add("Try “Alexa, tell my temi to take a video”")
                tips.add("Try “Alexa, tell my temi to take a GIF”")
            }
            Constants.WAKEUP_WORD_HEY_TEMI -> {
                tips.clear()
                tips.add(String.format("Try “Hey temi, go to %s”", locationForTips))
                tips.add("Try “Hey temi, follow me”")
                tips.add(String.format("Try “Hey temi, call %s”", callForTips))
                tips.add("Try “Hey temi, to take a selfie”")
                tips.add("Try “Hey temi, to take a video”")
                tips.add("Try “Hey temi, to take a GIF”")
                tips.add("Try “Hey temi, play music on iHeart”")
            }
            else -> {
                tips.clear()
                tips.add(String.format("“叮当叮当，去%s”", locationForTips))
                tips.add("“叮当叮当，跟着我”")
                tips.add(String.format("“叮当叮当，打电话给%s”", callForTips))
                tips.add("“叮当叮当，来张自拍”")
                tips.add("“叮当叮当，拍视频”")
                tips.add("“叮当叮当，今天天气”")
            }
        }
        tips.shuffle()
        adapter.updateList(tips)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (hasWindowFocus) {
            loadTips()
            AnimationUtils.animate(
                viewFlipper,
                R.animator.tips_flipper_slide_in,
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        viewFlipper.inAnimation = rightInAnimator
                        viewFlipper.outAnimation = leftOutAnimator
                    }
                })
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