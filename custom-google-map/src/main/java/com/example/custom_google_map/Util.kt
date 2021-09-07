package com.example.custom_google_map

import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.time.Duration

fun vectorToBitmapDescriptor(@DrawableRes id: Int, @ColorInt color: Int, resources: Resources): BitmapDescriptor? {
    val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable!!.intrinsicWidth,
        vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    DrawableCompat.setTint(vectorDrawable, color)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun animateMarker(marker: Marker, to: LatLng, duration: Long = 300) {

    if(marker == null) return

    val from = marker.position

    fun interpolate(t: Float, a: LatLng, b: LatLng) =
        LatLng (
            a.latitude * (1 - t) + b.latitude * t,
            a.longitude * (1 - t) + b.longitude * t
        )

    with(ValueAnimator.ofFloat(0f, 1f)) {
        this.duration = duration
        interpolator = LinearInterpolator()
        addUpdateListener(object : ValueAnimator.AnimatorUpdateListener{
            override fun onAnimationUpdate(valueAnimator: ValueAnimator?) {
                valueAnimator?.let {
                    val t = it.animatedFraction
                    val latLng = interpolate(t, from, to)
                    marker.position = latLng
                }
            }
        })
        start()
    }

}
