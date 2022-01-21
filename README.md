Google Map Plus (WIP 🚧)
============
**Google Map Plus** is a library that provides a better looking version (plus some additional features) of the **GoogleMap** that is provided within Google's **Maps SDK for Android**. The latter has very dull buttons whose appearance or position cannot be changed and also lacks others features that **Google Maps** app has (the **Map type selector**).

![ezgif com-gif-maker](https://user-images.githubusercontent.com/84658876/144655476-ec46b384-c437-494e-b6b5-56a8ffbe12a1.gif)

The library contains 3 modules:

- `google-map-plus`: Contains the `GoogleMapPlus` class together with its 3 custom Views
- `open-street-map`: Provides convenient methods that retrieve data from the [Nominatim API](https://nominatim.openstreetmap.org) (see [usage](#open-street-map))
- `snap-to-roads`: Provides a convenient method to use Google's [Roads API](https://developers.google.com/maps/documentation/roads/snap)

## Setup
### Provide the gradle dependency
```
implementation 'com.github.daniyelp:CustomGoogleMap:1.0.7'
```
### Provide the API Key
In `local.properties`, add the following line containing your API key that you can obtain from [here](https://developers.google.com/maps/documentation/embed/get-api-key). If you plan on using the library's `snap-to-roads` module, make sure that your API key also has selected, besides the Maps SDK for Android API, the Roads API.

```
GMAPS_API_KEY=AI....XAB
```
## Usage
### `google-map-plus` module
Using `GoogleMapPlus` is almost the same as using the original `GoogleMap`. One very important difference between the two is that `GoogleMapPlus` must be informed of the current `LatLng` location, the status of the `GPS` and the status of the `Internet` in order for it to function properly. You must also tell `GoogleMapPlus` what's the expected milliseconds interval between the location updates that it is going to receive.

Below, any of `StatusBar`, `MapTypeSelector` or `MyLocationButton` can be omitted.

In `XML`
```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.example.custom_google_map.MapViewPlus
        android:id="@+id/map_view_plus"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <com.example.custom_google_map.StatusBar
        android:id="@+id/line_primary_status"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <com.example.custom_google_map.MapTypeSelector
        android:id="@+id/selector_map_type"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        android:layout_marginTop="60dp"/>

    <com.example.custom_google_map.MyLocationButton
        android:id="@+id/button_my_location"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />
        
</androidx.constraintlayout.widget.ConstraintLayout>
```
In `Fragment`
```kotlin
class ExampleFragment: Fragment() {

    private lateinit var googleMapPlus: MapViewPlus.GoogleMapPlus
    private val viewModel : ExampleViewModel by viewModels()
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_view_plus.myLocationButton = button_my_location
        map_view_plus.statusBar = line_primary_status
        map_view_plus.mapTypeSelector = selector_map_type
        map_view_plus.getMapAsync { googleMapPlus ->
            googleMapPlus.animationDuration = viewModel.animationDuration //e.g. 1000L
            googleMapPlus.durationBetweenLocationUpdates = viewModel.durationBetweenLocationUpdates //e.g. 2000L
            this.googleMapPlus = googleMapPlus
            selector_map_type.googleMapPlus = googleMapPlus
            with(viewModel) {
                lastLocation.observe(viewLifecycleOwner) {
                    googleMapPlus.newLatLng(it)
                }

                gpsEnabled.observe(viewLifecycleOwner) {
                    googleMapPlus.gpsOn = it
                }

                internetEnabled.observe(viewLifecycleOwner) {
                    googleMapPlus.internetOn = it
                }
                
                marker.observe(viewLifecycleOwner) { latLng ->
                    googleMapPlus.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )
                }
            }
        }
    }
}
```
Also in `Fragment` (will not work without these)
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    map_view_plus.onCreate(savedInstanceState)
}

override fun onStart() {
    super.onStart()
    map_view_plus.onStart()
}

override fun onResume() {
    super.onResume()
    map_view_plus.onResume();
}

override fun onStop() {
    super.onStop()
    map_view_plus.onStop();
}

override fun onPause() {
    super.onPause()
    map_view_plus.onPause()
}

override fun onLowMemory() {
    super.onLowMemory()
    map_view_plus.onLowMemory()
}

override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    map_view_plus.onSaveInstanceState(outState)
}
```
<a name="open-street-map"></a>
### `open-street-map` module
```kotlin
val osm = Osm()
val latLng = LatLng(..) //coordinate inside a city
viewModelScope.launch {
    val (city, boundary): Pair<String, List<LatLng>> = osm.getCityWithBoundary(latLng)
}
```

```kotlin
val osm = Osm()
val paths: List<List<LatLng>> = listOf(listOf(..), listOf(..))
viewModelScope.launch {
    val cities: List<City> = osm.splitOnCity(paths)
}
```
in which `City` is
```kotlin
data class City(val name: String, val paths: MutableList<List<LatLng>>, val boundary: List<LatLng>)
```

### `snap-to-roads` module
```kotlin
val snapToRoads = SnapToRoads(API_KEY)
val path: List<LatLng> = listOf(..)
val snappedPath = snapToRoads.getSnappedToRoadsPath(path)
```


## License
Copyright (c) 2021 daniyelp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.