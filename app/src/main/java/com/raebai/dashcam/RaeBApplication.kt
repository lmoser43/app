package com.raebai.dashcam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

/**
 * Application class responsible for initializing app-wide resources
 * such as notification channels required for the foreground service.
 */
class RaeBApplication : Application() {

    companion object {
        const val CHANNEL_ID_DASHCAM = "raeb_dashcam_channel"
        const val CHANNEL_NAME_DASHCAM = "Dashcam Recording"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    /**
     * Creates the notification channel for the foreground dashcam service.
     * Required for Android O (API 26)+.
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_DASHCAM,
                CHANNEL_NAME_DASHCAM,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ongoing notification for dashcam recording"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
