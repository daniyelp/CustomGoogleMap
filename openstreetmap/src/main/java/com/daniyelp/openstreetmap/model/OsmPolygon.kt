package com.daniyelp.openstreetmap.model

internal data class OsmPolygon (
    val type: String?,
    val coordinates: List<List<List<Double>>>?
)