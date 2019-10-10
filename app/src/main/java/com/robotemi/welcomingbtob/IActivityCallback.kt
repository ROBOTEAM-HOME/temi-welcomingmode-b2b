package com.robotemi.welcomingbtob

import com.robotemi.sdk.listeners.OnWelcomingModeStatusChangedListener

interface IActivityCallback {
    fun setCloseVisibility(isVisible: Boolean)

    fun toggleWelcomingModeListener(enable: Boolean)

    fun toggleActivityClickListener(enable: Boolean)
}