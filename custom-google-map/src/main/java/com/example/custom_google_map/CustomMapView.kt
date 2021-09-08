package com.example.custom_google_map

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import de.p72b.maps.animation.AnimatedPolyline
import kotlinx.android.synthetic.main.map_view_custom.view.*
import java.util.*


@ExperimentalAnimationApi
class CustomMapView(context: Context, attributes: AttributeSet) : ConstraintLayout(context, attributes) {

    private class StatusController(private val onStatusChange: () -> Unit) {
        private val TAG = "STATUS_CONTROLLER"
        enum class PrimaryStatus {
            NOTHING,
            INTERNET_OFF,
            GPS_OFF,
            ACQUIRING_LOCATION,
            LOCATION_RETRIEVED
        }
        enum class InternetStatus {
            INTERNET_ON,
            INTERNET_OFF
        }
        enum class LocationStatus {
            GPS_OFF,
            ACQUIRING_LOCATION,
            LOCATION_RETRIEVED,
            CHILLING
        }

        var primaryStatus : PrimaryStatus = PrimaryStatus.NOTHING
            private set(value) {
                Log.d(TAG, "old value is ${primaryStatus.toString()}, new value is ${value}")
                if(primaryStatus != value) {
                    Log.d(TAG, "on status change called with ${value}}")
                    field = value
                    onStatusChange()
                }
            }
        var internetStatus : InternetStatus = InternetStatus.INTERNET_OFF
            private
            set(value) {
                field = value
                updatePrimaryStatus()
            }

        var locationTimer : Timer? = null
        var locationStatus : LocationStatus = LocationStatus.GPS_OFF
            private
            set(value) {
                field = value
                when(value) {
                    LocationStatus.GPS_OFF -> {
                        locationTimer?.cancel()
                        locationTimer?.purge()
                    }
                    LocationStatus.ACQUIRING_LOCATION -> {
                        locationTimer?.cancel()
                        locationTimer?.purge()
                    }
                    LocationStatus.LOCATION_RETRIEVED -> {
                        locationTimer = Timer().apply {
                            scheduleAtFixedRate(
                                object : TimerTask() {
                                    init {
                                        needNewLocation = true
                                    }
                                    override fun run() {
                                        Handler(Looper.getMainLooper()).post {
                                            if(needNewLocation) {
                                                locationStatus = LocationStatus.ACQUIRING_LOCATION
                                            } else {
                                                locationStatus = LocationStatus.CHILLING
                                            }
                                        }
                                    }
                                },
                                2000,
                                2000
                            )
                        }
                    }
                    LocationStatus.CHILLING -> {
                        locationTimer!!.cancel()
                        locationTimer!!.purge()

                        locationTimer = Timer().apply {
                            scheduleAtFixedRate(
                                object : TimerTask() {
                                    init {
                                        needNewLocation = true
                                    }
                                    override fun run() {
                                        Handler(Looper.getMainLooper()).post {
                                            if(needNewLocation) {
                                                locationStatus = LocationStatus.ACQUIRING_LOCATION
                                            } else {
                                                needNewLocation = true
                                            }
                                        }
                                    }
                                },
                                2000,
                                2000
                            )
                        }
                    }
                }
                updatePrimaryStatus()
            }

        var internetOn = false
            set(value) {
                field = value
                internetStatus = if(value) InternetStatus.INTERNET_ON else InternetStatus.INTERNET_OFF
            }

        private var needNewLocation = false

        var gpsOn = false
            set(value) {
                field = value
                if(!value) {
                    locationStatus = LocationStatus.GPS_OFF
                } else {
                    when(locationStatus) {
                        LocationStatus.GPS_OFF -> {
                            locationStatus = LocationStatus.ACQUIRING_LOCATION
                        }
                        LocationStatus.ACQUIRING_LOCATION -> {} //no action
                        LocationStatus.LOCATION_RETRIEVED -> {} //no action
                    }
                }
            }

