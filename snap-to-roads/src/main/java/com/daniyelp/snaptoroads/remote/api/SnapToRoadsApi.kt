package com.daniyelp.snaptoroads.remote.api

import com.daniyelp.snaptoroads.remote.model.SnappedPoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface SnapToRoadsApi {

    @GET("/v1/snapToRoads")
    suspend fun getSnappedToRoadsPath(
        @Query("path") path: String,
        @Query("key") apiKey: String,
        @Query("interpolate") interpolate: Boolean = true
    ) : Response<SnappedPoints>
}