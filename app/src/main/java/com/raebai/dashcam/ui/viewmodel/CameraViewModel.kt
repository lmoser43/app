package com.raebai.dashcam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raebai.dashcam.location.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel that manages the camera screen's UI state, including
 * real-time location and speed data streamed from [LocationManager].
 */
class CameraViewModel : ViewModel() {

    private val _locationData = MutableStateFlow<LocationManager.LocationData?>(null)
    val locationData: StateFlow<LocationManager.LocationData?> = _locationData.asStateFlow()

    /**
     * Starts collecting location updates from the provided [LocationManager].
     * Updates are emitted as a [StateFlow] for Compose observers.
     */
    fun startLocationUpdates(locationManager: LocationManager) {
        viewModelScope.launch {
            locationManager.locationUpdates().collect { data ->
                _locationData.value = data
            }
        }
    }
}
