package com.robotemi.welcomingbtob.app

import timber.log.Timber

class DebugMetricsHelper {
    fun init() {
        Timber.plant(Timber.DebugTree())
    }
}