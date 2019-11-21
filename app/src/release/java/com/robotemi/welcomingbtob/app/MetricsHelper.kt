package com.robotemi.welcomingbtob.app

import android.app.Application
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.robotemi.welcomingbtob.BuildConfig

class MetricsHelper {
    fun init(app: Application) {
        AppCenter.start(app, BuildConfig.APPCENTER_SECRET, Analytics::class.java, Crashes::class.java)
    }
}