        private fun updatePrimaryStatus() {
            when(locationStatus) {
                LocationStatus.GPS_OFF -> primaryStatus = PrimaryStatus.GPS_OFF
                LocationStatus.ACQUIRING_LOCATION -> primaryStatus = PrimaryStatus.ACQUIRING_LOCATION
                LocationStatus.LOCATION_RETRIEVED -> primaryStatus = PrimaryStatus.LOCATION_RETRIEVED
                LocationStatus.CHILLING -> {
                    if (internetStatus == InternetStatus.INTERNET_OFF) {
                        primaryStatus = PrimaryStatus.INTERNET_OFF
                    }
                }
            }
        }

        fun newLocationAvailable() {
            needNewLocation = false
            if(locationStatus == LocationStatus.ACQUIRING_LOCATION) {
                locationStatus = LocationStatus.LOCATION_RETRIEVED
            }
        }
    }

    inner class CustomGoogleMap constructor(val googleMap: GoogleMap) {

        private val statusController: StatusController

        var mapType : Int = GoogleMap.MAP_TYPE_NORMAL
            set(value) {
                field = value
                googleMap.mapType = value
            }

        private var locationMarker: Marker? = null

        private var lastLatLng: LatLng? = null
            set(value) {
                field = value

                statusController.newLocationAvailable()
                updateCamera()
                updateMarker()
            }

        var gpsOn: Boolean = false
            set(value) {
                field = value
                statusController.gpsOn = value
            }

        var internetOn: Boolean = false
            set(value) {
                field = value
                statusController.internetOn = value
            }

        init {
            googleMap.uiSettings.isCompassEnabled = false
            googleMap.uiSettings.isMyLocationButtonEnabled = false
            googleMap.uiSettings.isMapToolbarEnabled = false

            googleMap.setOnCameraMoveStartedListener {
                if(it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    myLocationButton?.decenter()
                }
            }

            myLocationButton?.onCenter = {
                updateCamera()
            }

            statusController = StatusController {
                updateMarker()
                updateStatusLine()
            }

        }

        @ExperimentalAnimationApi
        private fun updateStatusLine() {
            Log.d("STATUS_CONTROLLER", "updateLine with primary status ${statusController.primaryStatus}")
            when(statusController.primaryStatus) {
                StatusController.PrimaryStatus.INTERNET_OFF -> statusBar?.display("INTERNET IS OFF", R.color.gray, true)
                StatusController.PrimaryStatus.GPS_OFF -> statusBar?.display("GPS IS OFF", R.color.red, true)
                StatusController.PrimaryStatus.ACQUIRING_LOCATION -> statusBar?.display("ACQUIRING LOCATION", R.color.blue, true)
                StatusController.PrimaryStatus.LOCATION_RETRIEVED -> statusBar?.display("LOCATION ACQUIRED", R.color.green, false)
                else -> {}
            }
        }

        private fun updateMarker() {

            if(lastLatLng == null) {
                return
            }

            if(locationMarker == null) {
                val icon = vectorToBitmapDescriptor(
                    R.drawable.ic_location,
                    when (statusController.primaryStatus) {
                        StatusController.PrimaryStatus.LOCATION_RETRIEVED -> android.graphics.Color.BLUE
                        else -> android.graphics.Color.LTGRAY
                    },
                    resources
                )

                val position = lastLatLng!!

                //create a new marker at lastLatLng and with the color based on tracking
                val markerOptions = MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(position)
                    .icon(icon)

                locationMarker = googleMap.addMarker(markerOptions)

            } else {
                animateMarker(locationMarker!!, lastLatLng!!)
            }

        }

        private fun updateCamera() {
            if(lastLatLng == null) {
                return
            }

            if(myLocationButton?.center == true) {
                zoomTo(lastLatLng!!, true)
            }
        }

        fun newLatLng(latLng: LatLng) {
            lastLatLng = latLng
        }

        private fun getBoundsToFit(path: List<LatLng>) : LatLngBounds {
            val boundsBuilder = LatLngBounds.builder()
            for (latLng in path) {
                boundsBuilder.include(latLng)
            }
            return boundsBuilder.build()
        }

        fun onSnapshotReady(onSnapshotReadyCallback : (Bitmap) -> Unit) {
            googleMap.snapshot {
                onSnapshotReadyCallback(it)
            }
        }

        private var markers = mutableListOf<Marker>()

        fun addAsMarker(latLng: LatLng) {
            val marker = googleMap.addMarker(MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker((markers.size * 10).toFloat() % 360)))
                .also { it.showInfoWindow()}
            markers.add(marker)
        }

