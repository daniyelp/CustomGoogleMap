package com.example.snap_to_roads.remote.api

import com.example.snap_to_roads.remote.model.SnappedPoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SnapToRoadsApi {

    @GET("/v1/snapToRoads")
    suspend fun getSnappedToRoadsPath(
        @Query("path") path: String,
        @Query("key") apiKey: String,
        @Query("interpolate") interpolate: Boolean = true
    ) : Response<SnappedPoints>
}