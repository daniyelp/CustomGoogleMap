package com.example.custom_google_map

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.FloatingActionButtonDefaults.elevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.button_my_location.view.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

class MyLocationButton: ConstraintLayout {

    constructor(context: Context): super(context)
    constructor(context: Context, attributes: AttributeSet): super(context, attributes)

    init {
        inflate(context, R.layout.button_my_location, this)

        view_compose_my_location_button.setContent {
            MyLocationButton()
        }
        setOnClickListener {
            onClick()
        }
    }

    private val tint = mutableStateOf(Color.Black)

    var onCenter : () -> Unit = {}
    var onDecenter : () -> Unit = {}

    var center: Boolean = false
        private set(value) {
            field = value
            if (value) {
                tint.value = Color.Blue
                onCenter()
            } else {
                tint.value = Color.Black
                onDecenter()
            }
        }

    fun decenter() {
        center = false
    }

    @Composable
    fun MyLocationButton(elevation : Dp = 8.dp) {
        FloatingActionButton(
            onClick = { onClick() },
            backgroundColor = Color.White,
            elevation = elevation(elevation, 0.dp),
            modifier = Modifier
                .padding(elevation + elevation)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_my_location),
                tint = tint.value,
                contentDescription = "my location button"
            )
        }
    }

    private fun onClick() {
        center = !center
    }
}