        fun addAsMarkers(latLngs: List<LatLng>) {
            latLngs.forEach {
                addAsMarker(it)
            }
        }

        fun removeMarkers() {
            markers.forEach{it.remove()}
            markers = mutableListOf()
        }

        fun removeLastMarkerIfAny() {
            var marker = markers.removeLastOrNull()
            marker?.remove()
        }

        private var polylines = mutableListOf<AnimatedPolyline>()

        fun getDefaultPolylineOptions() = PolylineOptions()
            .color(android.graphics.Color.BLACK)
            .startCap(RoundCap())
            .endCap(RoundCap())

        fun addPath(
            path: List<LatLng>,
            animated: Boolean = false,
            polylineOptions: PolylineOptions = getDefaultPolylineOptions()
        ) {

            if(path.size in listOf(0, 1))
                return

            val polyline = AnimatedPolyline(
                googleMap,
                path,
                polylineOptions,
                if(animated) 1000 else 0
            ).also { it.start() }
            polylines.add(polyline)
        }

        fun addPaths(paths: List<List<LatLng>>, animated: Boolean = false, polylineOptions: PolylineOptions = getDefaultPolylineOptions()) {
            paths.forEach {
                addPath(it, animated, polylineOptions)
            }
        }

        fun removePaths() {
            polylines.forEach {
                it.remove()
            }
            polylines = mutableListOf()
        }

        fun removeLastPath() {
            val polyline = polylines.removeLastOrNull()
            polyline?.remove()
        }

        fun setOnMapLongClickListener(f : (latLng: LatLng) -> Unit) {
            googleMap.setOnMapLongClickListener {
                f(it)
            }
        }
        fun setOnMapClickListener(f : (latLng: LatLng) -> Unit) {
            googleMap.setOnMapClickListener {
                f(it)
            }
        }

        @JvmName("zoomToFit1")
        fun zoomToFit(path: List<LatLng>, animated: Boolean = false) {
            if(animated) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getBoundsToFit(path), 20))
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBoundsToFit(path), 20))
            }
        }

        fun zoomToFit(paths: List<List<LatLng>>, animated: Boolean = false) {
            val pathsFlattened = paths.flatten()
            zoomToFit(pathsFlattened, animated)
        }

        private var zooming = false
        fun zoomTo(latLng: LatLng, animated: Boolean = false) {
            if(animated) {
                if(!zooming) {
                    zooming = true
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(latLng, 16f),
                        object : GoogleMap.CancelableCallback {
                            override fun onFinish() {
                                zooming = false
                            }
                            override fun onCancel() {
                                zooming = false
                            }
                        }
                    )
                }
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            }
        }

    }

    private lateinit var customGoogleMap: CustomGoogleMap

    var myLocationButton: MyLocationButton? = null
    var statusBar: StatusBar? = null
    var mapTypeSelector: MapTypeSelector? = null

    init {
        inflate(context, R.layout.map_view_custom, this)
    }

    fun getCustomMapAsync(onCustomMapReadyCallback : (CustomGoogleMap) -> Unit) {
        map_view.getMapAsync {
            customGoogleMap = CustomGoogleMap(it)
            onCustomMapReadyCallback(customGoogleMap)
        }
    }

    fun onCreate(savedInstanceState: Bundle?) {
        map_view.onCreate(savedInstanceState)

    }

    fun onStart() {
        map_view.onStart()
    }

    fun onResume() {
        map_view.onResume();
    }

    fun onStop() {
        map_view.onStop();
    }

    fun onPause() {
        map_view.onPause()
    }

    fun onLowMemory() {
        map_view.onLowMemory()
    }

    fun onDestroy() {
        map_view.onDestroy()
    }

    fun onSaveInstanceState(outState: Bundle) {
        map_view.onSaveInstanceState(outState)
    }
}