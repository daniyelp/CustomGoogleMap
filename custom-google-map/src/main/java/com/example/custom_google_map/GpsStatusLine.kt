package com.example.custom_google_map

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.line_gps_status.view.*


class GpsStatusLine(context: Context, attributes: AttributeSet) : FrameLayout(context, attributes) {

    private val view: TextView

    init {
        inflate(context, R.layout.line_gps_status, this)
        view = text_gps_status
    }

    fun instant(text: String, color: Int, infinite: Boolean = false) {
        view.text = text
        view.setBackgroundColor(color)
        view.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            slideUp()
        }, 2000)
    }

    fun slide(text: String, color: Int, infinite: Boolean = false) {
        view.text = text
        view.setBackgroundColor(color)
        slideDown()
        if(!infinite) {
            Handler(Looper.getMainLooper()).postDelayed({
                slideUp()
            }, 2000)
        }
    }

    private fun slideDown() {
        Log.d("ANIMATION", "slide down invoked")
        val animate = TranslateAnimation(
            0f,
            0f,
            -view.height.toFloat(),
            0f
        ).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    view.visibility = View.VISIBLE
                    Log.d("ANIMATION", "slide down started")
                }

                override fun onAnimationEnd(animation: Animation?) {

                }

                override fun onAnimationRepeat(animation: Animation?) {

                }
            })
            duration = 500
            //fillAfter = true
        }

        view.startAnimation(animate)
    }

    private fun slideUp() {
        val animate = TranslateAnimation(
            0f,
            0f,
            0f,
            -view.height.toFloat()
        ).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    view.visibility = View.VISIBLE
                    Log.d("ANIMATION", "slide up started")
                }

                override fun onAnimationEnd(animation: Animation?) {
                    view.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }
            })
            duration = 500
            //fillAfter = true
        }

        view.startAnimation(animate)
    }

}