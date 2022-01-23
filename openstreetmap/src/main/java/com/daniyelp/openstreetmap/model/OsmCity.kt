package com.daniyelp.openstreetmap.model

//https://github.com/osm-search/Nominatim/blob/master/docs/api/Output.md
internal data class OsmCity (
    val place_id: Long?,
    val license: String?,
    val osm_type: String?,
    val osm_id: Long?,
    val boundingbox: List<Double>?,
    val lat: Double?,
    val lon: Double?,
    val display_name: String?,
    val clazz: String?,
    val type: String?,
    val importance: Long?,
    val icon: String?,
    val address: OsmAddress?,
    val extratags: OsmExtratags?,
    val geojson: OsmPolygon?
    )