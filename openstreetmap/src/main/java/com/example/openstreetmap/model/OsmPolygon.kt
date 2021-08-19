package com.example.openstreetmap.model


data class OsmPolygon (
    val type: String?,
    val coordinates: List<List<List<Double>>>?
        )