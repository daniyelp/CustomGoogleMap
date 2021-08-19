package com.example.snap_to_roads.remote.model

import com.google.android.gms.maps.model.LatLng

data class SnappedPoint (
    val location: LatLng,
    val originalIndex : Int? = null,
    val placeId: String
    )