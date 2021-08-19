package com.example.snap_to_roads.remote.model

import com.google.gson.annotations.SerializedName

data class SnappedPoints(
    @SerializedName("snappedPoints")
    val list: List<SnappedPoint>
)