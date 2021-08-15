package com.example.customgooglemapexample.util

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
            Log.d("GPS", "STH")
            result?.locations?.forEach {
                _lastLocation.value = LatLng(it.latitude, it.longitude)
                Log.d("GPS", _lastLocation.value.toString())
            }
        }
    }

    fun startLocationUpdates() {
        Log.d("GPS", "started location provider")
        _receivingLocationUpdates.value = true
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
                Log.d("GPS", "permissions denied")
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        Log.d("GPS", "stopped location provider")
        _receivingLocationUpdates.value = false
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}