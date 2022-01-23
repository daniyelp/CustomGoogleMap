package com.daniyelp.openstreetmap.repository

import com.daniyelp.openstreetmap.api.OsmApi
import com.google.android.gms.maps.model.LatLng

internal class OsmRepository(private val osmApi: OsmApi) {

    class OsmException(message: String? = null): Exception(message)

    suspend fun getCityWithBoundary(latLng: LatLng, zoom: Int) : Pair<String, List<LatLng>> {
        var city: String? = null
        var latLngs: List<LatLng>? = null

        try {
            val response = osmApi.getCity(latLng.latitude, latLng.longitude, zoom)
            if (response.isSuccessful) {
                response.body()?.let {
                    city = it.address?.let {
                        it.state ?: it.city ?: it.town ?: it.village ?: it.municipality
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
                throw OsmException(response.errorBody()?.charStream().toString())
            }
        } catch (e: Exception) {
            throw OsmException(e.message)
        }

        if(city == null || latLngs == null) {
            throw OsmException("response successful but failed to determine city or boundary")
        }

        return Pair(city!!, latLngs!!)
    }
}