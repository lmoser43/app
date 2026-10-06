package com.raebai.dashcam.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Helper class that wraps [FusedLocationProviderClient] to provide
 * continuous speed and GPS coordinate updates as a Kotlin [Flow].
 *
 * @param context Application or Activity context used to obtain the FusedLocationProviderClient.
 */
class LocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * High-accuracy location request tuned for vehicle speed tracking.
     * Updates every second with PRIORITY_HIGH_ACCURACY for GPS-level precision.
     */
    private val locationRequest: LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateIntervalMillis(500L)      // Fastest update interval
            .setWaitForAccurateLocation(true)       // Wait for GPS fix
            .setMinUpdateDistanceMeters(1f)         // Minimum displacement
            .build()

    /**
     * Emits [LocationData] containing speed (KM/H), latitude, longitude,
     * and accuracy at ~1 Hz intervals.
     *
     * Uses [callbackFlow] to bridge the Google Play Services callback API
     * into a cold Flow that collectors can observe.
     */
    @SuppressLint("MissingPermission")
    fun locationUpdates(): Flow<LocationData> = callbackFlow {

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val speedKmh = if (location.hasSpeed) {
                        // m/s → km/h conversion (1 m/s = 3.6 km/h)
                        location.speed * 3.6f
                    } else {
                        0f
                    }

                    val data = LocationData(
                        speedKmh = speedKmh,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        timestamp = location.time
                    )

                    trySend(data)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

        // Clean up when the Flow collector is cancelled
        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }.distinctUntilChanged { old, new ->
        // Only emit when speed or position meaningfully changes
        val speedDiff = kotlin.math.abs(old.speedKmh - new.speedKmh)
        val latDiff = kotlin.math.abs(old.latitude - new.latitude)
        val lonDiff = kotlin.math.abs(old.longitude - new.longitude)
        speedDiff < 0.5f && latDiff < 0.0001 && lonDiff < 0.0001
    }

    /**
     * Data class representing a single location snapshot.
     */
    data class LocationData(
        val speedKmh: Float,
        val latitude: Double,
        val longitude: Double,
        val accuracy: Float,
        val timestamp: Long
    )
}
