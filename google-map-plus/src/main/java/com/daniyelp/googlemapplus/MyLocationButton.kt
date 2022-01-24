package com.daniyelp.googlemapplus

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.FloatingActionButtonDefaults.elevation
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.android.synthetic.main.button_my_location.view.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.custom_google_map.R

class MyLocationButton: FrameLayout {

    constructor(context: Context): super(context) {
        initCompose()
    }
    constructor(context: Context, attributes: AttributeSet): super(context, attributes) {
        initCompose()
    }
    var elevation by mutableStateOf(8.dp)
    var padding by mutableStateOf(16.dp)
    var backgroundColor by mutableStateOf(Color.White)
    var disabledColor by mutableStateOf(Color.Black)
    var enabledColor by mutableStateOf(MapsBlue)
    constructor(
        context: Context,
        elevation: Dp = 8.dp,
        padding: Dp = 16.dp,
        backgroundColor: Color = Color.White,
        disabledColor: Color = Color.Black,
        enabledColor: Color = MapsBlue
    ): super(context) {
        this.elevation = elevation
        this.padding = padding
        this.backgroundColor = backgroundColor
        this.disabledColor = disabledColor
        this.enabledColor = enabledColor
        initCompose()
    }

    private fun initCompose() {
        view_compose_my_location_button.setContent {
            MyLocationButton()
        }
        setOnClickListener {
            onClick()
        }
    }

    init {
        inflate(context, R.layout.button_my_location, this)
    }

    private var tint by mutableStateOf(disabledColor)

    internal var onCenter : () -> Unit = {}
    internal var onDecenter : () -> Unit = {}

    var centering: Boolean = false
        private set(value) {
            field = value
            if (value) {
                tint = enabledColor
                onCenter()
            } else {
                tint = disabledColor
                onDecenter()
            }
        }

    internal fun decenter() {
        centering = false
    }

    @Composable
    private fun MyLocationButton() {
        FloatingActionButton(
            onClick = { onClick() },
            backgroundColor = backgroundColor,
            elevation = elevation(elevation, elevation * 2),
            modifier = Modifier.padding(padding)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_my_location),
                tint = tint,
                contentDescription = null
            )
        }
    }

    private fun onClick() {
        centering = !centering
    }
}