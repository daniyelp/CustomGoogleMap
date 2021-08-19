package com.example.snap_to_roads

import com.example.snap_to_roads.remote.api.SnapToRoadsApi
import com.example.snap_to_roads.remote.repository.SnapToRoadsRepository
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SnapToRoads(private val apiKey: String) {

    private val snapToRoadsRepository: SnapToRoadsRepository

    init {
        val snapToRoadsApi = provideSnapToRoadsApi()
        snapToRoadsRepository = provideSnapToRoadsRepository(snapToRoadsApi, apiKey)
    }

    suspend fun getSnappedToRoadsPath(path: List<LatLng>): List<LatLng> {
        return snapToRoadsRepository.getSnappedToRoadsPath(path)
    }

    private fun provideSnapToRoadsApi(): SnapToRoadsApi {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://roads.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(SnapToRoadsApi::class.java)
    }

    private fun provideSnapToRoadsRepository(snapToRoadsApi: SnapToRoadsApi, apiKey: String) : SnapToRoadsRepository {
        return SnapToRoadsRepository(snapToRoadsApi, apiKey)
    }
}