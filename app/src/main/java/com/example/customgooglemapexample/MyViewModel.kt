package com.example.customgooglemapexample

import androidx.lifecycle.*
import com.example.customgooglemapexample.util.GpsBroadcastReceiver
import com.example.customgooglemapexample.util.LocationTracker
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor (
    private val locationTracker: LocationTracker,
    private val gpsBroadcastReceiver: GpsBroadcastReceiver
    ): ViewModel() {

    val lastLocation : LiveData<LatLng> = locationTracker.lastLocation
    val gpsEnabled : LiveData<Boolean> = gpsBroadcastReceiver.gpsEnabled

    init {
        locationTracker.startLocationUpdates()
        gpsBroadcastReceiver.start()
    }

    override fun onCleared() {
        super.onCleared()
        locationTracker.stopLocationUpdates()
        gpsBroadcastReceiver.stop()
    }
}