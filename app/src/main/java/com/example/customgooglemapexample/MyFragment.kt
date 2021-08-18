package com.example.customgooglemapexample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.custom_google_map.CustomMapView
import com.example.customgooglemapexample.databinding.FragmentMineBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_mine.*

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_custom.myLocationButton = button_my_location
        map_custom.primaryStatusLine = line_primary_status
        map_custom.mapTypeSelector = selector_map_type
        map_custom.getCustomMapAsync {
            customGoogleMap = it
            selector_map_type.customGoogleMap = it
            subscribeToObservers()
        }
    }

    private fun subscribeToObservers() {
        with(viewModel) {

            lastLocation.observe(viewLifecycleOwner, Observer {
                it?.let {
                    customGoogleMap.newLatLng(it)
                }
            })

            gpsEnabled.observe(viewLifecycleOwner, Observer {
                it?.let {
                    customGoogleMap.gpsOn = it
                    Log.d("GPS", "gps status changed inside fragme")
                }
            })

            internetEnabled.observe(viewLifecycleOwner, Observer {
                it?.let {
                    customGoogleMap.internetOn = it
                    Log.d("INTERNET", "internet status changed inside fragment")
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