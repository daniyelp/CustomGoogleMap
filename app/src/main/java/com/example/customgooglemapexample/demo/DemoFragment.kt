package com.example.customgooglemapexample.demo

import android.accessibilityservice.GestureDescription
import android.app.Instrumentation
import android.content.Context
import android.net.wifi.WifiManager
import android.os.*
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.fragment.app.Fragment
import com.example.custom_google_map.CustomMapView
import com.example.custom_google_map.MyLocationButton
import com.example.customgooglemapexample.BuildConfig
import com.example.customgooglemapexample.R
import com.example.openstreetmap.Osm
import com.example.snap_to_roads.SnapToRoads
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_demo.*
import kotlinx.coroutines.*
import kotlin.concurrent.thread

@ExperimentalAnimationApi
class DemoFragment: Fragment(R.layout.fragment_demo) {

    private lateinit var customGoogleMap: CustomMapView.CustomGoogleMap
    private lateinit var snapToRoads : SnapToRoads
    private lateinit var osm: Osm

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_custom_demo.myLocationButton = button_my_location_demo
        map_custom_demo.statusBar = line_primary_status_demo
        map_custom_demo.mapTypeSelector = selector_map_type_demo
        map_custom_demo.getCustomMapAsync {
            customGoogleMap = it
            selector_map_type_demo.customGoogleMap = it

            snapToRoads = SnapToRoads(BuildConfig.GMAPS_API_KEY)
            osm = Osm()

            startDemo()
        }
    }

    @Volatile var run2: Boolean = false

    //private val berlin0 = listOf(LatLng(46.782061545357166,23.623105958104134), LatLng(46.78227873593419,23.62397901713848), LatLng(46.78250878250315,23.624899350106716), LatLng(46.78271839493815,23.626008443534378), LatLng(46.78299527495394,23.62714972347021), LatLng(46.78314197945983,23.628453612327572), LatLng(46.7832675618726,23.629401437938217))
   // private val berlin0 = listOf(LatLng(46.772073046495585,23.59093654900789), LatLng(46.77218946949091,23.591261096298695), LatLng(46.772296477386924,23.591544069349766), LatLng(46.772360314614176,23.591783791780472), LatLng(46.77239682580008,23.592027202248577), LatLng(46.77248615180462,23.59231822192669), LatLng(46.772577314695056,23.59260488301516))
    private val berlin0 = listOf(LatLng(46.772360314614176,23.591783791780472), LatLng(46.77239682580008,23.592027202248577), LatLng(46.77248615180462,23.59231822192669), LatLng(46.772577314695056,23.59260488301516))
    //private val berlin1 = listOf(LatLng(46.7821749621103,23.629894964396957), LatLng(46.78220480858456,23.630937673151493), LatLng(46.78225508837688,23.631913997232914), LatLng(46.78225164455697,23.63305527716875), LatLng(46.78236368338451,23.63430049270391), LatLng(46.78277280679499,23.63507565110922), LatLng(46.78301616716933,23.636011742055413), LatLng(46.78331715192202,23.636788241565228), LatLng(46.783358017761756,23.637740425765518), LatLng(46.783220726783995,23.638487085700035), LatLng(46.782522787302455,23.638122640550133), LatLng(46.78196305166721,23.637313283979896), LatLng(46.781710962101485,23.63583706319332), LatLng(46.7815495598745,23.634082227945328), LatLng(46.781353718400254,23.63213561475277), LatLng(46.780854813043526,23.630778416991234))
    private val berlin1 = listOf(LatLng(46.77242001842731,23.592956922948364), LatLng(46.77204732777236,23.59308566898107), LatLng(46.77167899756709,23.59283622354269), LatLng(46.7714557937481,23.591900467872623), LatLng(46.771214447837146,23.59104953706265))
    //private val berlin2 = listOf(LatLng(46.771270708398305,23.590870834887024), LatLng(46.77125899702103,23.59081383794546), LatLng(46.77123855951341,23.59076723456383), LatLng(46.77122386287392,23.590710572898388), LatLng(46.771204343893395,23.590648882091042), LatLng(46.771186661987294,23.59059289097786), LatLng(46.77116392809948,23.59052047133446), LatLng(46.77114670545082,23.590471856296062), LatLng(46.77112672717145,23.590416200459003), LatLng(46.77111065268845,23.59034813940525), LatLng(46.77108562241247,23.59027370810509), LatLng(46.771065414474776,23.59020095318556), LatLng(46.77104451762209,23.59014328569174))
    //private val berlin2 = listOf(LatLng(47.188733179293685,27.57358409464359), LatLng(47.18870401463679,27.573619298636913), LatLng(47.18868510317107,27.573638074100018), LatLng(47.18865707773352,27.57365215569735), LatLng(47.18863543206025,27.573671601712707), LatLng(47.18861310283016,27.573685012757778), LatLng(47.18859578627791,27.573702782392502), LatLng(47.188578469720014,27.573722898960114), LatLng(47.188558418961684,27.573737986385822), LatLng(47.18853426690184,27.573754750192165), LatLng(47.18850532998548,27.57376983761787), LatLng(47.18847138035483,27.57379665970802), LatLng(47.18844996245586,27.573822140693668), LatLng(47.188421709044015,27.57384192198515))
    private val berlin2 = listOf(LatLng(52.315510491823886,9.973794557154179), LatLng(52.31560723217582,9.974149279296398), LatLng(52.31571483507311,9.974513053894043), LatLng(52.315810140277904,9.974989145994186), LatLng(52.31587859593281,9.975292570888996), LatLng(52.315998290595886,9.975691549479961), LatLng(52.31610322811642,9.976103939116001), LatLng(52.31625530509715,9.97660618275404), LatLng(52.3163450753607,9.977040700614452), LatLng(52.31648485426703,9.9775804951787), LatLng(52.31662319805783,9.978088103234768))
    private val berlin3 = listOf(LatLng(46.771270708398305,23.590870834887024), LatLng(46.77125899702103,23.59081383794546), LatLng(46.77123855951341,23.59076723456383), LatLng(46.77122386287392,23.590710572898388), LatLng(46.771204343893395,23.590648882091042), LatLng(46.771186661987294,23.59059289097786), LatLng(46.77116392809948,23.59052047133446), LatLng(46.77114670545082,23.590471856296062), LatLng(46.77112672717145,23.590416200459003), LatLng(46.77111065268845,23.59034813940525), LatLng(46.77108562241247,23.59027370810509), LatLng(46.771065414474776,23.59020095318556), LatLng(46.77104451762209,23.59014328569174))

    @DelicateCoroutinesApi
    private fun startDemo() {

        val myLocationButtonX = 950f
        val myLocationButtonY = 1780f

        val selectorX = 1015f
        val selectorY = 337f

        val defaultX = 444f
        val defaultY = 470f

        val terrainX = 615f
        val terrainY = 470f

        val satelliteX = 815f
        val satelliteY = 470f

        GlobalScope.launch {

            customGoogleMap.durationBetweenLocationUpdates = 1200L

            customGoogleMap.internetOn = true

            customGoogleMap.gpsOn = false

            delay(1700)

            customGoogleMap.gpsOn = true

            delay(1000)

            touch(myLocationButtonX, myLocationButtonY)

            delay(500)

            customGoogleMap.animationDuration = 500

            requireActivity().runOnUiThread{
                customGoogleMap.newLatLng(berlin0[0])
            }

            delay(1000)

            requireActivity().runOnUiThread{
                customGoogleMap.newLatLng(berlin0[0])
            }

            for(i in 1 until berlin0.size) {
                delay(1000)
                requireActivity().runOnUiThread{
                    customGoogleMap.newLatLng(berlin0[i])
                    customGoogleMap.addPath(listOf(berlin0[i - 1], berlin0[i]), animated = true)
                }
            }

            var run = true

            async {
                while(run) {
                    delay(1000)
                    requireActivity().runOnUiThread{ customGoogleMap.newLatLng(berlin0.last()) }
                }
            }

            delay(1500)

            fling(500f ,501f ,530f,800f, 5)

            delay(500)

            touch(myLocationButtonX, myLocationButtonY)

            delay (1100)

            touch(selectorX, selectorY)

            delay(1000)

            touch(terrainX, terrainY)

            delay(1000)

            touch(satelliteX, satelliteY)

            delay(1000)

            touch(defaultX, defaultY)

            run = false

            delay(1000)

            touch(selectorX, selectorY)

            delay(4000)

            customGoogleMap.animationDuration = 400

            requireActivity().runOnUiThread{ customGoogleMap.newLatLng(berlin1[0]) }

            delay(700)

            for(i in 1 until berlin1.size) {
                requireActivity().runOnUiThread{
                    customGoogleMap.newLatLng(berlin1[i])
                    customGoogleMap.addPath(listOf(berlin1[i - 1], berlin1[i]), animated = true)
                }
                delay(700)
            }

            touch(myLocationButtonX, myLocationButtonY)

            run2 = true

            delay(700)

            async {
                while(run2) {
                    Log.d("PATH", "RUN2 CALLED")
                    requireActivity().runOnUiThread{ customGoogleMap.newLatLng(berlin1.last()) }
                    delay(700)
                }
            }

            delay(400)

            requireActivity().runOnUiThread {
                customGoogleMap.zoomToFit(berlin1, animated = true)
            }

            delay(400)

            val snappedPath = snapToRoads.getSnappedToRoadsPath(berlin1)

            customGoogleMap.animationDuration = 1000
            requireActivity().runOnUiThread{
                customGoogleMap.addPath(
                    snappedPath,
                    animated = true,
                    polylineOptions = customGoogleMap.getDefaultPolylineOptions().color(android.graphics.Color.RED)
                )
            }

            delay(800)

            requireActivity().runOnUiThread {
                customGoogleMap.zoomToFit(snappedPath, animated = true, paddingPx = 150)
            }

            delay(800)

            requireActivity().runOnUiThread() {
                customGoogleMap.addMarker(
                    MarkerOptions()
                        .position(snappedPath.first())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
            }

            delay(500)

            requireActivity().runOnUiThread() {
                customGoogleMap.addMarker(
                    MarkerOptions()
                        .position(snappedPath.last())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
            }

            delay(500)

            val latLng = snappedPath.first()
            val (city, boundary) = osm.getCityWithBoundary(latLng.latitude, latLng.longitude)

            requireActivity().runOnUiThread() {
            customGoogleMap.zoomToFit(boundary, animated = true)
            }

            delay(1500)

            customGoogleMap.animationDuration = 2000
            requireActivity().runOnUiThread() {
            //Toast.makeText(requireActivity(), city, Toast.LENGTH_SHORT).show()
            customGoogleMap.addPath(boundary, animated = true, polylineOptions = customGoogleMap.getDefaultPolylineOptions().color(android.graphics.Color.RED))
            }

            delay(1000)

            requireActivity().runOnUiThread{
                changeWifiStatus(isEnabled = false)
                customGoogleMap.internetOn = false
            }

            delay(2000)

            run2 = false

            customGoogleMap.durationBetweenLocationUpdates = 1200L

            customGoogleMap.animationDuration = 0

            requireActivity().runOnUiThread{
                Log.d("PATH", "BERLIN2 CALLED")
                customGoogleMap.newLatLng(berlin2[0])
            }

            delay(1000)

            requireActivity().runOnUiThread{
                Log.d("PATH", "BERLIN2 CALLED")
                customGoogleMap.newLatLng(berlin2[0])
            }

            touch(myLocationButtonX, myLocationButtonY)

            delay(1000)

            requireActivity().runOnUiThread{
                Log.d("PATH", "BERLIN2 CALLED")
                customGoogleMap.newLatLng(berlin2[0])
            }

            delay(1000)

            customGoogleMap.animationDuration = 500

            async {
                delay(4000)

                requireActivity().runOnUiThread{
                    changeWifiStatus(isEnabled = true)
                    customGoogleMap.internetOn = true
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
                    Log.d("PATH", "BERLIN2 CALLED")
                    customGoogleMap.newLatLng(berlin2[i])
                    customGoogleMap.addPath(listOf(berlin2[i - 1], berlin2[i]), animated = true)
                }
                delay(1000)
            }
            
        }
    }

    private fun changeWifiStatus(isEnabled: Boolean) {
        (requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager).isWifiEnabled = isEnabled
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            event.source = InputDevice.SOURCE_TOUCHSCREEN
        }
        inst.sendPointerSync(event)

        for (i in 0 until stepCount) {
            y += yStep
            x += xStep
            eventTime = SystemClock.uptimeMillis()
            event = MotionEvent.obtain(
                downTime, eventTime + stepCount,
                MotionEvent.ACTION_MOVE, x, y, 0
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                event.source = InputDevice.SOURCE_TOUCHSCREEN
            }
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
