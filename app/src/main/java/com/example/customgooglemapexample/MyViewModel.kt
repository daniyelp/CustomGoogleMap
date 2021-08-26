package com.example.customgooglemapexample

import android.view.animation.Transformation
import androidx.lifecycle.*
import com.example.customgooglemapexample.util.*
import com.example.gpsbroadcastreceiver.GpsBroadcastReceiver
import com.example.internetbroadcastreceiver.InternetBroadcastReceiver
import com.example.openstreetmap.City
import com.example.openstreetmap.Osm
import com.example.snap_to_roads.SnapToRoads
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor (
    private val locationTracker: LocationTracker,
    private val gpsBroadcastReceiver: GpsBroadcastReceiver,
    private val internetBroadcastReceiver: InternetBroadcastReceiver,
    private val snapToRoads: SnapToRoads,
    private val osm: Osm
    ): ViewModel() {

    val lastLocation : LiveData<LatLng> = locationTracker.lastLocation
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

    fun zoomToFit() {
        _zoomToFit.value = true
    }

    fun zoomToFitHandled() {
        _zoomToFit.value = false
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

    val zoomToFitEnabled : LiveData<Boolean> = Transformations.map(_markers) {
        it?.let {
            it.list.size >= 2
        }
    }

    init {
        locationTracker.startLocationUpdates()
        gpsBroadcastReceiver.start()
        internetBroadcastReceiver.start()
    }

    override fun onCleared() {
        super.onCleared()
        locationTracker.stopLocationUpdates()
        gpsBroadcastReceiver.stop()
        internetBroadcastReceiver.stop()
    }

    fun onNewMarker(latLng: LatLng) {
        val list = _markers.value!!.list.toMutableList()
        list.add(latLng)
        _markers.value = Resource(list, Status.ADDED_ELEMENT)
    }

    fun undoAllMarkers() {
        _markers.value = Resource(listOf(), Status.RESETED)
    }

    fun undoAllPaths() {
        _paths.value = Resource(listOf(), Status.RESETED)
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

    fun snapToRoads() {
        val path = paths.value!!.list.last()
        viewModelScope.launch {
            val snappedPath = snapToRoads.getSnappedToRoadsPath(path)
            addPath(snappedPath, animate = true, special = true)
        }
    }

    fun osm() {
        _markers.value!!.list.last().let { latLng ->
            viewModelScope.launch {
                val (city, boundary) = osm.getCityWithBoundary(latLng.latitude, latLng.longitude)
                _displayCityName.value = DisplayResource(city, true)
                addPath(boundary, animate = true, zoomToFit = true)
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

    private fun addPath(path: List<LatLng>, animate: Boolean? = false, special: Boolean = false, zoomToFit: Boolean? = false) {
        val list = _paths.value!!.list.toMutableList()
        list.add(path)
        _paths.value = Resource(list, Status.ADDED_ELEMENT, animate = animate, special = special, zoomToFit = zoomToFit)
    }

    private fun addPaths(paths: List<List<LatLng>>, animate: Boolean = false) {
        val list = _paths.value!!.list.toMutableList()
        list.addAll(paths)
        _paths.value = Resource(list, Status.ADDED_SEVERAL_ELEMENTS, paths.size, animate = animate)
    }
}