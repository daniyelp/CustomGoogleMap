package com.example.custom_google_map

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.selector_map_type.view.*

class MapTypeSelector(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {

    lateinit var customGoogleMap: CustomMapView.CustomGoogleMap

    var selectorVisible = false
    val oldColor : Int

    init {
        inflate(context, R.layout.selector_map_type, this)

        oldColor = text_map_type_default.currentTextColor

        fab_map_type.setOnClickListener {
            if(selectorVisible) {
                hideSelector(it)
            } else {
                displaySelector(it)
            }
        }

        btn_map_type_terrain.setOnClickListener {
            selectTerrain()
        }

        btn_map_type_default.setOnClickListener {
            selectDefault()
        }

        btn_map_type_satellite.setOnClickListener {
            selectSatellite()
        }

    }

    private fun displaySelector(view: View) {
        selectorVisible = true
        layout_map_type.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(200)
                .setListener( object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {
                        (view as FloatingActionButton).setImageResource(R.drawable.ic_clear)
                    }

                    override fun onAnimationEnd(animation: Animator?) {

                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                }
                )
        }
    }

    private fun hideSelector(view: View) {
        selectorVisible = false
        layout_map_type
            .animate()
            .alpha(0f)
            .setDuration(200)
            .setListener( object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    (view as FloatingActionButton).setImageResource(R.drawable.ic_layers)
                }

                override fun onAnimationEnd(animation: Animator?) {

                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            })
    }

    private fun selectTerrain() {
        btn_map_type_terrain.setBackgroundResource(R.drawable.background_map_type_btn_post_click)
        btn_map_type_default.setBackgroundResource(R.drawable.background_map_type_btn)
        btn_map_type_satellite.setBackgroundResource(R.drawable.background_map_type_btn)
        customGoogleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        text_map_type_terrain.setTextColor(Color.BLUE)
        text_map_type_default.setTextColor(oldColor)
        text_map_type_satellite.setTextColor(oldColor)
    }

    private fun selectDefault() {
        btn_map_type_default.setBackgroundResource(R.drawable.background_map_type_btn_post_click)
        btn_map_type_terrain.setBackgroundResource(R.drawable.background_map_type_btn)
        btn_map_type_satellite.setBackgroundResource(R.drawable.background_map_type_btn)
        customGoogleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        text_map_type_default.setTextColor(Color.BLUE)
        text_map_type_satellite.setTextColor(oldColor)
        text_map_type_terrain.setTextColor(oldColor)
    }

    private fun selectSatellite() {
        btn_map_type_satellite.setBackgroundResource(R.drawable.background_map_type_btn_post_click)
        btn_map_type_default.setBackgroundResource(R.drawable.background_map_type_btn)
        btn_map_type_terrain.setBackgroundResource(R.drawable.background_map_type_btn)
        customGoogleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        text_map_type_satellite.setTextColor(Color.BLUE)
        text_map_type_default.setTextColor(oldColor)
        text_map_type_terrain.setTextColor(oldColor)
    }

}
