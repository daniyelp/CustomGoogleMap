package com.example.customgooglemapexample.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.customgooglemapexample.R
import dagger.hilt.android.AndroidEntryPoint
import android.view.MotionEvent




@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        Log.d("TOUCH", "x is $x and y is $y")
        return false
    }
}