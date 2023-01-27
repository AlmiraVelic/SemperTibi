package com.example.sempertibi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.widget.NestedScrollView

class TippsTricksOverview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tipps_tricks_overview)

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

        val breath = findViewById<Button>(R.id.btnBreath)
        breath.setOnClickListener{
            var intent = Intent(this, tippBreath::class.java)
            startActivity(intent)
        }

        val breaks = findViewById<Button>(R.id.btnBreaks)
        breaks.setOnClickListener{
            var intent = Intent(this, tippBreaks::class.java)
            startActivity(intent)
        }

        val sports = findViewById<Button>(R.id.btnSports)
        sports.setOnClickListener{
            var intent = Intent(this, tippSports::class.java)
            startActivity(intent)
        }

        val plans = findViewById<Button>(R.id.btnPlans)
        plans.setOnClickListener{
            var intent = Intent(this, tippPlans::class.java)
            startActivity(intent)
        }

        val nutrition = findViewById<Button>(R.id.btnNutrition)
        nutrition.setOnClickListener{
            var intent = Intent(this, tippNutrition::class.java)
            startActivity(intent)
        }
    }
}