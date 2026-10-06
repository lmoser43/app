package com.raebai.dashcam

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.raebai.dashcam.service.RaeBDashcamService
import com.raebai.dashcam.ui.CameraPreviewScreen
import com.raebai.dashcam.ui.theme.RaeBAITheme

/**
 * Entry point of the RaeB AI application.
 *
 * Handles runtime permission requests and sets the Compose content tree.
 * The activity is locked to landscape orientation for dashcam use.
 */
class MainActivity : ComponentActivity() {

    /**
     * List of all runtime permissions required by the app.
     * POST_NOTIFICATIONS is only requested on Android 13+ (API 33+).
     */
    private val requiredPermissions: Array<String> = buildList {
        add(Manifest.permission.CAMERA)
        add(Manifest.permission.RECORD_AUDIO)
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    /**
     * Launcher for the multi-permission request dialog.
     * If all permissions are granted, the camera preview becomes active.
     */
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Permissions result handled reactively in Compose via UI state
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions on first launch
        requestRequiredPermissions()

        setContent {
            RaeBAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RaeBAIApp(
                        onStartRecording = { startDashcamService() },
                        onStopRecording = { stopDashcamService() }
                    )
                }
            }
        }
    }

    /**
     * Checks if all required permissions are granted; if not, launches
     * the system permission dialog.
     */
    private fun requestRequiredPermissions() {
        val allGranted = requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted) {
            permissionLauncher.launch(requiredPermissions)
        }
    }

    /**
     * Starts the foreground dashcam recording service.
     */
    private fun startDashcamService() {
        val intent = Intent(this, RaeBDashcamService::class.java).apply {
            action = RaeBDashcamService.ACTION_START
        }
        ContextCompat.startForegroundService(this, intent)
    }

    /**
     * Stops the foreground dashcam recording service.
     */
    private fun stopDashcamService() {
        val intent = Intent(this, RaeBDashcamService::class.java).apply {
            action = RaeBDashcamService.ACTION_STOP
        }
        startService(intent)
    }
}

/**
 * Root composable that hosts the main camera preview screen.
 */
@Composable
fun RaeBAIApp(
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    CameraPreviewScreen(
        onStartRecording = onStartRecording,
        onStopRecording = onStopRecording
    )
}
