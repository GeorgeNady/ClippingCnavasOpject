package com.george.clippingcanvasobject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clippingView = ClippingView(this)
        setContentView(clippingView)


    }
}