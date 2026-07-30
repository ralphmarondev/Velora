package com.ralphmarondev.velora.receiver

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ralphmarondev.velora.R
import com.ralphmarondev.velora.core.data.network.FirebaseDatabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TrafficForegroundService : Service() {

    private val firebaseDatabaseService: FirebaseDatabaseService by inject()
    private val notificationHelper: NotificationHelper by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastTimestamp = 0L

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()

        val notification = NotificationCompat.Builder(
            this,
            NotificationHelper.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Velora")
            .setContentText("Monitoring traffic...")
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        serviceScope.launch {
            firebaseDatabaseService.observeTrafficRecord().collect { record ->
                record ?: return@collect

                if (record.timestamp <= lastTimestamp) {
                    return@collect
                }
                lastTimestamp = record.timestamp

                when {
                    record.isCongested -> {
                        notificationHelper.showNotification(
                            "Traffic Congestion",
                            "Heavy traffic detected. Consider another route."
                        )
                    }

                    record.isUnderConstruction -> {
                        notificationHelper.showNotification(
                            "Road Construction",
                            "Road construction ahead. Drive safely."
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null
}