package com.example.custom_google_map

import android.animation.Animator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.updateLayoutParams
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.line_gps_status.view.*
import kotlinx.android.synthetic.main.map_view_custom.view.*
import java.util.*


class CustomMapView(context: Context, attributes: AttributeSet) : ConstraintLayout(context, attributes) {

    class StatusController(private val onStatusChange: (LocationStatus) -> Unit) {

        enum class LocationStatus {
            OFF,
            ON,
            ACQUIRING_LOCATION
        }

        private var needNewLocation = false

        var status = LocationStatus.OFF
        private set(value) {
            field = value
            if(value == LocationStatus.ON) {

                Timer().scheduleAtFixedRate(
                    object : TimerTask() {
                        init {
                            needNewLocation = true
                        }
                        override fun run() {
                            Handler(Looper.getMainLooper()).post {
                                if(needNewLocation) {
                                    status = LocationStatus.ACQUIRING_LOCATION
                                    cancel()
                                } else {
                                    needNewLocation = true
                                }
                            }
                        }
                    },
                    3900,
                    4000
                )
            }
            onStatusChange(value)
        }

        var gpsActive = false
            set(value) {
                field = value
                when(status) {
                    LocationStatus.OFF -> {
                        if(gpsActive) {
                           status = LocationStatus.ACQUIRING_LOCATION
                        }
                    }
                    LocationStatus.ON -> {
                        if(!gpsActive) {
                            status = LocationStatus.OFF
                        }
                    }
                    LocationStatus.ACQUIRING_LOCATION -> {
                        if(!gpsActive) {
                            status = LocationStatus.OFF
                        }
                    }
                }
            }

        fun newLocationAvailable() {
            needNewLocation = false
            if(status == LocationStatus.ACQUIRING_LOCATION) {
                status = LocationStatus.ON
            }
        }
    }

