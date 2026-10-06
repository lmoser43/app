package com.raebai.dashcam.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.raebai.dashcam.MainActivity
import com.raebai.dashcam.R
import com.raebai.dashcam.RaeBApplication

/**
 * Foreground service that keeps the dashcam recording alive even when
 * the app is backgrounded. Uses `camera|location` foreground service type
 * on Android 14+ (API 34).
 */
class RaeBDashcamService : Service() {

    companion object {
        const val ACTION_START = "com.raebai.dashcam.START_RECORDING"
        const val ACTION_STOP = "com.raebai.dashcam.STOP_RECORDING"
        const val NOTIFICATION_ID = 1001
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startRecording()
            ACTION_STOP -> stopRecording()
        }
        return START_STICKY
    }

    /**
     * Promotes the service to foreground state with a persistent notification.
     * On Android 14+, specifies `camera` and `location` as foreground service types.
     */
    private fun startRecording() {
        val notification = buildNotification("Recording in progress...")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // TODO: Initialize CameraX VideoRecording and start capture
    }

    /**
     * Stops recording and removes the foreground notification.
     */
    private fun stopRecording() {
        // TODO: Stop CameraX VideoRecording and release resources

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    /**
     * Builds the ongoing notification displayed while recording.
     */
    private fun buildNotification(contentText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, RaeBApplication.CHANNEL_ID_DASHCAM)
            .setContentTitle("RaeB AI Dashcam")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}
