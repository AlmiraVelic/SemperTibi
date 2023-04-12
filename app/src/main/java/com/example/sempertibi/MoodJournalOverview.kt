package com.example.sempertibi

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDao
import com.example.sempertibi.data.UserDatabase
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MoodJournalOverview : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var fabButton: ExtendedFloatingActionButton
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_journal_overview)

        initViews()
        setupPieChart()

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

        initUserDao()

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateString = dateFormat.format(currentDate)

        GlobalData.dateInCalendar = dateString
        GlobalData.dateToday = dateString

        updateFabBtn(dateString)

        observeDateChanges()

        fabButton.setOnClickListener {
            val intent = Intent(this, MoodJournalNew::class.java)
            startActivity(intent)
        }
    }

    private fun initViews() {
        calendarView = findViewById(R.id.calendarView)
        calendarView.firstDayOfWeek = Calendar.MONDAY
        fabButton = findViewById(R.id.ext_fab)
    }

    private fun initUserDao() {
        userDao = UserDatabase.getInstance(this).userDao()
    }

    private fun observeDateChanges() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val dateInCalendar = formatDate(dayOfMonth, month, year)
            GlobalData.dateInCalendar = dateInCalendar

            updateFabBtn(dateInCalendar)
        }
    }

    private fun formatDate(dayOfMonth: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun updateFabBtn(date: String) {
        lifecycleScope.launch {
            val existingEntry = withContext(Dispatchers.IO) {
                userDao.readMoodOfUser(GlobalData.userID!!, date)
            }

            if (existingEntry == null) {
                fabButton.setIconResource(R.drawable.ic_add)
                fabButton.text = getString(R.string.newEntry)
            } else {
                GlobalData.moodEntryID = existingEntry.entry_id
                fabButton.setIconResource(R.drawable.ic_edit)
                fabButton.text = getString(R.string.editEntry)
            }
        }
    }

    private fun setupPieChart() {
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val pieChartText = findViewById<TextView>(R.id.tvMoodPie)

        lifecycleScope.launch {
            val moodScores = withContext(Dispatchers.IO) {
                userDao.getMoodLast7Entries(GlobalData.userID!!)
            }

            if (moodScores.isNotEmpty()) {

                pieChart.setUsePercentValues(false)
                pieChart.description.isEnabled = false
                pieChart.legend.isEnabled = false
                pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                pieChart.legend.setDrawInside(false)
                pieChart.holeRadius = 40f

                val moodCounts = moodScores.groupingBy { it }.eachCount()

                val entries = moodCounts.entries.map {
                    val moodIcon = when (it.key) {
                        5 -> "😊"
                        1 -> "😢"
                        2 -> "😠"
                        4 -> "😲"
                        3 -> "😨"
                        else -> ""
                    }
                    PieEntry(it.value.toFloat(), moodIcon)
                }

                val dataSet = PieDataSet(entries, "")
                dataSet.colors = listOf(
                    ContextCompat.getColor(this@MoodJournalOverview, R.color.colorMood1),
                    ContextCompat.getColor(this@MoodJournalOverview, R.color.colorMood2),
                    ContextCompat.getColor(this@MoodJournalOverview, R.color.colorMood3),
                    ContextCompat.getColor(this@MoodJournalOverview, R.color.colorMood4),
                    ContextCompat.getColor(this@MoodJournalOverview, R.color.colorMood5)
                )

                dataSet.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }

                val data = PieData(dataSet)
                data.setValueTextSize(14f)
                data.setValueTextColor(
                    ContextCompat.getColor(
                        this@MoodJournalOverview,
                        R.color.logo_font
                    )
                )

                pieChart.data = data
                pieChart.invalidate()
            } else {
                pieChart.visibility = View.GONE
                pieChartText.visibility = View.GONE
            }
        }
    }
}
