package com.example.custom_google_map

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.bar_status.view.*

@ExperimentalAnimationApi
class StatusBar(context: Context, attributes: AttributeSet) : ConstraintLayout(context, attributes) {

    init {
        inflate(context, R.layout.bar_status, this)
        view_compose_status_bar.setContent {
            StatusBar()
        }
    }

    private val isVisible = mutableStateOf(false)
    private val text = mutableStateOf("")
    private val color = mutableStateOf(R.color.gray)

    private val untilGoneDuration = 1500L
    private var hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hide() }
    private var hasCallBack = false

    fun display(text: String, color: Int, infinite: Boolean = false) {

        hideHandler.removeCallbacks(hideRunnable)

        isVisible.value = true
        this.text.value = text
        this.color.value = color

        if(!infinite) {
            hideHandler.postDelayed(hideRunnable, untilGoneDuration)
            hasCallBack = true
        }
    }

    fun hide(now: Boolean = true) {

        if(now) {
            hideHandler.removeCallbacks(hideRunnable)
            hasCallBack = false
            isVisible.value = false
        } else {
            if(!hasCallBack) {
                isVisible.value = false
            }
        }
    }

    @ExperimentalAnimationApi
    @Composable
    fun StatusBar() {
        AnimatedVisibility(
            visible = isVisible.value,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier
                .padding(8.dp)
                .clip(
                    shape = RoundedCornerShape(5)
                )
        ) {
            Card(

                backgroundColor = colorResource(color.value),
            ) {
                Text(
                    modifier = Modifier
                        .padding(6.dp),
                    text = text.value,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }

}