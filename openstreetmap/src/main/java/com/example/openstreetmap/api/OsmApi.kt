package com.example.openstreetmap.api

import com.example.openstreetmap.model.OsmCity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OsmApi {

    @GET("/reverse?format=json&polygon_geojson=1&zoom=10")
    suspend fun getCity(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<OsmCity>

}