package com.robotemi.welcomingbtob.app

import android.app.Application
import com.google.gson.Gson
import com.robotemi.sdk.Robot
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    private val appModule = module {
        single { MetricsHelper() }
        single { Robot.getInstance() }
        single { Gson() }
    }

    private val metricsHelper: MetricsHelper by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            androidFileProperties()
            modules(appModule)
        }
        metricsHelper.init(this)
    }
}