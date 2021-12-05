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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import kotlinx.android.synthetic.main.selector_map_type.view.*

@ExperimentalAnimationApi
class MapTypeSelector: ConstraintLayout {

    constructor(context: Context): super(context)
    constructor(context: Context, attributes: AttributeSet): super(context, attributes)

    private var _googleMapPlus: MapViewPlus.GoogleMapPlus? = null
        set(value) {
            field = value
            selectDefaultType()
        }
    var googleMapPlus: MapViewPlus.GoogleMapPlus
        get() = _googleMapPlus ?: throw UninitializedPropertyAccessException("customGoogleMap was not initialized")
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

        view_compose_map_type_selector.setContent {
            MapTypeSelector()
        }
    }

    private val icon = mutableStateOf(R.drawable.ic_layers)
    private val selectorVisible = mutableStateOf(false)
    private val defaultTypeSelected = mutableStateOf(true )
    private val satelliteTypeSelected = mutableStateOf(false)
    private val terrainTypeSelected = mutableStateOf(false)

    @Composable
    private fun MapTypeSelectorItem(icon: Int, text: String, selected: MutableState<Boolean>, onClick: () -> Unit) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(20))
                    .border(
                        if (selected.value) 3.dp else 0.dp,
                        if (selected.value) colorResource(R.color.maps_blue) else Color.White,
                        RoundedCornerShape(20)
                    )
                    .clickable(onClick = onClick)
            )
            Text(
                text = text,
                color = if (selected.value) colorResource(R.color.maps_blue) else colorResource(R.color.maps_gray),
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
    fun MapTypeSelector(elevation : Dp = 8.dp) {
        ConstraintLayout(
            modifier = Modifier
                .width(300.dp)
                .height(150.dp)
        ) {
            val (fab, card) = createRefs()

            FloatingActionButton(
                onClick = {
                    icon.value = if (selectorVisible.value) R.drawable.ic_layers else R.drawable.ic_clear
                    selectorVisible.value = !selectorVisible.value
                },
                modifier = Modifier
                    .constrainAs(fab) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .padding(elevation)
                    .size(40.dp),
                elevation = elevation(elevation, 0.dp),
                backgroundColor = Color.White,
            ) {
                Icon(
                    painter = painterResource(id = icon.value),
                    contentDescription = "map type selector"
                )
            }
            if (selectorVisible.value) {
                Card(
                    modifier = Modifier
                        .constrainAs(card) {
                            top.linkTo(parent.top)
                            end.linkTo(fab.start)
                        }
                        .padding(top = elevation, start = elevation, bottom = elevation),
                    shape = RoundedCornerShape(5),
                    elevation = elevation,
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Map Type",
                            fontSize = 16.sp,
                            color = colorResource(R.color.maps_gray),
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
                                defaultTypeSelected.value = true
                                terrainTypeSelected.value = false
                                satelliteTypeSelected.value = false
                                selectDefaultType()
                            }
                            MapTypeSelectorItem(
                                icon = R.drawable.map_type_satellite,
                                text = "Satellite",
                                satelliteTypeSelected
                            ) {
                                defaultTypeSelected.value = false
                                satelliteTypeSelected.value = true
                                terrainTypeSelected.value = false
                                selectSatelliteType()
                            }
                            MapTypeSelectorItem(
                                icon = R.drawable.map_type_terrain,
                                text = "Terrain",
                                terrainTypeSelected
                            ) {
                                defaultTypeSelected.value = false
                                satelliteTypeSelected.value = false
                                terrainTypeSelected.value = true
                                selectTerrainType()
                            }
                        }
                    }
                }
            }
        }
    }
}
