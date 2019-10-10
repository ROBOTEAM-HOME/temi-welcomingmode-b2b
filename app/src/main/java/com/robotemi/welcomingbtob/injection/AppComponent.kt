package com.robotemi.welcomingbtob.injection

import com.robotemi.welcomingbtob.app.App
import com.robotemi.welcomingbtob.app.DebugMetricsHelper
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(app: App)

    fun getDebugMetricsHelper() : DebugMetricsHelper
}