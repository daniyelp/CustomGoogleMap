package com.example.openstreetmap

import com.example.openstreetmap.api.OsmApi
import com.example.openstreetmap.repository.OsmRepository
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Osm {

    private val osmRepository: OsmRepository

    init {
        val osmApi = provideOsmApi()
        osmRepository = OsmRepository(osmApi)
    }

    suspend fun getCityWithBoundary(lat: Double, lon: Double) : Pair<String, List<LatLng>> {
        return osmRepository.getCityWithBoundary(lat, lon)
    }

    private fun provideOsmApi() : OsmApi {
        val client = OkHttpClient.Builder()
            .build()

        return Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(OsmApi::class.java)
    }
}