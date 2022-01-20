package com.example.custom_google_map

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import de.p72b.maps.animation.AnimatedPolyline
import kotlinx.android.synthetic.main.map_view_custom.view.*
import java.util.*

@ExperimentalAnimationApi
class MapViewPlus: ConstraintLayout {

    constructor(context: Context): super(context)
    constructor(context: Context, attributes: AttributeSet): super(context, attributes)

    private class StatusController(var durationBetweenLocationUpdates: Long, private val onStatusChange: () -> Unit) {
        enum class PrimaryStatus {
            NOTHING,
            INTERNET_OFF,
            GPS_OFF,
            ACQUIRING_LOCATION,
            LOCATION_RETRIEVED,
            CHILLING
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
                if(primaryStatus != value) {
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
                                durationBetweenLocationUpdates,
                                durationBetweenLocationUpdates
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
                                durationBetweenLocationUpdates,
                                durationBetweenLocationUpdates
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
            primaryStatus = when(locationStatus) {
                LocationStatus.GPS_OFF -> PrimaryStatus.GPS_OFF
                LocationStatus.ACQUIRING_LOCATION -> PrimaryStatus.ACQUIRING_LOCATION
                LocationStatus.LOCATION_RETRIEVED -> PrimaryStatus.LOCATION_RETRIEVED
                LocationStatus.CHILLING -> {
                    if (internetStatus == InternetStatus.INTERNET_OFF) {
                        PrimaryStatus.INTERNET_OFF
                    } else {
                        PrimaryStatus.CHILLING
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

    inner class GoogleMapPlus constructor(val googleMap: GoogleMap) {

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
                updateLocationMarker()
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

        var animationDuration = 1000L

        var durationBetweenLocationUpdates = 2000L
            set(value) {
                field = value
                statusController.durationBetweenLocationUpdates = value
            }

        var darkThemeOn: Boolean = false
            set(value) {
                field = value
                googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context,
                        if(value) R.raw.dark_map_style else R.raw.standard_map_style
                    )
                )
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

            statusController = StatusController(durationBetweenLocationUpdates) {
                updateLocationMarker()
                updateStatusLine()
            }

        }

        @ExperimentalAnimationApi
        private fun updateStatusLine() {
            when(statusController.primaryStatus) {
                StatusController.PrimaryStatus.INTERNET_OFF -> statusBar?.display("INTERNET IS OFF", R.color.gray, true)
                StatusController.PrimaryStatus.GPS_OFF -> statusBar?.display("GPS IS OFF", R.color.red, true)
                StatusController.PrimaryStatus.ACQUIRING_LOCATION -> statusBar?.display("ACQUIRING LOCATION", R.color.blue, true)
                StatusController.PrimaryStatus.LOCATION_RETRIEVED -> statusBar?.display("LOCATION ACQUIRED", R.color.green, false)
                StatusController.PrimaryStatus.CHILLING -> statusBar?.hide(now = false)
                else -> {}
            }
        }

        private val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

        private var penultimateLocationStatus: StatusController.LocationStatus? = null

        private fun updateLocationMarker() {

            fun getIcon(color: Int) = vectorToBitmapDescriptor(
                R.drawable.ic_location,
                color,
                resources
            )

            fun updateMarkerColor(marker: Marker, color: Int) {
                marker.setIcon(getIcon(color))
            }

            fun animateMarker(marker: Marker, to: LatLng, duration: Long = 1000) {
                valueAnimator.removeAllUpdateListeners()
                valueAnimator.end()
                valueAnimator.cancel()

                val from = marker.position

                fun interpolate(t: Float, a: LatLng, b: LatLng) =
                    LatLng (
                        a.latitude * (1 - t) + b.latitude * t,
                        a.longitude * (1 - t) + b.longitude * t
                    )

                with(valueAnimator) {
                    this.duration = duration
                    interpolator = LinearInterpolator()

                    addUpdateListener(object : ValueAnimator.AnimatorUpdateListener{
                        override fun onAnimationUpdate(valueAnimator: ValueAnimator?) {
                            valueAnimator?.let {
                                val t = it.animatedFraction
                                val latLng = interpolate(t, from, to)
                                marker.position = latLng
                            }
                        }
                    })
                    start()
                }
            }

            fun getColor() = when (statusController.locationStatus) {
                StatusController.LocationStatus.LOCATION_RETRIEVED -> android.graphics.Color.BLUE
                StatusController.LocationStatus.CHILLING -> android.graphics.Color.BLUE
                else -> android.graphics.Color.LTGRAY
            }

            fun getPosition() = lastLatLng!!

            fun shouldAnimate() =
                !( penultimateLocationStatus == StatusController.LocationStatus.ACQUIRING_LOCATION && statusController.locationStatus == StatusController.LocationStatus.LOCATION_RETRIEVED)

            if(lastLatLng == null) return

            if(locationMarker == null) {
                //create a new marker at lastLatLng and with the color based on tracking
                val markerOptions = MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(getPosition())
                    .icon(getIcon(getColor()))
                locationMarker = googleMap.addMarker(markerOptions)
            } else {
                updateMarkerColor(locationMarker!!, getColor())
                if(shouldAnimate()) {
                    animateMarker(locationMarker!!, getPosition(), duration = animationDuration)
                } else {
                    locationMarker!!.position = getPosition()
                }
            }

            penultimateLocationStatus = statusController.locationStatus
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

        fun addPath(
            path: List<LatLng>,
            animated: Boolean = false,
            polylineOptions: PolylineOptions = getDefaultPolylineOptions()
        ): AnimatedPolyline {
            val polyline = AnimatedPolyline(
                googleMap,
                path,
                polylineOptions,
                if(animated) animationDuration else 0
            ).also { it.start() }

            return polyline
        }

        fun getDefaultPolylineOptions() = PolylineOptions()
            .color(android.graphics.Color.BLACK)
            .startCap(RoundCap())
            .endCap(RoundCap())

        fun addPaths(
            paths: List<List<LatLng>>,
            animated: Boolean = false,
            polylineOptions: PolylineOptions = getDefaultPolylineOptions()
        ): List<AnimatedPolyline> = paths.map { addPath(it, animated, polylineOptions) }

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
        fun zoomToFit(path: List<LatLng>, animated: Boolean = false, paddingPx: Int = 50) {
            fun getBoundsToFit(path: List<LatLng>) : LatLngBounds {
                val boundsBuilder = LatLngBounds.builder()
                for (latLng in path) {
                    boundsBuilder.include(latLng)
                }
                return boundsBuilder.build()
            }
            if(animated) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getBoundsToFit(path), paddingPx))
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBoundsToFit(path), paddingPx))
            }
        }

        fun zoomToFit(paths: List<List<LatLng>>, animated: Boolean = false, paddingPx: Int = 50) {
            val pathsFlattened = paths.flatten()
            zoomToFit(pathsFlattened, animated, paddingPx)
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

        fun addMarker(markerOptions: MarkerOptions) = googleMap.addMarker(markerOptions)
    }

    private lateinit var googleMapPlus: GoogleMapPlus

    var myLocationButton: MyLocationButton? = null
    var statusBar: StatusBar? = null
    var mapTypeSelector: MapTypeSelector? = null

    init {
        inflate(context, R.layout.map_view_custom, this)
    }

    fun getMapAsync(onCustomMapReadyCallback : (GoogleMapPlus) -> Unit) {
        map_view.getMapAsync {
            googleMapPlus = GoogleMapPlus(it)
            onCustomMapReadyCallback(googleMapPlus)
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