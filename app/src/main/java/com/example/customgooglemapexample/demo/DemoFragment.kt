package com.example.customgooglemapexample.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.fragment.app.Fragment
import com.example.custom_google_map.CustomMapView
import com.example.customgooglemapexample.R
import kotlinx.android.synthetic.main.fragment_demo.*

@ExperimentalAnimationApi
class DemoFragment: Fragment(R.layout.fragment_demo) {

    private lateinit var customGoogleMap: CustomMapView.CustomGoogleMap

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_custom_demo.myLocationButton = button_my_location_demo
        map_custom_demo.statusBar = line_primary_status_demo
        map_custom_demo.mapTypeSelector = selector_map_type_demo
        map_custom_demo.getCustomMapAsync {
            customGoogleMap = it
            selector_map_type_demo.customGoogleMap = it
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

}