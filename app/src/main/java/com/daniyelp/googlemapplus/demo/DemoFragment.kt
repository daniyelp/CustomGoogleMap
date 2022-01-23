package com.daniyelp.googlemapplus.demo

import android.app.Instrumentation
import android.content.Context
import android.net.wifi.WifiManager
import android.os.*
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.fragment.app.Fragment
import com.daniyelp.googlemapplus.MapViewPlus
import com.example.customgooglemapexample.BuildConfig
import com.example.customgooglemapexample.R
import com.daniyelp.openstreetmap.Osm
import com.daniyelp.snaptoroads.SnapToRoads
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_demo.*
import kotlinx.coroutines.*

@ExperimentalAnimationApi
class DemoFragment: Fragment(R.layout.fragment_demo) {

    private lateinit var googleMapPlus: MapViewPlus.GoogleMapPlus
    private lateinit var snapToRoads : SnapToRoads
    private lateinit var osm: Osm

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_custom_demo.myLocationButton = button_my_location_demo
        map_custom_demo.statusBar = line_primary_status_demo
        map_custom_demo.mapTypeSelector = selector_map_type_demo
        map_custom_demo.getMapAsync {
            googleMapPlus = it
            snapToRoads = SnapToRoads(BuildConfig.GMAPS_API_KEY)
            osm = Osm()
            startDemo()
        }
    }

    @Volatile var run2: Boolean = false
    private val berlin0 = listOf(LatLng(46.772360314614176,23.591783791780472), LatLng(46.77239682580008,23.592027202248577), LatLng(46.77248615180462,23.59231822192669), LatLng(46.772577314695056,23.59260488301516))
    private val berlin1 = listOf(LatLng(46.77242001842731,23.592956922948364), LatLng(46.77204732777236,23.59308566898107), LatLng(46.77167899756709,23.59283622354269), LatLng(46.7714557937481,23.591900467872623), LatLng(46.771214447837146,23.59104953706265))
    private val berlin2 = listOf(LatLng(52.315510491823886,9.973794557154179), LatLng(52.31560723217582,9.974149279296398), LatLng(52.31571483507311,9.974513053894043), LatLng(52.315810140277904,9.974989145994186), LatLng(52.31587859593281,9.975292570888996), LatLng(52.315998290595886,9.975691549479961), LatLng(52.31610322811642,9.976103939116001), LatLng(52.31625530509715,9.97660618275404), LatLng(52.3163450753607,9.977040700614452), LatLng(52.31648485426703,9.9775804951787), LatLng(52.31662319805783,9.978088103234768))

    @DelicateCoroutinesApi
    private fun startDemo() {

        val myLocationButtonX: Float = BuildConfig.MY_LOCATION_BUTTON_X
        val myLocationButtonY: Float = BuildConfig.MY_LOCATION_BUTTON_Y

        val mapTypeSelectorX = BuildConfig.MAP_TYPE_SELECTOR_X
        val mapTypeSelectorY = BuildConfig.MAP_TYPE_SELECTOR_Y

        val defaultMapTypeX = BuildConfig.DEFAULT_MAP_TYPE_X
        val defaultMapTypeY = BuildConfig.DEFAULT_MAP_TYPE_Y

        val terrainMapTypeX = BuildConfig.TERRAIN_MAP_TYPE_X
        val terrainMapTypeY = BuildConfig.TERRAIN_MAP_TYPE_Y

        val satelliteMapTypeX = BuildConfig.SATELLITE_MAP_TYPE_X
        val satelliteMapTypeY = BuildConfig.SATELLITE_MAP_TYPE_Y

        GlobalScope.launch {
            googleMapPlus.durationBetweenLocationUpdates = 1200L
            googleMapPlus.internetOn = true
            googleMapPlus.gpsOn = false

            delay(1700)

            googleMapPlus.gpsOn = true

            delay(1000)

            touch(myLocationButtonX, myLocationButtonY)

            delay(500)

            googleMapPlus.animationDuration = 500

            requireActivity().runOnUiThread{
                googleMapPlus.newLatLng(berlin0[0])
            }

            delay(1000)

            requireActivity().runOnUiThread{
                googleMapPlus.newLatLng(berlin0[0])
            }

            for(i in 1 until berlin0.size) {
                delay(1000)
                requireActivity().runOnUiThread{
                    googleMapPlus.newLatLng(berlin0[i])
                    googleMapPlus.addPath(listOf(berlin0[i - 1], berlin0[i]), animated = true)
                }
            }

            var run = true

            async {
                while(run) {
                    delay(1000)
                    requireActivity().runOnUiThread{ googleMapPlus.newLatLng(berlin0.last()) }
                }
            }

            delay(1500)

            fling(500f ,501f ,530f,800f, 5)

            delay(500)

            touch(myLocationButtonX, myLocationButtonY)

            delay (1100)

            touch(mapTypeSelectorX, mapTypeSelectorY)

            delay(1000)

            touch(terrainMapTypeX, terrainMapTypeY)

            delay(1000)

            touch(satelliteMapTypeX, satelliteMapTypeY)

            delay(1000)

            touch(defaultMapTypeX, defaultMapTypeY)

            run = false

            delay(1000)

            touch(mapTypeSelectorX, mapTypeSelectorY)

            delay(4000)

            googleMapPlus.animationDuration = 400

            requireActivity().runOnUiThread{ googleMapPlus.newLatLng(berlin1[0]) }

            delay(700)

            for(i in 1 until berlin1.size) {
                requireActivity().runOnUiThread{
                    googleMapPlus.newLatLng(berlin1[i])
                    googleMapPlus.addPath(listOf(berlin1[i - 1], berlin1[i]), animated = true)
                }
                delay(700)
            }

            touch(myLocationButtonX, myLocationButtonY)

            run2 = true

            delay(700)

            async {
                while(run2) {
                    requireActivity().runOnUiThread{ googleMapPlus.newLatLng(berlin1.last()) }
                    delay(700)
                }
            }

            delay(400)

            requireActivity().runOnUiThread {
                googleMapPlus.zoomToFit(berlin1, animated = true)
            }

            delay(400)

            val snappedPath = snapToRoads.getSnappedToRoadsPath(berlin1)

            googleMapPlus.animationDuration = 1000
            requireActivity().runOnUiThread{
                googleMapPlus.addPath(
                    snappedPath,
                    animated = true,
                    polylineOptions = googleMapPlus.getDefaultPolylineOptions().color(android.graphics.Color.RED)
                )
            }

            delay(800)

            requireActivity().runOnUiThread {
                googleMapPlus.zoomToFit(snappedPath, animated = true, paddingPx = 150)
            }

            delay(800)

            requireActivity().runOnUiThread() {
                googleMapPlus.addMarker(
                    MarkerOptions()
                        .position(snappedPath.first())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
            }

            delay(500)

            requireActivity().runOnUiThread() {
                googleMapPlus.addMarker(
                    MarkerOptions()
                        .position(snappedPath.last())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
            }

            delay(500)

            val latLng = snappedPath.first()
            val (city, boundary) = osm.getCityWithBoundary(latLng)

            requireActivity().runOnUiThread() {
            googleMapPlus.zoomToFit(boundary, animated = true)
            }

            delay(2300)

            googleMapPlus.animationDuration = 2000
            requireActivity().runOnUiThread() {
            //Toast.makeText(requireActivity(), city, Toast.LENGTH_SHORT).show()
            googleMapPlus.addPath(boundary, animated = true, polylineOptions = googleMapPlus.getDefaultPolylineOptions().color(android.graphics.Color.RED))
            }

            delay(1000)

            requireActivity().runOnUiThread{
                changeWifiStatus(isEnabled = false)
                googleMapPlus.internetOn = false
            }

            delay(2000)

            run2 = false

            googleMapPlus.durationBetweenLocationUpdates = 1200L

            googleMapPlus.animationDuration = 0

            requireActivity().runOnUiThread{
                googleMapPlus.newLatLng(berlin2[0])
            }

            delay(1000)

            requireActivity().runOnUiThread{
                googleMapPlus.newLatLng(berlin2[0])
            }

            touch(myLocationButtonX, myLocationButtonY)

            delay(1000)

            requireActivity().runOnUiThread{
                googleMapPlus.newLatLng(berlin2[0])
            }

            delay(1000)

            googleMapPlus.animationDuration = 500

            async {
                delay(4000)
                requireActivity().runOnUiThread{
                    changeWifiStatus(isEnabled = true)
                    googleMapPlus.internetOn = true
                }
            }

            async {
                delay(200)
                requireActivity().runOnUiThread{
                    changeWifiStatus(isEnabled = true)
                }
            }

            for(i in 1 until berlin2.size) {
                requireActivity().runOnUiThread{
                    googleMapPlus.newLatLng(berlin2[i])
                    googleMapPlus.addPath(listOf(berlin2[i - 1], berlin2[i]), animated = true)
                }
                delay(1000)
            }
        }
    }

    private fun changeWifiStatus(isEnabled: Boolean) {
        try {
            (requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager).isWifiEnabled = isEnabled
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), "Can't change wifi state", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map_custom_demo.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        map_custom_demo.onStart()
    }

    override fun onResume() {
        super.onResume()
        map_custom_demo.onResume();
    }

    override fun onStop() {
        super.onStop()
        map_custom_demo.onStop();
    }

    override fun onPause() {
        super.onPause()
        map_custom_demo.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_custom_demo.onLowMemory()
    }

    //crashes the app
    /*override fun onDestroy() {
        super.onDestroy()
        map_custom_demo.onDestroy()
    }*/

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_custom_demo.onSaveInstanceState(outState)
    }

    /** * Simulate touching a specific location and dragging to a new location.
     *
     * @param fromX X coordinate of the initial touch, in screen coordinates
     * @param toX X coordinate of the drag destination, in screen coordinates
     * @param fromY X coordinate of the initial touch, in screen coordinates
     * @param toY Y coordinate of the drag destination, in screen coordinates
     * @param stepCount How many move steps to include in the drag
     */
    private fun fling(
        fromX: Float, toX: Float, fromY: Float,
        toY: Float, stepCount: Int
    ) {
        val inst = Instrumentation()

        val downTime = SystemClock.uptimeMillis()
        var eventTime = SystemClock.uptimeMillis()

        var y = fromY
        var x = fromX

        val yStep = (toY - fromY) / stepCount
        val xStep = (toX - fromX) / stepCount

        var event = MotionEvent.obtain(
            downTime, eventTime,
            MotionEvent.ACTION_DOWN, fromX, fromY, 0
        )
        event.source = InputDevice.SOURCE_TOUCHSCREEN
        inst.sendPointerSync(event)

        for (i in 0 until stepCount) {
            y += yStep
            x += xStep
            eventTime = SystemClock.uptimeMillis()
            event = MotionEvent.obtain(
                downTime, eventTime + stepCount,
                MotionEvent.ACTION_MOVE, x, y, 0
            )
            event.source = InputDevice.SOURCE_TOUCHSCREEN
            inst.sendPointerSync(event)
        }

        eventTime = SystemClock.uptimeMillis() + stepCount.toLong() + 2
        event = MotionEvent.obtain(
            downTime, eventTime,
            MotionEvent.ACTION_UP, toX, toY, 0
        )

        event.source = InputDevice.SOURCE_TOUCHSCREEN

        inst.sendPointerSync(event)
    }

    private fun touch(x: Float, y: Float) {

        val inst = Instrumentation()

        var event = MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis() + 50,
            MotionEvent.ACTION_DOWN,
            x,
            y,
            0
        )
        event.source = InputDevice.SOURCE_TOUCHSCREEN

        inst.sendPointerSync(event)

        event = MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis() + 50,
            MotionEvent.ACTION_UP,
            x,
            y,
            0
        )
        event.source = InputDevice.SOURCE_TOUCHSCREEN

        inst.sendPointerSync(event)
    }
}
