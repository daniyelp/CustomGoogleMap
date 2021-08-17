package com.example.customgooglemapexample

import androidx.lifecycle.*
import com.example.customgooglemapexample.util.LocationTracker
import com.example.gpsbroadcastreceiver.GpsBroadcastReceiver
import com.example.internetbroadcastreceiver.InternetBroadcastReceiver
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor (
    private val locationTracker: LocationTracker,
    private val gpsBroadcastReceiver: GpsBroadcastReceiver,
    private val internetBroadcastReceiver: InternetBroadcastReceiver
    ): ViewModel() {

    val lastLocation : LiveData<LatLng> = locationTracker.lastLocation
    val gpsEnabled : LiveData<Boolean> = gpsBroadcastReceiver.gpsEnabled
    val internetEnabled: LiveData<Boolean> = internetBroadcastReceiver.internetEnabled

    init {
        locationTracker.startLocationUpdates()
        gpsBroadcastReceiver.start()
        internetBroadcastReceiver.start()
    }

    override fun onCleared() {
        super.onCleared()
        locationTracker.stopLocationUpdates()
        gpsBroadcastReceiver.stop()
        internetBroadcastReceiver.stop()
    }
}