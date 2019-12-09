package com.robotemi.welcomingbtob

interface IActivityCallback {
    fun setCloseVisibility(isVisible: Boolean)

    fun toggleActivityClickListener(enable: Boolean)
}