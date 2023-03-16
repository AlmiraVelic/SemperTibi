package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.github.mikephil.charting.charts.LineChart
import kotlinx.coroutines.launch
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import com.example.sempertibi.data.UserDao
import com.example.sempertibi.data.entities.StressPSS
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class StressTrackerOverview : AppCompatActivity() {

    // declare UI elements
    lateinit var lineChart: LineChart
    lateinit var newMeasurePSS: Button
    lateinit var newMeasureHRV: Button
    lateinit var learnMore: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stress_tracker_overview)

        // initialize UI elements
        lineChart = findViewById(R.id.lineChart)
        newMeasurePSS = findViewById(R.id.btnNewMeasurePSS)
        newMeasureHRV = findViewById(R.id.btnNewMeasureHRV)
        learnMore = findViewById(R.id.btnLearnMore)

        // initialize database access
        val dao = UserDatabase.getInstance(this).userDao()
        val userID = GlobalData.userID

        // launch a coroutine to fetch data from the database in the background
        lifecycleScope.launch {
            Log.d("StressTestOV", "1")
            // add default entries to database if there are less than 7 entries
            withContext(Dispatchers.IO) {
                Log.d("StressTestOV", "2")
                val currentDate = Date()
                Log.d("StressTestOV", "3")
                val numEntries = dao.getPSSNumEntries(userID!!)
                Log.d("StressTestOV", "4")
                if (numEntries<7) {
                    for (i in 1..(7 - numEntries)) {
                        Log.d("StressTestOV", "5")
                        val date = Calendar.getInstance().apply { time = currentDate }
                        Log.d("StressTestOV", "6")
                        date.add(Calendar.DAY_OF_MONTH, -(i + 365))
                        Log.d("StressTestOV", "7")
                        val testDate =
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date.time)
                        Log.d("StressTestOV", "8")
                        dao.addStressPSS(StressPSS(0, userID!!, testDate, 0))
                        Log.d("StressTestOV", "9")
                    }
                }
            }

            val pssEntries = withContext(Dispatchers.IO) {
                dao.getPSSLast7Entries(userID!!)
            }

            // update the UI with the fetched data on the main thread
            withContext(Dispatchers.Main) {
                val pssScores = mutableListOf<Entry>()
                val pssDates = mutableListOf<String>()

                // process the fetched data into a format that can be displayed on the chart
                pssEntries.forEachIndexed { index, stressPSS ->
                    pssScores.add(Entry(index.toFloat(), stressPSS.PSS_score.toFloat()))
                    pssDates.add(stressPSS.testPSS_date)
                }

                // configure the chart dataset with the processed data
                val dataSet = LineDataSet(pssScores, "PSS Scores")
                dataSet.setCircleColor(Color.rgb(0, 128, 128))
                dataSet.color = Color.rgb(0, 128, 128)
                dataSet.lineWidth = 3f
                dataSet.setDrawValues(false)
                dataSet.valueTextColor = Color.RED // set text color
                dataSet.valueTextSize = 14f // set text size
                dataSet.valueTypeface = Typeface.create("Arial", Typeface.NORMAL) // set font

                // create a chart data object with the dataset
                val lineData = LineData(dataSet)

                // configure the chart with the data and display settings
                lineChart.data = lineData
                // format y-Axis
                lineChart.axisLeft.axisMinimum = 0f // set minimum value of the y-axis to 0
                lineChart.axisLeft.axisMaximum = 50f // set maximum value of the y-axis to 50
                lineChart.axisLeft.valueFormatter =
                    YAxisValueFormatter() // format values to low, medium and high
                lineChart.axisLeft.labelCount = 3
                lineChart.axisLeft.setDrawLabels(true)
                lineChart.axisLeft.setDrawGridLines(false)
                lineChart.axisRight.isEnabled = false
                // format x-Axis
                lineChart.xAxis.isEnabled = false
                lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(pssDates)
                lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                lineChart.xAxis.setDrawLabels(false)
                lineChart.xAxis.setDrawGridLines(false)

                lineChart.setDrawGridBackground(false)
                lineChart.description.isEnabled = false
                lineChart.setBackgroundColor(Color.LTGRAY)
                lineChart.invalidate() // refresh the chart display
            }
        }

        // set up button click listeners to launch new measurement activities or information activities
        newMeasurePSS.setOnClickListener {
            val intent = Intent(this, StressTestPSS::class.java)
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
