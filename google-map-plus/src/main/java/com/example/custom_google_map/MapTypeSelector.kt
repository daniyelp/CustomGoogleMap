package com.example.custom_google_map

import android.content.Context
import android.util.AttributeSet
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults.elevation
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.selector_map_type.view.*

@ExperimentalAnimationApi
class MapTypeSelector: ConstraintLayout {
    constructor(context: Context): super(context) {
        initCompose()
    }
    constructor(context: Context, attributes: AttributeSet): super(context, attributes) {
        initCompose()
    }
    private var elevation: Dp = 8.dp
    private var padding: Dp = 8.dp
    private var cardShape: Shape = RoundedCornerShape(5)
    private var cardItemShape: Shape = RoundedCornerShape(20)
    private var surfaceColor: Color = Color.White
    private var onSurfaceColor: Color = Color(0xFF3C4043)
    private var selectedColor: Color = Color(0xFF1A73E8)
    constructor(
        context: Context,
        elevation: Dp = 8.dp,
        padding: Dp = 8.dp,
        cardShape: Shape = RoundedCornerShape(5),
        cardItemShape: Shape = RoundedCornerShape(20),
        surfaceColor: Color = Color.White,
        onSurfaceColor: Color = Color(0xFF3C4043),
        selectedColor: Color = Color(0xFF1A73E8),
    ): super(context) {
        this.elevation = elevation
        this.padding = padding
        this.cardShape = cardShape
        this.cardItemShape = cardItemShape
        this.surfaceColor = surfaceColor
        this.onSurfaceColor = onSurfaceColor
        this.selectedColor = selectedColor
        initCompose()
    }

    private fun initCompose() {
        view_compose_map_type_selector.setContent {
            MapTypeSelector()
        }
    }

    private var _googleMapPlus: MapViewPlus.GoogleMapPlus? = null
        set(value) {
            field = value
            selectDefaultType()
        }
    var googleMapPlus: MapViewPlus.GoogleMapPlus
        get() = _googleMapPlus ?: throw UninitializedPropertyAccessException("googleMapPlus was not initialized")
        set(value) {
            _googleMapPlus = value
        }

    private fun selectDefaultType() {
        googleMapPlus.mapType = GoogleMap.MAP_TYPE_NORMAL
    }
    private fun selectTerrainType() {
        googleMapPlus.mapType = GoogleMap.MAP_TYPE_TERRAIN
    }
    private fun selectSatelliteType() {
        googleMapPlus.mapType = GoogleMap.MAP_TYPE_SATELLITE
    }

    init {
        inflate(context, R.layout.selector_map_type, this)
    }

    private var toggleIcon by mutableStateOf(R.drawable.ic_layers)
    private var selectorVisible by mutableStateOf(false)
    private var defaultTypeSelected by mutableStateOf(true )
    private var satelliteTypeSelected by mutableStateOf(false)
    private var terrainTypeSelected by mutableStateOf(false)

    @Composable
    private fun MapTypeSelectorItem(icon: Int, text: String, selected: Boolean, onClick: () -> Unit) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier
                    .size(50.dp)
                    .clip(cardItemShape)
                    .border(
                        width = if (selected) 3.dp else 0.dp,
                        color = if (selected) selectedColor else surfaceColor,
                        shape = cardItemShape
                    )
                    .clickable(onClick = onClick)
            )
            Text(
                text = text,
                color = if (selected) selectedColor else onSurfaceColor,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.W600,
                fontFamily = FontFamily(Font(R.font.product_sans_regular))
            )
        }
    }
    
    @ExperimentalAnimationApi
    @Composable
    fun MapTypeSelector() {
        ConstraintLayout(
            modifier = Modifier
                .width(300.dp)
                .height(150.dp)
        ) {
            val (fab, card) = createRefs()

            FloatingActionButton(
                onClick = {
                    toggleIcon = if (selectorVisible) R.drawable.ic_layers else R.drawable.ic_clear
                    selectorVisible = !selectorVisible
                },
                modifier = Modifier
                    .constrainAs(fab) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .padding(padding)
                    .size(40.dp),
                elevation = elevation(
                    defaultElevation = elevation,
                    pressedElevation = elevation * 2),
                backgroundColor = surfaceColor,
            ) {
                Icon(
                    painter = painterResource(id = toggleIcon),
                    tint = onSurfaceColor,
                    contentDescription = null
                )
            }
            if (selectorVisible) {
                Card(
                    modifier = Modifier
                        .constrainAs(card) {
                            top.linkTo(parent.top)
                            end.linkTo(fab.start)
                        }
                        .padding(top = padding, start = padding, bottom = padding),
                    shape = cardShape,
                    elevation = elevation,
                    backgroundColor = surfaceColor
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Map Type",
                            fontSize = 16.sp,
                            color = onSurfaceColor,
                            fontWeight = FontWeight.W600,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily(Font(R.font.product_sans_regular)),
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MapTypeSelectorItem(
                                icon = R.drawable.map_type_default,
                                text = "Default",
                                defaultTypeSelected
                            ) {
                                defaultTypeSelected = true
                                terrainTypeSelected = false
                                satelliteTypeSelected = false
                                selectDefaultType()
                            }
                            MapTypeSelectorItem(
                                icon = R.drawable.map_type_satellite,
                                text = "Satellite",
                                satelliteTypeSelected
                            ) {
                                defaultTypeSelected = false
                                satelliteTypeSelected = true
                                terrainTypeSelected = false
                                selectSatelliteType()
                            }
                            MapTypeSelectorItem(
                                icon = R.drawable.map_type_terrain,
                                text = "Terrain",
                                terrainTypeSelected
                            ) {
                                defaultTypeSelected = false
                                satelliteTypeSelected = false
                                terrainTypeSelected = true
                                selectTerrainType()
                            }
                        }
                    }
                }
            }
        }
    }
}
