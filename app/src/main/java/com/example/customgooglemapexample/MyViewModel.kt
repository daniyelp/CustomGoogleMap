package com.example.customgooglemapexample

import android.os.Handler
import androidx.lifecycle.*
import com.example.customgooglemapexample.util.*
import com.example.gpsbroadcastreceiver.GpsBroadcastReceiver
import com.example.internetbroadcastreceiver.InternetBroadcastReceiver
import com.example.openstreetmap.Osm
import com.example.snap_to_roads.SnapToRoads
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class MyViewModel @Inject constructor (
    private val locationTracker: LocationTracker,
    private val gpsBroadcastReceiver: GpsBroadcastReceiver,
    private val internetBroadcastReceiver: InternetBroadcastReceiver,
    private val snapToRoads: SnapToRoads,
    private val osm: Osm
    ): ViewModel() {

    val _lastMockLocation = MutableLiveData<LatLng>()
    val lastMockLocation: LiveData<LatLng> = _lastMockLocation

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

    init {
        locationTracker.startLocationUpdates()
        gpsBroadcastReceiver.start()
        internetBroadcastReceiver.start()

        mockPath()
    }

    override fun onCleared() {
        super.onCleared()
        locationTracker.stopLocationUpdates()
        gpsBroadcastReceiver.stop()
        internetBroadcastReceiver.stop()
    }

    private fun mockPath() {
        val mockLatLngs = listOf(
            LatLng(46.768771764277,23.609049841761585), LatLng(46.76845760834686,23.609049171209335), LatLng(46.76826677150349,23.609121926128868), LatLng(46.76804607967789,23.60913299024105), LatLng(46.76784375890598,23.60922083258629), LatLng(46.767581039312134,23.609315380454063), LatLng(46.76736126328297,23.609449155628685), LatLng(46.76707167197351,23.60970765352249), LatLng(46.76683283260867,23.60988937318325), LatLng(46.76661098663457,23.610065057873726), LatLng(46.766420832215324,23.610198833048344), LatLng(46.76623159575003,23.610360436141494), LatLng(46.766023756390474,23.6104653775692), LatLng(46.765838193058165,23.610623963177204), LatLng(46.76562277343745,23.610873408615586), LatLng(46.765403678398016,23.611051440238953), LatLng(46.76506171381456,23.61140113323927), LatLng(46.76472870390697,23.61173674464226), LatLng(46.76454221745955,23.611930198967457), LatLng(46.76424020909719,23.61225239932537), LatLng(46.76396874462241,23.612418025732037)
        )
        thread(start = true) {
            while(true) {
                mockLatLngs.forEach {
                    _lastMockLocation.postValue(it)
                    Thread.sleep(1000)
                }
            }
        }
    }

    fun onNewMockLocation(latLng: LatLng) {
        _lastMockLocation.value = latLng
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

    //FIXME: the osm.splitOnCity seems not to work correctly
    // for a specific example, it gave back two cities, as it should have,
    // but there were duplicated paths for each city
    fun split() {
        val path = paths.value!!.list.last()
        viewModelScope.launch {
            val cities = osm.splitOnCity(listOf(path))
            val paths = cities.map { it.paths }.flatten()
            //we should replace the path, so firstly we should remove the last path from the map
            undoPath()
            //then we add back the path, but split
            for ((index, path) in paths.withIndex()) {
                addPath(path, true, index % 2 == 0)
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