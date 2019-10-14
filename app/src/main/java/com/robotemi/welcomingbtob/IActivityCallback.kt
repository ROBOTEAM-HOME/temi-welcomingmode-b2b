package com.robotemi.welcomingbtob

interface IActivityCallback {
    fun setCloseVisibility(isVisible: Boolean)

    fun toggleWelcomingModeListener(enable: Boolean)

    fun toggleActivityClickListener(enable: Boolean)
}