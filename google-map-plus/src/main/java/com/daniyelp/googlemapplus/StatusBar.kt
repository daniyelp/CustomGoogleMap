package com.daniyelp.googlemapplus

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.custom_google_map.R
import kotlinx.android.synthetic.main.bar_status.view.*

@ExperimentalAnimationApi
class StatusBar: FrameLayout {

    constructor(context: Context): super(context) {
        initCompose()
    }
    constructor(context: Context, attributes: AttributeSet): super(context, attributes) {
        initCompose()
    }

    var elevation by mutableStateOf(8.dp)
    var padding by mutableStateOf(8.dp)
    var shape by mutableStateOf<Shape>(RoundedCornerShape(5))
    var internetOffText by mutableStateOf("INTERNET OFF")
    var gpsOffText by mutableStateOf("GPS OFF")
    var acquiringLocationText by mutableStateOf("ACQUIRING LOCATION")
    var locationRetrievedText by mutableStateOf("LOCATION RETRIEVED")
    var internetOffTextColor by mutableStateOf(Color.White)
    var gpsOffTextColor by mutableStateOf(Color.White)
    var acquiringLocationTextColor by mutableStateOf(Color.White)
    var locationRetrievedTextColor by mutableStateOf(Color.White)
    var internetOffBackgroundColor by mutableStateOf(MapsGray)
    var gpsOffBackgroundColor by mutableStateOf(MapsRed)
    var acquiringLocationBackgroundColor by mutableStateOf(MapsBlue)
    var locationRetrievedBackgroundColor by mutableStateOf(MapsGreen)

    constructor(
        elevation: Dp = 8.dp,
        padding: Dp = 8.dp,
        shape: Shape = RoundedCornerShape(5),
        context: Context,
        internetOffText: String = "INTERNET OFF",
        gpsOffText: String = "GPS OFF",
        acquiringLocationText: String = "ACQUIRING LOCATION",
        locationRetrievedText: String = "LOCATION RETRIEVED",
        internetOffTextColor: Color = Color.White,
        gpsOffTextColor: Color = Color.White,
        acquiringLocationTextColor: Color = Color.White,
        locationRetrievedTextColor: Color = Color.White,
        internetOffBackgroundColor: Color = MapsGray,
        gpsOffBackgroundColor: Color = MapsRed,
        acquiringLocationBackgroundColor: Color = MapsBlue,
        locationRetrievedBackgroundColor: Color = MapsGreen,
    ): super(context) {
        this.elevation = elevation
        this.padding = padding
        this.shape = shape
        this.internetOffText = internetOffText
        this.gpsOffText = gpsOffText
        this.acquiringLocationText = acquiringLocationText
        this.locationRetrievedText =  locationRetrievedText
        this.internetOffBackgroundColor = internetOffBackgroundColor
        this.gpsOffBackgroundColor = gpsOffBackgroundColor
        this.acquiringLocationBackgroundColor = acquiringLocationBackgroundColor
        this.locationRetrievedBackgroundColor = locationRetrievedBackgroundColor
        this.internetOffTextColor = internetOffTextColor
        this.gpsOffTextColor = gpsOffTextColor
        this.acquiringLocationTextColor = acquiringLocationTextColor
        this.locationRetrievedTextColor = locationRetrievedTextColor
        initCompose()
    }

    private fun initCompose() {
        view_compose_status_bar.setContent {
            StatusBar()
        }
    }

    init {
        inflate(context, R.layout.bar_status, this)
    }

    private var visible by mutableStateOf(false)
    private var text by mutableStateOf("")
    private var backgroundColor by mutableStateOf(Color.Transparent)
    private var textColor by mutableStateOf(Color.Transparent)
    private val untilGoneDuration = 1500L
    private var hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hide() }
    private var hasCallBack = false

    internal fun display(status: MapViewPlus.StatusController.PrimaryStatus, indeterminate: Boolean = false) {
        this.text = when(status) {
            MapViewPlus.StatusController.PrimaryStatus.INTERNET_OFF -> internetOffText
            MapViewPlus.StatusController.PrimaryStatus.GPS_OFF -> gpsOffText
            MapViewPlus.StatusController.PrimaryStatus.ACQUIRING_LOCATION -> acquiringLocationText
            MapViewPlus.StatusController.PrimaryStatus.LOCATION_RETRIEVED -> locationRetrievedText
            else -> ""
        }
        this.backgroundColor = when(status) {
            MapViewPlus.StatusController.PrimaryStatus.INTERNET_OFF -> internetOffBackgroundColor
            MapViewPlus.StatusController.PrimaryStatus.GPS_OFF -> gpsOffBackgroundColor
            MapViewPlus.StatusController.PrimaryStatus.ACQUIRING_LOCATION -> acquiringLocationBackgroundColor
            MapViewPlus.StatusController.PrimaryStatus.LOCATION_RETRIEVED -> locationRetrievedBackgroundColor
            else -> Color.Transparent
        }
        this.textColor = when(status) {
            MapViewPlus.StatusController.PrimaryStatus.INTERNET_OFF -> internetOffTextColor
            MapViewPlus.StatusController.PrimaryStatus.GPS_OFF -> gpsOffTextColor
            MapViewPlus.StatusController.PrimaryStatus.ACQUIRING_LOCATION -> acquiringLocationTextColor
            MapViewPlus.StatusController.PrimaryStatus.LOCATION_RETRIEVED -> locationRetrievedTextColor
            else -> Color.Transparent
        }
        visible = true
        hideHandler.removeCallbacks(hideRunnable)
        if(!indeterminate) {
            hideHandler.postDelayed(hideRunnable, untilGoneDuration)
            hasCallBack = true
        }
    }

    internal fun hide(now: Boolean = true) {
        if(now) {
            hideHandler.removeCallbacks(hideRunnable)
            hasCallBack = false
            visible = false
        } else {
            if(!hasCallBack) {
                visible = false
            }
        }
    }

    @ExperimentalAnimationApi
    @Composable
    private fun StatusBar() {
        AnimatedVisibility(
            modifier = Modifier.clip(shape),
            visible = visible,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Card(
                modifier = Modifier.padding(padding),
                elevation = elevation,
                backgroundColor = backgroundColor,
            ) {
                Text(
                    modifier = Modifier.padding(6.dp),
                    text = text,
                    color = textColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}