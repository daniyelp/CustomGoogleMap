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

    private val _gpsEnabled = MutableLiveData<Boolean>()
    val gpsEnabled : LiveData<Boolean> = _gpsEnabled

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action!! == LocationManager.PROVIDERS_CHANGED_ACTION) {
            Log.d("GPS", "new change")
            updateGpsStatus()
        }
    }

    fun start() {
        updateGpsStatus()
        IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION).also {
            context.registerReceiver(this, it)
            Log.d("GPS", "broadcast started")
        }
    }

    fun stop() {
        context.unregisterReceiver(this)
        Log.d("GPS", "broadcast stopped")
    }

    private fun updateGpsStatus() {
        val lm = context.getSystemService(Service.LOCATION_SERVICE) as LocationManager
        lm.isProviderEnabled(LocationManager.GPS_PROVIDER).let {
            if(it != _gpsEnabled.value) {
                _gpsEnabled.value = it
                Log.d("GPS", "gps status ${if (it) "on" else "off"}")
            }
        }
    }
}