package com.ralphmarondev.velora

import android.app.Application
import com.ralphmarondev.velora.di.appModule
import com.ralphmarondev.velora.receiver.NotificationHelper
import com.ralphmarondev.velora.receiver.TrafficListener
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }

        val notificationHelper: NotificationHelper = getKoin().get()
        notificationHelper.createNotificationChannel()
        val trafficListener: TrafficListener = getKoin().get()
        trafficListener.start()
    }
}