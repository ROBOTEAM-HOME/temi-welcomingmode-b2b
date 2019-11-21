package com.robotemi.welcomingbtob.app

import timber.log.Timber

class MetricsHelper {
    fun init(app: Application) {
        Timber.plant(Timber.DebugTree())
    }
}