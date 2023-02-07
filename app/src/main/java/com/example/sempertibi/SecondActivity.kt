package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val user = intent.getStringExtra("USER")
        val textView = findViewById<TextView>(R.id.tvOffer)
        val message = "Hi $user,\n\nHow do you feel?"
        textView.text = message
        val skip = findViewById<Button>(R.id.btnSkip)
        skip.setOnClickListener{
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }

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