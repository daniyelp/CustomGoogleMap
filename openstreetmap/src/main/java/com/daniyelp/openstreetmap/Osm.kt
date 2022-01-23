package com.daniyelp.openstreetmap

import com.daniyelp.openstreetmap.api.OsmApi
import com.daniyelp.openstreetmap.repository.OsmRepository
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class City(val name: String, val paths: MutableList<List<LatLng>>, val boundary: List<LatLng>)

class Osm {

    private val osmRepository: OsmRepository
    val DEFAULT_ZOOM = 10

    init {
        val osmApi = provideOsmApi()
        osmRepository = OsmRepository(osmApi)
    }

    suspend fun getCityWithBoundary(latLng: LatLng, zoom: Int = DEFAULT_ZOOM) =
        osmRepository.getCityWithBoundary(latLng, zoom)

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

    suspend fun splitOnCity(paths: List<List<LatLng>>, zoom: Int = DEFAULT_ZOOM) : List<City> {
        val cities = mutableListOf<City>()

        for(curPath in paths) {

            val firstLatLng = curPath[0]
            var (curCityName, curBoundary) = getCityWithBoundary(firstLatLng, zoom)
            var auxPath = mutableListOf(firstLatLng)

            fun updateOrAddCity() {
                val city = cities.filter { city -> city.name == curCityName }.firstOrNull()
                if(city == null) {
                    cities.add(City(curCityName, mutableListOf(auxPath), curBoundary))
                } else {
                    city.paths.add(auxPath)
                }
            }

            for(latLng in curPath.subList(1, curPath.size)) {
                /**
                 * We create a City instance with the auxPath we created so far, City(name, list(auxPath), boundary)
                 * But we could find this City present in our cities list, it could have been visited before
                 * In this case, we append auxPath to the paths of that already there City
                 * If this is not the case, we just add the new City instance to the cities list
                 */
                if(PolyUtil.containsLocation(latLng, curBoundary, true)) {
                    auxPath.add(latLng)
                } else {
                    updateOrAddCity()
                    auxPath = mutableListOf(latLng)
                    with(getCityWithBoundary(latLng, zoom)) {
                        curCityName = first
                        curBoundary = second
                    }
                }
            }
            updateOrAddCity()
        }

        return cities
    }
}