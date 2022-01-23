package com.daniyelp.openstreetmap.api

import com.daniyelp.openstreetmap.model.OsmCity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OsmApi {

    @GET("/reverse?format=json&polygon_geojson=1")
    suspend fun getCity(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("zoom") zoom: Int
    ): Response<OsmCity>

}