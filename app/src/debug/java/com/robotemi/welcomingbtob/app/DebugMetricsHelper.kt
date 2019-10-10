package com.robotemi.welcomingbtob.app

import android.content.Context
import timber.log.Timber
import javax.inject.Inject

class DebugMetricsHelper @Inject constructor() {
    fun init(context: Context) {
        Timber.plant(Timber.DebugTree())
    }
}