    inner class CustomGoogleMap constructor(private val googleMap: GoogleMap) {

        private val statusController: StatusController

        init {
            googleMap.setOnCameraMoveStartedListener {
                if(it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    decenter()
                }
            }

            btn_my_location.setOnClickListener {
                onCenter()
            }

            statusController = StatusController {
                updateMarker()
                updateLine()
            }

            fab_map_type.setOnClickListener { fab ->
                layout_map_type.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setDuration(200)
                        .setListener( object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator?) {
                                fab.visibility = View.INVISIBLE
                            }

                            override fun onAnimationEnd(animation: Animator?) {

                            }

                            override fun onAnimationCancel(animation: Animator?) {

                            }

                            override fun onAnimationRepeat(animation: Animator?) {

                            }

                        }
                        )
                }
            }

            googleMap.setOnMapClickListener {
                // Conduct the animation if the FAB is invisible (window open)
                if (fab_map_type.visibility == View.INVISIBLE) {
                    layout_map_type
                        .animate()
                        .alpha(0f)
                        .setDuration(200)
                        .setListener( object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator?) {
                                fab_map_type.visibility = View.VISIBLE
                            }

                            override fun onAnimationEnd(animation: Animator?) {

                            }

                            override fun onAnimationCancel(animation: Animator?) {

                            }

                            override fun onAnimationRepeat(animation: Animator?) {

                            }

                        })
                }
            }

            val oldColor = text_map_type_default.currentTextColor

            fun selectTerrain() {
                btn_map_type_terrain.setBackgroundResource(R.drawable.background_map_type_btn_post_click)
                btn_map_type_default.setBackgroundResource(R.drawable.background_map_type_btn)
                btn_map_type_satellite.setBackgroundResource(R.drawable.background_map_type_btn)
                googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                text_map_type_terrain.setTextColor(Color.BLUE)
                text_map_type_default.setTextColor(oldColor)
                text_map_type_satellite.setTextColor(oldColor)
            }

            fun selectDefault() {
                btn_map_type_default.setBackgroundResource(R.drawable.background_map_type_btn_post_click)
                btn_map_type_terrain.setBackgroundResource(R.drawable.background_map_type_btn)
                btn_map_type_satellite.setBackgroundResource(R.drawable.background_map_type_btn)
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                text_map_type_default.setTextColor(Color.BLUE)
                text_map_type_satellite.setTextColor(oldColor)
                text_map_type_terrain.setTextColor(oldColor)
            }

            fun selectSatellite() {
                btn_map_type_satellite.setBackgroundResource(R.drawable.background_map_type_btn_post_click)
                btn_map_type_default.setBackgroundResource(R.drawable.background_map_type_btn)
                btn_map_type_terrain.setBackgroundResource(R.drawable.background_map_type_btn)
                googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                text_map_type_satellite.setTextColor(Color.BLUE)
                text_map_type_default.setTextColor(oldColor)
                text_map_type_terrain.setTextColor(oldColor)
            }

            btn_map_type_terrain.setOnClickListener {
                selectTerrain()
            }
            btn_map_type_default.setOnClickListener {
                selectDefault()
            }
            btn_map_type_satellite.setOnClickListener {
                selectSatellite()
            }

            selectDefault()

        }

        private var locationMarker: Marker? = null

        private var center = true
            set(value) {
                field = value

                updateButtonState()
                updateCamera()
            }

        private var lastLatLng: LatLng? = null
            set(value) {
                field = value

                statusController.newLocationAvailable()
                updateCamera()
                updateMarker()
            }

        var available: Boolean = false
            set(value) {
                field = value
                statusController.gpsActive = value
            }

        private fun updateLine() {
            when(statusController.status) {
                StatusController.LocationStatus.ON -> {
                    Log.d("ANIMATION", "location on")
                    line_gps_status.slide("GPS ON", Color.GREEN)
                }
                StatusController.LocationStatus.OFF -> {
                    line_gps_status.slide("GPS OFF", Color.RED, true)
                }
                StatusController.LocationStatus.ACQUIRING_LOCATION -> {
                    line_gps_status.slide("ACQUIRING LOCATION", Color.BLUE, true)
                }
            }
        }

        private fun updateMarker() {

            if(lastLatLng == null) {
                return
            }

            //delete last marker
            locationMarker?.remove()

            //create a new marker at lastLatLng and with the color based on tracking
            val markerOptions = MarkerOptions()
                //.flat(true)
                .position(lastLatLng!!)
                .icon(vectorToBitmap(
                    R.drawable.ic_location,
                    when(statusController.status) {
                        StatusController.LocationStatus.ON -> Color.BLUE
                        else -> Color.LTGRAY
                    }))

            locationMarker = googleMap.addMarker(markerOptions)

        }

        private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor? {
            val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
            val bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            DrawableCompat.setTint(vectorDrawable, color)
            vectorDrawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

        private fun updateCamera() {
            if(lastLatLng == null) {
                return
            }

            if(center) {
                animatedZoomTo(lastLatLng!!)
            }
        }

        private fun updateButtonState() {
            if (center) {
                btn_my_location.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_my_location_blue,
                        null
                    )
                )
            } else {
                btn_my_location.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_my_location_black,
                        null
                    )
                )
            }
        }

        fun onCenter() {
            center = !center
        }

        private fun decenter() {
            center = false
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

        fun animatedZoomToFit(path: List<LatLng>) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getBoundsToFit(path), 20))
        }

        fun instantZoomToFit(path: List<LatLng>) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBoundsToFit(path), 20))
        }

        fun instantZoomTo(latLng: LatLng) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
        fun animatedZoomTo(latLng: LatLng) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }

    }

    private lateinit var customGoogleMap: CustomGoogleMap

    init {
        inflate(context, R.layout.map_view_custom, this)
        val a = context.obtainStyledAttributes(attributes, R.styleable.CustomMapView)
        var pos = a.getInt(R.styleable.CustomMapView_myLocationButtonPosition, 1)
        btn_my_location.updateLayoutParams<LayoutParams> {
            when(pos) {
                0 -> {
                    leftToLeft = R.id.layout_custom_map
                    topToTop = R.id.layout_custom_map
                }
                1 -> {
                    rightToRight = R.id.layout_custom_map
                    topToBottom = R.id.layout_map_type
                }
                2 -> {
                    rightToRight = R.id.layout_custom_map
                    bottomToBottom = R.id.layout_custom_map
                }
                3 -> {
                    leftToLeft = R.id.layout_custom_map
                    bottomToBottom = R.id.layout_custom_map
                } else -> {
                    rightToRight = R.id.layout_custom_map
                    bottomToBottom = R.id.layout_custom_map
                }
            }
        }
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