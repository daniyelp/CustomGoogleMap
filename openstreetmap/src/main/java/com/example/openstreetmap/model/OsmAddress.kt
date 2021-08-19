package com.example.openstreetmap.model

data class OsmAddress (
    val continent: String?,
    val country: String?,
    val country_code: String?,
    val region: String?,
    val state: String?,
    val state_district: String?,
    val county: String?,
    val municipality: String?,
    val city: String?,
    val town: String?,
    val village: String?
    //etc
    )