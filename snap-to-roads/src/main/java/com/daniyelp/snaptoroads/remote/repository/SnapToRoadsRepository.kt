package com.daniyelp.snaptoroads.remote.repository

import com.daniyelp.snaptoroads.remote.api.SnapToRoadsApi
import com.daniyelp.snaptoroads.remote.model.SnappedPoints

import com.google.android.gms.maps.model.LatLng
import java.lang.StringBuilder

internal class SnapToRoadsRepository constructor(
    private val api: SnapToRoadsApi,
    private val apiKey: String
) {

    suspend fun getSnappedToRoadsPath(path: List<LatLng>) : List<LatLng> {
        val stringPath : String = with(StringBuilder()) {
            path.forEach {
                append("${it.latitude},${it.longitude}|")
            }
            dropLast(1).toString()
        }
        var snappedPoints: SnappedPoints?
        try {
            val response = api.getSnappedToRoadsPath(stringPath, apiKey)
            if(response.isSuccessful) {
                snappedPoints = response.body()
            } else {
                throw Exception(response.raw().toString())
            }
        } catch (e: Exception) {
            throw e
        }
        val snappedPath : List<LatLng> = snappedPoints!!.list.map {
            it.location
        }
        return snappedPath
    }
}