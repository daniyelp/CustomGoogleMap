package com.example.customgooglemapexample.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class CombinedLiveData<T, K, S>(source1: LiveData<T>, source2: LiveData<K>, private val combine: (data1: T?, data2: K?) -> S) : MediatorLiveData<S>() {

    private var data1: T? = null
    private var data2: K? = null

    init {
        addSource(source1) {
            data1 = it
            value = combine(data1, data2)
        }
        addSource(source2) {
            data2 = it
            value = combine(data1, data2)
        }
    }
}