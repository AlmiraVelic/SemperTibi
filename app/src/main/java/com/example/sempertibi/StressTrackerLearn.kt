package com.example.sempertibi

import android.os.Bundle
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity

class StressTrackerLearn : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stress_tracker_learn)

        expandableListView = findViewById(R.id.expandableListView)

        val faqList = listOf(
            FAQ(getString(R.string.whatPSS), getString(R.string.explainPSS)),
            FAQ(getString(R.string.whatHRV), getString(R.string.explainHRV))
        )

        val adapter = ExpandableListAdapter(this, faqList)

        expandableListView.setAdapter(adapter)

    }
}