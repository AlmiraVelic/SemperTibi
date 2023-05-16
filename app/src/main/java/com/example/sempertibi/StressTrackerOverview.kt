package com.example.sempertibi

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.StressPSS
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StressTrackerOverview : AppCompatActivity() {

    // declare UI elements
    lateinit var barChart: HorizontalBarChart
    lateinit var newMeasurePSS: Button
    lateinit var newMeasureHRV: Button
    lateinit var learnMore: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stress_tracker_overview)
        StrictMode.enableDefaults()

        // initialize UI elements
        barChart = findViewById(R.id.barChart)
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)
        barChart.animateY(1000)

        newMeasurePSS = findViewById(R.id.btnNewMeasurePSS)
        newMeasureHRV = findViewById(R.id.btnNewMeasureHRV)
        learnMore = findViewById(R.id.btnLearnMore)

        // initialize database access
        val dao = UserDatabase.getInstance(this).userDao()
        val userID = GlobalData.userID

        // launch a coroutine to fetch data from the database in the background
        lifecycleScope.launch {

            // add default entries to database if there are less than 7 entries
            withContext(Dispatchers.IO) {
                val currentDate = Date()
                val numEntries = dao.getPSSNumEntries(userID!!)
                if (numEntries < 7) {
                    for (i in 1..(7 - numEntries)) {
                        val date = Calendar.getInstance().apply { time = currentDate }
                        date.add(Calendar.DAY_OF_MONTH, (i - 50))
                        val testDate =
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.time)
                        dao.addStressPSS(StressPSS(0, userID, testDate, 0))
                    }
                }
            }

            val pssEntries = withContext(Dispatchers.IO) {
                dao.getPSSLast7Entries(userID!!)
            }

            val entries = pssEntries.mapIndexed { index, pssEntry ->
                BarEntry(index.toFloat(), pssEntry.PSS_score.toFloat())
            }

            val dataSet = BarDataSet(entries, "PSS scores")

            dataSet.color =
                ContextCompat.getColor(this@StressTrackerOverview, R.color.lotus_blue)
            dataSet.valueTextColor =
                ContextCompat.getColor(this@StressTrackerOverview, R.color.anti_flash_white)
            dataSet.valueTextSize = 14f
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }

            val data = BarData(dataSet)
            barChart.data = data

            val xAxisBottom = barChart.xAxis
            xAxisBottom.position = XAxis.XAxisPosition.BOTTOM
            xAxisBottom.setDrawGridLines(false)
            val labels = pssEntries.map { it.testPSS_date }
            xAxisBottom.valueFormatter = IndexAxisValueFormatter(labels)
            xAxisBottom.labelRotationAngle = 0f
            xAxisBottom.granularity = 1f
            xAxisBottom.xOffset = 15f

            val yAxisRight = barChart.axisRight
            yAxisRight.setDrawGridLines(true)
            yAxisRight.setDrawAxisLine(false)
            yAxisRight.axisMinimum = 0f
            yAxisRight.axisMaximum = 40f
            yAxisRight.textColor =
                ContextCompat.getColor(this@StressTrackerOverview, R.color.logo_font)
            yAxisRight.isEnabled = true
            yAxisRight.valueFormatter = YAxisValueFormatter()

            val yAxisLeft = barChart.axisLeft
            yAxisLeft.setDrawGridLines(false)
            yAxisLeft.setDrawAxisLine(false)
            yAxisLeft.labelCount = 3
            yAxisLeft.axisMinimum = 0f
            yAxisLeft.axisMaximum = 40f
            yAxisLeft.textColor =
                ContextCompat.getColor(this@StressTrackerOverview, R.color.logo_font)
            yAxisLeft.isEnabled = true

            val legend = barChart.legend
            legend.isEnabled = true
            legend.textSize = 14f
            legend.textColor =
                ContextCompat.getColor(this@StressTrackerOverview, R.color.logo_font)

            barChart.description.isEnabled = false
            barChart.setTouchEnabled(false)
            barChart.setDrawValueAboveBar(false)

            barChart.setExtraOffsets(50f, 0f, 0f, 0f)

            barChart.invalidate()
        }

        // set up button click listeners to launch new measurement activities or information activities
        newMeasurePSS.setOnClickListener {
            lifecycleScope.launch {
                val pssEntries = withContext(Dispatchers.IO) {
                    dao.getPSSLast7Entries(userID!!)
                }

                val lastEntryDate = pssEntries.firstOrNull()?.testPSS_date
                val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                if (lastEntryDate != null) {
                    val daysSinceLastPSS = daysBetween(lastEntryDate, dateString)
                    if (daysSinceLastPSS < 30) {
                        AlertDialog.Builder(this@StressTrackerOverview)
                            .setTitle("PSS Score Info")
                            .setMessage("The last PSS Score evaluation has been done in less than 30 days. Do you really want to make a new PSS Test?")
                            .setPositiveButton("Yes") { _, _ ->
                                val intent = Intent(this@StressTrackerOverview, StressTestPSS::class.java)
                                startActivity(intent)
                            }
                            .setNegativeButton("No") { _, _ ->
                                // Do nothing
                            }
                            .show()
                    } else {
                        val intent = Intent(this@StressTrackerOverview, StressTestPSS::class.java)
                        startActivity(intent)
                    }
                } else {
                    // No PSS tests found, launch new test activity
                    val intent = Intent(this@StressTrackerOverview, StressTestPSS::class.java)
                    startActivity(intent)
                }
            }
        }

        newMeasureHRV.setOnClickListener {
            val intent = Intent(this, StressTestHRV::class.java)
            startActivity(intent)
        }

        learnMore.setOnClickListener {
            val intent = Intent(this, StressTrackerLearn::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuDashboard -> {
                    val intent = Intent(this, Dashboard::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuMoodJournal -> {
                    val intent = Intent(this, MoodJournalOverview::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuStressTracker -> {
                    val intent = Intent(this, StressTrackerOverview::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuToDoList -> {
                    val intent = Intent(this, ToDoList::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuSignOut -> {
                    AlertDialog.Builder(this).setTitle("Sign Out")
                        .setMessage("Do you really want to sign out?")
                        .setPositiveButton("Yes") { _, _ ->
                            // Handle sign out here
                            val myApplication = applicationContext as MyApplication
                            myApplication.clearGlobalData()
                            val packageManager = applicationContext.packageManager
                            val intent =
                                packageManager.getLaunchIntentForPackage(applicationContext.packageName)
                            val componentName = intent!!.component
                            val mainIntent = Intent.makeRestartActivityTask(componentName)
                            applicationContext.startActivity(mainIntent)
                        }
                        .setNegativeButton("No") { _, _ ->
                            val intent = Intent(this, Dashboard::class.java)
                            startActivity(intent)
                        }
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun daysBetween(date1: String, date2: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val d1 = sdf.parse(date1)
        val d2 = sdf.parse(date2)
        val diff = d2!!.time - d1!!.time
        return (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)).toInt()
    }
}
