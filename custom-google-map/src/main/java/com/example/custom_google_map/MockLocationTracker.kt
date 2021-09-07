package com.example.custom_google_map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import kotlin.concurrent.thread

//would have been better practice to use an interface
class MockLocationTracker {

    private val _lastLocation = MutableLiveData<LatLng>()
    val lastLocation : LiveData<LatLng> = _lastLocation

    private var shouldRun = false

    private fun mockPath() {
        val mockLatLngs = listOf(
            LatLng(46.768771764277,23.609049841761585), LatLng(46.76845760834686,23.609049171209335), LatLng(46.76826677150349,23.609121926128868), LatLng(46.76804607967789,23.60913299024105), LatLng(46.76784375890598,23.60922083258629), LatLng(46.767581039312134,23.609315380454063), LatLng(46.76736126328297,23.609449155628685), LatLng(46.76707167197351,23.60970765352249), LatLng(46.76683283260867,23.60988937318325), LatLng(46.76661098663457,23.610065057873726), LatLng(46.766420832215324,23.610198833048344), LatLng(46.76623159575003,23.610360436141494), LatLng(46.766023756390474,23.6104653775692), LatLng(46.765838193058165,23.610623963177204), LatLng(46.76562277343745,23.610873408615586), LatLng(46.765403678398016,23.611051440238953), LatLng(46.76506171381456,23.61140113323927), LatLng(46.76472870390697,23.61173674464226), LatLng(46.76454221745955,23.611930198967457), LatLng(46.76424020909719,23.61225239932537), LatLng(46.76396874462241,23.612418025732037)
        )
        thread(start = true) {
            while(shouldRun) {
                mockLatLngs.forEach {
                    _lastLocation.postValue(it)
                    Thread.sleep(1000)
                }
            }
        }
    }

    fun startLocationUpdates() {
        shouldRun = true
        mockPath()
    }

    fun stopLocationUpdates() {
        shouldRun = false
    }
}