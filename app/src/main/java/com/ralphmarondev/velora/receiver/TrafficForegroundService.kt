package com.ralphmarondev.velora.receiver

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ralphmarondev.velora.R
import com.ralphmarondev.velora.core.data.local.preferences.AppPreferences
import com.ralphmarondev.velora.core.data.network.FirebaseAuthService
import com.ralphmarondev.velora.core.data.network.FirebaseDatabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TrafficForegroundService : Service() {

    private val firebaseDatabaseService: FirebaseDatabaseService by inject()
    private val firebaseAuthService: FirebaseAuthService by inject()
    private val preferences: AppPreferences by inject()
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
            .setContentTitle("TugueGARAW")
            .setContentText("Monitoring traffic...")
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(1, notification)
        }

        serviceScope.launch {
            try {
                val email = preferences.email.first()
                val password = preferences.password.first()

                if (email.isNullOrBlank() || password.isNullOrBlank()) {
                    notificationHelper.showNotification(
                        title = "Foreground Service",
                        message = "Monitoring failed. Authenticate first!"
                    )
                    Log.e("TrafficForegroundService", "Authenticate first.")
                    stopSelf()
                    return@launch
                }

                val uid = firebaseAuthService.login(email = email, password = password)
                if (uid == null) {
                    notificationHelper.showNotification(
                        title = "Foreground Service",
                        message = "Authentication failed."
                    )
                    Log.e("TrafficForegroundService", "Authentication failed.")
                    stopSelf()
                    return@launch
                }
                Log.d("TrafficForegroundService", "Authenticated as $uid")

                firebaseDatabaseService.observeTrafficRecord().collect { record ->
                    record ?: return@collect

                    if (record.timestamp <= lastTimestamp) {
                        return@collect
                    }
                    lastTimestamp = record.timestamp

                    when {
                        record.isCongested -> {
                            notificationHelper.showNotification(
                                title = "Traffic Congestion",
                                message = "Heavy traffic detected. Consider another route."
                            )
                        }

                        record.isUnderConstruction -> {
                            notificationHelper.showNotification(
                                title = "Road Construction",
                                message = "Road construction ahead. Drive safely."
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("TrafficForegroundService", "Error: ${e.message}")
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        Log.d("TrafficForegroundService", "Service Destroyed :(")
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null
}