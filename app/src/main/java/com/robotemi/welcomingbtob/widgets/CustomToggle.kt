package com.robotemi.welcomingbtob.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.robotemi.welcomingbtob.R
import kotlinx.android.synthetic.main.view_custom_toggle.view.*
import timber.log.Timber

class CustomToggle : LinearLayout {
    private var green = 0
    private var red = 0
    private var grey = 0
    private var state = false
    private var listener: ToggleListener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.view_custom_toggle, null)
        addView(view)
        green = ContextCompat.getColor(context, R.color.primary_green)
        red = ContextCompat.getColor(context, R.color.red)
        grey = ContextCompat.getColor(context, R.color.gray200)
        setOnClickListener { v: View? -> toggle() }
    }

    fun setToggle(isOn: Boolean) {
        state = isOn
        if (isOn) {
            customToggleTextOn!!.setTextColor(green)
            customToggleTextOff!!.setTextColor(grey)
        } else {
            customToggleTextOn!!.setTextColor(grey)
            customToggleTextOff!!.setTextColor(red)
        }
    }

    fun toggle() {
        Timber.d("toggle()")
        state = !state
        setToggle(state)
        listener!!.onToggle(state)
    }

    fun setToggleListener(toggleListener: ToggleListener?) {
        listener = toggleListener
    }

    interface ToggleListener {
        fun onToggle(on: Boolean)
    }
}