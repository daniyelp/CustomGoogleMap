package com.daniyelp.googlemapplus.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.daniyelp.googlemapplus.MapViewPlus
import com.example.customgooglemapexample.R
import com.example.customgooglemapexample.databinding.FragmentMineBinding
import com.daniyelp.googlemapplus.util.Status
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import de.p72b.maps.animation.AnimatedPolyline
import kotlinx.android.synthetic.main.fragment_mine.*

@ExperimentalAnimationApi
@AndroidEntryPoint
class MyFragment : Fragment() {

    private lateinit var binding: FragmentMineBinding
    private val viewModel: MyViewModel by viewModels()
    private lateinit var googleMapPlus: MapViewPlus.GoogleMapPlus

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

    @Composable
    fun Buttons(viewModel: MyViewModel) {
        val connectEnabled by viewModel.connectEnabled.observeAsState(false)
        val connect2Enabled by viewModel.connect2Enabled.observeAsState(false)
        val addStartMarkerEnabled by viewModel.addStartMarkerEnabled.observeAsState(false)
        val addFinishMarkerEnabled by viewModel.addFinishMarkerEnabled.observeAsState(false)
        val snapToRoadsEnabled by viewModel.snapToRoadsEnabled.observeAsState(false)
        val osmEnabled by viewModel.osmEnabled.observeAsState(false)
        val splitEnabled by viewModel.splitEnabled.observeAsState(false)
        val undoMarkerEnabled by viewModel.undoMarkerEnabled.observeAsState(false)
        val undoPathEnabled by viewModel.undoPathEnabled.observeAsState(false)

        @Composable
        fun MyDivider() {
            Divider(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .width(60.dp),
                color = Color.Black,
                thickness = 3.dp
            )
        }
        Card(
            modifier = Modifier.padding(8.dp),
            elevation = 0.dp,
            backgroundColor = Color.Gray.copy(alpha = 0.0f)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MyDivider()
                Column(
                    modifier = Modifier
                        .height(200.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(onClick = viewModel::reset) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Button(
                        onClick = viewModel::connect,
                        enabled = connectEnabled
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_path),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Button(
                        onClick = viewModel::connect2,
                        enabled = connect2Enabled
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_path),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "+")
                    }
                    Button(
                        onClick = viewModel::onNewStartMarker,
                        enabled = addStartMarkerEnabled,
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.green))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_flag),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Button(
                        onClick = viewModel::onNewFinishMarker,
                        enabled = addFinishMarkerEnabled,
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.red))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_flag),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Button(
                        onClick = viewModel::snapToRoads,
                        enabled = snapToRoadsEnabled
                    ) {
                        Text("STR")
                    }
                    Button(
                        onClick = viewModel::osm,
                        enabled = osmEnabled
                    ) {
                        Text("OSM")
                    }
                    Button(
                        onClick = viewModel::split,
                        enabled = splitEnabled
                    ) {
                        Text("SPLIT")
                    }
                    Button(
                        onClick = viewModel::undoMarker,
                        enabled = undoMarkerEnabled
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_undo),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp)
                        )
                        Text("MARKER")
                    }
                    Button(
                        onClick = viewModel::undoPath,
                        enabled = undoPathEnabled
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_undo),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp)
                        )
                        Text("PATH")
                    }
                }
                MyDivider()
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_view_plus.myLocationButton = button_my_location
        map_view_plus.statusBar = line_primary_status
        map_view_plus.mapTypeSelector = selector_map_type
        map_view_plus.getMapAsync { googleMapPlus ->
            googleMapPlus.animationDuration = 1000L
            googleMapPlus.durationBetweenLocationUpdates = 2000L
            googleMapPlus.darkThemeOn = false
            this.googleMapPlus = googleMapPlus
            subscribeToObservers()
            googleMapPlus.setOnMapClickListener { latLng ->
                viewModel.onNewMarker(latLng)
            }
            googleMapPlus.setOnMapLongClickListener { latLng ->
                viewModel.onNewLocation(latLng)
            }
            view_compose_mine.setContent {
                Buttons(viewModel)
            }
        }
    }

    private val _markers = mutableListOf<Marker>()
    private val _paths = mutableListOf<AnimatedPolyline>()

    private fun subscribeToObservers() {
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

            markers.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.RESET -> {
                        _markers.forEach { it.remove() }
                        _markers.clear()
                    }
                    Status.ADDED_ELEMENT -> {
                        val newMarker = googleMapPlus.addMarker(
                            MarkerOptions()
                                .position(it.list.last())
                                .icon(
                                    when (it.amSpecialLevel) {
                                        0 -> BitmapDescriptorFactory.defaultMarker((_markers.size * 10).toFloat() % 360)
                                        2 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                        3 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                        else -> BitmapDescriptorFactory.defaultMarker((_markers.size * 10).toFloat() % 360)
                                    }
                                )
                        )
                        newMarker?.let {
                            _markers.add(it)
                        }
                    }
                    Status.REMOVED_LAST_ELEMENT -> {
                        _markers.removeLast().remove()
                    }
                    else -> { }
                }
            }

            paths.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.ADDED_ELEMENT -> {
                        _paths.add(
                            googleMapPlus.addPath(
                                it.list.last(),
                                it.animate ?: false,
                                googleMapPlus.getDefaultPolylineOptions()
                                    .color(if (it.amSpecialLevel == 0) android.graphics.Color.BLACK else android.graphics.Color.RED)
                            )
                        )
                        it.zoomToFit?.let { zoom ->
                            if (zoom) {
                                googleMapPlus.zoomToFit(it.list.last(), animated = true)
                            }
                        }
                    }
                    Status.RESET -> {
                        _paths.forEach { it.remove() }
                        _paths.clear()
                    }
                    Status.REMOVED_LAST_ELEMENT -> {
                        _paths.removeLast().remove()
                    }
                    Status.ADDED_SEVERAL_ELEMENTS -> {
                        _paths.addAll(
                            googleMapPlus.addPaths(
                                it.list.takeLast(it.n!!),
                                it.animate ?: false,
                                googleMapPlus.getDefaultPolylineOptions()
                                    .color(android.graphics.Color.RED)
                            )
                        )
                    }
                    else -> { }
                }
            }

            displayCityName.observe(viewLifecycleOwner) {
                if (it.display) {
                    Toast.makeText(requireContext(), it.t, Toast.LENGTH_SHORT).show()
                    viewModel.displayedCityName()
                }
            }
        }

    }

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
}