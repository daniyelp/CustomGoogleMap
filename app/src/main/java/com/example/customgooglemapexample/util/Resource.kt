package com.example.customgooglemapexample.util

data class Resource<T>(val list: List<T>, val status: Status, val n: Int? = null, val animate: Boolean? = null, val amSpecialLevel: Int = 0, val zoomToFit: Boolean? = null)

enum class Status {
    INITIALIZED,
    ADDED_ELEMENT,
    ADDED_SEVERAL_ELEMENTS,
    RESETED,
    REMOVED_LAST_ELEMENT
}