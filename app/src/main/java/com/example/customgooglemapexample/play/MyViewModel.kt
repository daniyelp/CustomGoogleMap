package com.example.customgooglemapexample.play

import androidx.lifecycle.*
import com.example.custom_google_map.FakeLocationTracker
import com.example.customgooglemapexample.util.*
import com.example.gpsbroadcastreceiver.GpsBroadcastReceiver
import com.example.internetbroadcastreceiver.InternetBroadcastReceiver
import com.example.openstreetmap.Osm
import com.example.snap_to_roads.SnapToRoads
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor (
    //private val locationTracker: LocationTracker,
    private val locationTracker: FakeLocationTracker,
    private val gpsBroadcastReceiver: GpsBroadcastReceiver,
    private val internetBroadcastReceiver: InternetBroadcastReceiver,
    private val snapToRoads: SnapToRoads,
    private val osm: Osm
    ): ViewModel() {

    val lastLocation : MutableLiveData<LatLng> = locationTracker.lastLocation as MutableLiveData<LatLng>
    val gpsEnabled : LiveData<Boolean> = gpsBroadcastReceiver.gpsEnabled
    val internetEnabled: LiveData<Boolean> = internetBroadcastReceiver.internetEnabled

    private val _markers = MutableLiveData(Resource<LatLng>(listOf(), Status.INITIALIZED))
    val markers : LiveData<Resource<LatLng>> = _markers

    private val _paths = MutableLiveData(Resource<List<LatLng>>(listOf(), Status.INITIALIZED))
    val paths : LiveData<Resource<List<LatLng>>> = _paths

    private val _zoomToFit = MutableLiveData<Boolean>(false)
    val zoomToFit : LiveData<Boolean> = _zoomToFit

    private val _displayCityName = MutableLiveData<DisplayResource<String>>()
    val displayCityName: LiveData<DisplayResource<String>> = _displayCityName

    fun displayedCityName() {
        _displayCityName.value?.let {
            _displayCityName.value = DisplayResource(it.t, false)
        }
    }

    private val startingIndexForPath = MutableLiveData(0)

    val connectEnabled : LiveData<Boolean> = CombinedLiveData(_markers, startingIndexForPath) {
        markers, startingIndexForPath ->
            markers?.let { markers ->
                startingIndexForPath?.let { startingIndexForPath ->
                    startingIndexForPath <= markers.list.size - 2
                }
            }
    }

    val connect2Enabled: LiveData<Boolean> = CombinedLiveData(_markers, startingIndexForPath) {
        markers, startingIndexForPath ->
            markers?.let { markers ->
                startingIndexForPath?.let { startingIndexForPath ->
                    startingIndexForPath <= markers.list.size - 1
                }
            }
    }

    val addStartMarkerEnabled : LiveData<Boolean> = Transformations.map(_paths) {
        it?.let {
            it.list.isNotEmpty()
        }
    }

    val addFinishMarkerEnabled : LiveData<Boolean> = Transformations.map(_paths) {
        it?.let {
            it.list.isNotEmpty()
        }
    }

    val snapToRoadsEnabled: LiveData<Boolean> = Transformations.map(_paths) {
        it?.let {
            it.list.isNotEmpty()
        }
    }

    val osmEnabled: LiveData<Boolean> = Transformations.map(_markers) {
        it?.let {
            it.list.isNotEmpty()
        }
    }

    val splitEnabled : LiveData<Boolean> = Transformations.map(_paths) {
        it?.let {
            it.list.isNotEmpty()
        }
    }
    val undoMarkerEnabled : LiveData<Boolean> = Transformations.map(_markers) {
        it?.let {
            it.list.isNotEmpty()
        }
    }

    val undoPathEnabled : LiveData<Boolean> = Transformations.map(_paths) {
        it?.let {
            it.list.isNotEmpty()
        }
    }

    init {
        locationTracker.startLocationUpdates()
        gpsBroadcastReceiver.start()
        internetBroadcastReceiver.start()
    }

    fun onNewLocation(latLng: LatLng) {
        lastLocation.value = latLng
    }

    override fun onCleared() {
        super.onCleared()
        locationTracker.stopLocationUpdates()
        gpsBroadcastReceiver.stop()
        internetBroadcastReceiver.stop()
    }

    fun onNewMarker(latLng: LatLng) {
        addMarker(latLng)
    }

    fun onNewStartMarker() {
        addMarker(latLng = paths.value!!.list.last().first(), amSpecialLevel = 2)
    }

    fun onNewFinishMarker() {
        addMarker(latLng = paths.value!!.list.last().last(), amSpecialLevel = 3)
    }

    fun undoAllMarkers() {
        _markers.value = Resource(listOf(), Status.RESET)
    }

    fun undoAllPaths() {
        _paths.value = Resource(listOf(), Status.RESET)
    }

    fun reset() {
        undoAllMarkers()
        undoAllPaths()
        startingIndexForPath.value = 0
    }

    fun connect(animate: Boolean = false) {
        val markers = _markers.value!!.list
        val path = markers.subList(startingIndexForPath.value!!, markers.size)
        if(path.size < 2) {
            return
        }
        startingIndexForPath.value = markers.size
        addPath(path, animate)
    }

    fun connect2(animate: Boolean = false) {
        val markers = _markers.value!!.list
        val path = mutableListOf(lastLocation.value!!)
        path.addAll(markers.subList(startingIndexForPath.value!!, markers.size))
        if(path.size < 2) {
            return
        }
        startingIndexForPath.value = markers.size
        addPath(path, animate)
    }

    fun snapToRoads() {
        val path = paths.value!!.list.last()
        viewModelScope.launch {
            val snappedPath = snapToRoads.getSnappedToRoadsPath(path)
            addPath(snappedPath, animate = true, amSpecialLevel = 1)
        }
    }

    fun osm() {
        _markers.value!!.list.last().let { latLng ->
            viewModelScope.launch {
                val (city, boundary) = osm.getCityWithBoundary(latLng.latitude, latLng.longitude)
                _displayCityName.value = DisplayResource(city, true)
                addPath(boundary, animate = true, amSpecialLevel = 1, zoomToFit = true)
            }
        }
    }

    fun split() {
        val path = paths.value!!.list.last()
        viewModelScope.launch {
            val cities = osm.splitOnCity(listOf(path))
            val paths = cities.map { it.paths }.flatten()
            //we should replace the path, so firstly we should remove the last path from the map
            undoPath()
            //then we add back the path, but split
            for ((index, path) in paths.withIndex()) {
                addPath(path, true, index % 2)
            }
        }
    }

    fun undoMarker() {
        val list = _markers.value!!.list.toMutableList()
        if(list.removeLastOrNull() != null) {
            _markers.value = Resource(list, Status.REMOVED_LAST_ELEMENT)
        }
    }

    fun undoPath() {
        val list = _paths.value!!.list.toMutableList()
        list.removeLastOrNull()?.let {
            startingIndexForPath.value = startingIndexForPath.value!!.minus(it.size)
            _paths.value = Resource(list, Status.REMOVED_LAST_ELEMENT)
        }
    }

    private fun addMarker(latLng: LatLng, amSpecialLevel: Int = 0) {
        val list = _markers.value!!.list.toMutableList()
        list.add(latLng)
        _markers.value = Resource(list, Status.ADDED_ELEMENT, amSpecialLevel = amSpecialLevel)
    }

    private fun addPath(path: List<LatLng>, animate: Boolean? = false, amSpecialLevel: Int = 0, zoomToFit: Boolean? = false) {
        val list = _paths.value!!.list.toMutableList()
        list.add(path)
        _paths.value = Resource(list, Status.ADDED_ELEMENT, animate = animate, amSpecialLevel = amSpecialLevel, zoomToFit = zoomToFit)
    }

    private fun addPaths(paths: List<List<LatLng>>, animate: Boolean = false) {
        val list = _paths.value!!.list.toMutableList()
        list.addAll(paths)
        _paths.value = Resource(list, Status.ADDED_SEVERAL_ELEMENTS, paths.size, animate = animate)
    }
}