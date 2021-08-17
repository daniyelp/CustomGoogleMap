package com.example.custom_google_map

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.button_my_location.view.*

class MyLocationButton(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    init {
        inflate(context, R.layout.button_my_location, this)
        button_inner_my_location.setOnClickListener {
            center = !center
        }
    }

    var onCenter : () -> Unit = {}
    var onDecenter : () -> Unit = {}

    var center: Boolean = false
    private set(value) {
        field = value
        if (value) {
            button_inner_my_location.setImageResource(R.drawable.ic_my_location_blue)
            onCenter()
        } else {
            button_inner_my_location.setImageResource(R.drawable.ic_my_location_black)
            onDecenter()
        }
    }

    fun decenter() {
        center = false
    }
}