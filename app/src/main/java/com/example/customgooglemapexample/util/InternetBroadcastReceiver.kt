package com.example.customgooglemapexample.util

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class InternetBroadcastReceiver @Inject constructor(private val context: Context): BroadcastReceiver() {

    private val TAG = "INTERNET"
    private val _internetEnabled = MutableLiveData<Boolean>()
    val internetEnabled : LiveData<Boolean> = _internetEnabled

    init {
        updateInternetStatus()
    }

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action!! == ConnectivityManager.CONNECTIVITY_ACTION) {
            updateInternetStatus()
        }
    }

    fun start() {
        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).also {
            context.registerReceiver(this, it)
        }
    }

    fun stop() {
        context.unregisterReceiver(this)
    }

    private fun updateInternetStatus() {
        val cm = context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        _internetEnabled.value = isConnected
        Log.d(TAG, "INTERNET STATUS CHANGED INSIDE BROADCAST RECEIVER")
    }
}