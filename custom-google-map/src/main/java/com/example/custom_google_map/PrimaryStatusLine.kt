package com.example.custom_google_map

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.line_primary_status.view.*

class PrimaryStatusLine(context: Context, attributes: AttributeSet) : ConstraintLayout(context, attributes) {

    private val TAG = "LINE"
    private val view: TextView

    init {
        inflate(context, R.layout.line_primary_status, this)
        view = text_line_primary_status
        view.visibility = View.GONE
    }

    fun instant(text: String, color: Int, infinite: Boolean = false) {
        view.apply {
            this.text = text
            backgroundTintList = ColorStateList.valueOf(color)
            //if I don't do this, the width of the textview won't shrink if I set the text smaller than the previous text
            visibility = View.GONE
            visibility = View.VISIBLE
        }
        if(!infinite) {
            Handler(Looper.getMainLooper()).postDelayed({
                slideLeft()
            }, 2000)
        }
    }

    fun slideRight(text: String, color: Int, infinite: Boolean = false) {
        view.apply {
            this.text = text
            backgroundTintList = ColorStateList.valueOf(color)
            visibility = View.GONE
        }
        slideRight()
        if(!infinite) {
            Handler(Looper.getMainLooper()).postDelayed({
                slideLeft()
            }, 2000)
        }
    }

    private fun slideRight() {
        val animate = TranslateAnimation(
            0f,
            view.width.toFloat(),
            0f,
            0f
        ).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    view.visibility = View.VISIBLE
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

    private fun slideLeft() {
        val animate = TranslateAnimation(
            0f,
            0f,
            0f,
            0f,
        ).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    view.visibility = View.VISIBLE
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