package com.example.sempertibi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.widget.NestedScrollView

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<View>(R.id.switch1)

        val icon = findViewById<ImageView>(R.id.logo)
        icon.bringToFront()

        val scrollView = findViewById<NestedScrollView>(R.id.nestedScrollView)
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                icon.visibility = View.INVISIBLE
            } else {
                icon.visibility = View.VISIBLE
            }
        }
    }
}