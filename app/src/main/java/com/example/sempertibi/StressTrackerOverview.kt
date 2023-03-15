package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class StressTrackerOverview : AppCompatActivity() {

    lateinit var newMeasurePSS: Button
    lateinit var newMeasureHRV: Button
    lateinit var learnMore: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stress_tracker_overview)

        newMeasurePSS = findViewById(R.id.btnNewMeasurePSS)
        newMeasureHRV = findViewById(R.id.btnNewMeasureHRV)
        learnMore = findViewById(R.id.btnLearnMore)

        newMeasurePSS.setOnClickListener {
            var intent = Intent(this, StressTestPSS::class.java)
            startActivity(intent)
        }

        newMeasureHRV.setOnClickListener {
            var intent = Intent(this, StressTestHRV::class.java)
            startActivity(intent)
        }

        learnMore.setOnClickListener {
            var intent = Intent(this, StressTrackerLearn::class.java)
            startActivity(intent)
        }
    }
}
