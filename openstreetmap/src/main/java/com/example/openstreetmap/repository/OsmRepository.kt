package com.example.openstreetmap.repository

import android.util.Log
import com.example.openstreetmap.api.OsmApi
import com.google.android.gms.maps.model.LatLng

class OsmRepository(private val osmApi: OsmApi){

    suspend fun getCityWithBoundary(lat: Double, lon: Double) : Pair<String, List<LatLng>> {
        var city: String? = null
        var latLngs: List<LatLng>? = null

        try {
            val response = osmApi.getCity(lat, lon)
            if (response.isSuccessful) {
                response.body()?.let {
                    city = it.address?.let {
                        //I don't really know what municipality is
                        it.city ?: it.town ?: it.village ?: it.municipality
                    }
                    it.geojson?.coordinates?.let { listOfBoundaries ->
                        if (listOfBoundaries.isNotEmpty()) {
                            listOfBoundaries[0].let { boundary ->
                                latLngs = boundary.map { LatLng(it[1], it[0]) }
                            }
                        }
                    }
                }
            } else {
                Log.d("RETROFIT", "not successful")
            }
        } catch (e: Exception) {
            Log.d("RETROFIT", e.toString())
        }

        if(city == null || latLngs == null) {
            throw Exception()
        }

        return Pair(city!!, latLngs!!)
    }
}