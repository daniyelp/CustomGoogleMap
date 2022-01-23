package com.daniyelp.snaptoroads.remote.model

import com.google.android.gms.maps.model.LatLng

internal data class SnappedPoint (
    val location: LatLng,
    val originalIndex : Int? = null,
    val placeId: String
    )