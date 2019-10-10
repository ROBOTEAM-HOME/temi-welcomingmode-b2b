package com.robotemi.welcomingbtob.app

import android.app.Application
import android.content.Context
import com.robotemi.welcomingbtob.injection.AppComponent
import com.robotemi.welcomingbtob.injection.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent.getDebugMetricsHelper().init(this)

//        startKoin {
//            androidLogger()
//            androidContext(this@App)
//            androidFileProperties()
//        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        this.appComponent = DaggerAppComponent
            .builder()
            .build()

        appComponent.inject(this)

    }
}