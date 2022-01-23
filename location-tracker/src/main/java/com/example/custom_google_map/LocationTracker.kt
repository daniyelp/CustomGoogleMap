package com.example.custom_google_map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.lang.Exception

class LocationTracker constructor(
    val context: Context,
    interval: Long = 2000L,
    fastestInterval: Long = 1000L
) {

    private val _receivingLocationUpdates = MutableLiveData<Boolean>()
    val receivingLocationUpdates: LiveData<Boolean> = _receivingLocationUpdates

    private val _lastLocation = MutableLiveData<com.google.android.gms.maps.model.LatLng>()
    val lastLocation: LiveData<com.google.android.gms.maps.model.LatLng> = _lastLocation

    private val fusedLocationProviderClient =
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = com.google.android.gms.location.LocationRequest().apply {
        this.interval = interval
        this.fastestInterval = fastestInterval
        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback: com.google.android.gms.location.LocationCallback = object : com.google.android.gms.location.LocationCallback() {
        @Override
        override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
            super.onLocationResult(result)
            result.locations
                .forEach {
                _lastLocation.value =
                    com.google.android.gms.maps.model.LatLng(it.latitude, it.longitude)
            }
        }
    }

    class LocationPermissionsNotGrantedException(message: String? = null) : Exception(message)

    fun startLocationUpdates() {
        _receivingLocationUpdates.value = true
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw LocationPermissionsNotGrantedException()
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        _receivingLocationUpdates.value = false
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}