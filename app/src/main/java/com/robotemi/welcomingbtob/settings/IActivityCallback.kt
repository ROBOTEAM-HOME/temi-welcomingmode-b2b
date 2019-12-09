package com.robotemi.welcomingbtob.settings

import android.view.View
import androidx.fragment.app.Fragment

interface IActivityCallback {
    fun setTitle(title: String)

    fun setVisibilityOfDone(isVisible: Boolean)

    fun setEnableOfDone(enable: Boolean)

    fun setDoneClickListener(listener: View.OnClickListener)

    fun setBackClickListener(listener: View.OnClickListener)

    fun startFragment(fragment: Fragment)
}