package com.daniyelp.snaptoroads.remote.model

import com.google.gson.annotations.SerializedName

internal data class SnappedPoints(
    @SerializedName("snappedPoints")
    val list: List<SnappedPoint>
)