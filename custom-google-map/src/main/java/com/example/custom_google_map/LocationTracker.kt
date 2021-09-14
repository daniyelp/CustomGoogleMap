package com.example.custom_google_map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class LocationTracker constructor(private val context: Context) {

    private val _receivingLocationUpdates = MutableLiveData<Boolean>()
    val receivingLocationUpdates : LiveData<Boolean> = _receivingLocationUpdates

    private val _lastLocation = MutableLiveData<LatLng>()
    val lastLocation : LiveData<LatLng> = _lastLocation

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest().apply {
        interval = 2000L
        fastestInterval = 1000L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        @Override
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            result?.locations?.forEach {
                _lastLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }

    fun startLocationUpdates() {
        _receivingLocationUpdates.value = true
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