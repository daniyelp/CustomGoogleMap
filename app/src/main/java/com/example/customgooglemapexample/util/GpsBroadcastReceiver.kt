package com.example.customgooglemapexample.util

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class GpsBroadcastReceiver @Inject constructor(private val context: Context): BroadcastReceiver() {

    private val TAG = "GPS"
    private val _gpsEnabled = MutableLiveData<Boolean>(false)
    val gpsEnabled : LiveData<Boolean> = _gpsEnabled

    init {
        updateGpsStatus()
    }

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action!! == LocationManager.PROVIDERS_CHANGED_ACTION) {
            updateGpsStatus()
        }
    }

    fun start() {
        IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION).also {
            context.registerReceiver(this, it)
        }
    }

    fun stop() {
        context.unregisterReceiver(this)
    }

    private fun updateGpsStatus() {
        val lm = context.getSystemService(Service.LOCATION_SERVICE) as LocationManager
        lm.isProviderEnabled(LocationManager.GPS_PROVIDER).let {
            _gpsEnabled.value = it
        }
        Log.d("GPS", "gps status changed inside broadcast receiver, gps is ${_gpsEnabled.value}")
    }
}