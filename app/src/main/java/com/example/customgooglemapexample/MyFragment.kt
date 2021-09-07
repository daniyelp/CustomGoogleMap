package com.example.customgooglemapexample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.custom_google_map.CustomMapView
import com.example.customgooglemapexample.databinding.FragmentMineBinding
import com.example.customgooglemapexample.util.Status
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.coroutines.delay
import kotlin.concurrent.thread

@AndroidEntryPoint
class MyFragment: Fragment() {

    private lateinit var binding: FragmentMineBinding
    private val viewModel : MyViewModel by viewModels()

    private lateinit var customGoogleMap: CustomMapView.CustomGoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_mine,
            container,
            false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    //@Preview
    @Composable
    fun Buttons(viewModel: MyViewModel) {

        val connectEnabled by viewModel.connectEnabled.observeAsState(false)
        val connect2Enabled by viewModel.connect2Enabled.observeAsState(false)
        val snapToRoadsEnabled by viewModel.snapToRoadsEnabled.observeAsState(false)
        val osmEnabled by viewModel.osmEnabled.observeAsState(false)

        val undoMarkerEnabled by viewModel.undoMarkerEnabled.observeAsState(false)
        val undoPathEnabled by viewModel.undoPathEnabled.observeAsState(false)

        Card(
            modifier = Modifier
                .padding(8.dp)
                .height(200.dp),
            elevation = 0.dp,
            shape = RoundedCornerShape(3),
            backgroundColor = Color.Gray.copy(alpha = 0.3f)
        ){
            Card(
                modifier = Modifier
                    .padding(8.dp),
                elevation = 0.dp,
                backgroundColor = Color.Gray.copy(alpha = 0.0f)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { viewModel.reset() },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = "Reset",
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                    Button(
                        onClick = { viewModel.connect() },
                        enabled = connectEnabled
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_path),
                            contentDescription = "Connect",
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                    Button(
                        onClick = { viewModel.connect2() },
                        enabled = connect2Enabled
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_path),
                            contentDescription = "Connect2",
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text(text = "+")
                    }
                    Button(
                        onClick = { viewModel.snapToRoads() },
                        enabled = snapToRoadsEnabled
                    ) {
                        Text("STR")
                    }
                    Button(
                        onClick = { viewModel.osm() },
                        enabled = osmEnabled
                    ) {
                        Text("OSM")
                    }
                    Button(
                        onClick = { viewModel.split() },
                    ) {
                        Text("SPLIT")
                    }
                    Button(
                        onClick = { viewModel.undoMarker() },
                        enabled = undoMarkerEnabled
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_undo),
                            contentDescription = "undo",
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text("MARKER")
                    }
                    Button(
                        onClick = { viewModel.undoPath() },
                        enabled = undoPathEnabled
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_undo),
                            contentDescription = "undo",
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text("PATH")
                    }
                }
            }
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_custom.myLocationButton = button_my_location
        map_custom.primaryStatusLine = line_primary_status
        map_custom.mapTypeSelector = selector_map_type
        map_custom.getCustomMapAsync {
            customGoogleMap = it
            selector_map_type.customGoogleMap = it
            subscribeToObservers()
            customGoogleMap.setOnMapClickListener { latLng ->
                viewModel.onNewMarker(latLng)
            }
            customGoogleMap.setOnMapLongClickListener { latLng ->
                viewModel.onNewMockLocation(latLng)
            }
            view_compose_mine.setContent {
                Buttons(viewModel)
            }
        }
    }

    private fun subscribeToObservers() {
        with(viewModel) {

            /*lastLocation.observe(viewLifecycleOwner, Observer {
                it?.let {
                    customGoogleMap.newLatLng(it)
                }
            })*/

            lastMockLocation.observe(viewLifecycleOwner, Observer {
                it?.let {
                    customGoogleMap.newLatLng(it)
                }
            })

            gpsEnabled.observe(viewLifecycleOwner, Observer {
                it?.let {
                    customGoogleMap.gpsOn = it
                    Log.d("GPS", "gps status changed inside fragment")
                }
            })

            internetEnabled.observe(viewLifecycleOwner, Observer {
                it?.let {
                    customGoogleMap.internetOn = it
                    Log.d("INTERNET", "internet status changed inside fragment")
                }
            })

            markers.observe(viewLifecycleOwner, Observer {
                it?.let {
                    when (it.status) {
                        Status.RESETED -> {
                            customGoogleMap.removeMarkers()
                        }
                        Status.ADDED_ELEMENT -> {
                            customGoogleMap.addAsMarker(it.list.last())
                        }
                        Status.REMOVED_LAST_ELEMENT -> {
                            customGoogleMap.removeLastMarkerIfAny()
                        }
                        else -> {

                        }
                    }
                }
            })

            paths.observe(viewLifecycleOwner, Observer {
                it?.let {
                    when(it.status) {
                        Status.ADDED_ELEMENT -> {
                            Log.d("PATH", it.list.last().toString())
                            customGoogleMap.addPath(
                                it.list.last(),
                                it.animate ?: false,
                                customGoogleMap.getDefaultPolylineOptions().color(if(it.special) android.graphics.Color.BLACK else android.graphics.Color.RED)
                            )
                            it.zoomToFit?.let { zoom ->
                                if(zoom) {
                                    customGoogleMap.zoomToFit(it.list.last(), animated = true)
                                }
                            }
                        }
                        Status.RESETED -> {
                            customGoogleMap.removePaths()
                        }
                        Status.REMOVED_LAST_ELEMENT -> {
                            customGoogleMap.removeLastPath()
                        }
                        Status.ADDED_SEVERAL_ELEMENTS -> {
                            customGoogleMap.addPaths(
                                it.list.takeLast(it.n!!),
                                it.animate ?: false,
                                customGoogleMap.getDefaultPolylineOptions().color(android.graphics.Color.RED)
                            )
                        }
                        else -> {

                        }
                    }
                }
            })

            displayCityName.observe(viewLifecycleOwner, Observer {
                it?.let {
                    if(it.display) {
                        Toast.makeText(requireContext(), it.t, Toast.LENGTH_SHORT).show()
                        viewModel.displayedCityName()
                    }
                }
            })

        }

    }

    //map does nothing without these below

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map_custom.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        map_custom.onStart()
    }

    override fun onResume() {
        super.onResume()
        map_custom.onResume();
    }

    override fun onStop() {
        super.onStop()
        map_custom.onStop();
    }

    override fun onPause() {
        super.onPause()
        map_custom.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_custom.onLowMemory()
    }

    /*override fun onDestroy() {
        super.onDestroy()
        map_custom.onDestroy()
    }*/

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_custom.onSaveInstanceState(outState)
    }
}