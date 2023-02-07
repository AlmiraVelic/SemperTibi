package com.example.sempertibi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.NestedScrollView

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Buttons
        val moodJournal = findViewById<Button>(R.id.btnMoodJournal)
        moodJournal.setOnClickListener {
            val intent = Intent(this, MoodJournalOverview::class.java)
            startActivity(intent)
        }
        val stressTracker = findViewById<Button>(R.id.btnStressTracker)
        stressTracker.setOnClickListener {
            val intent = Intent(this, StressTrackerOverview::class.java)
            startActivity(intent)
        }

        val tippsTricks = findViewById<Button>(R.id.btnTipsTricks)
        tippsTricks.setOnClickListener {
            val intent = Intent(this, TipsTricksOverview::class.java)
            startActivity(intent)
        }
        val settings = findViewById<Button>(R.id.btnSettings)

        settings.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
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

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar_dashboard))
        //supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menuDashboard -> {
                Toast.makeText(this, "Dashboard clicked", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.menuMoodJournal -> {
                Toast.makeText(this, "Mood Journal clicked", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.menuStressTracker -> {
                Toast.makeText(this, "Stress Tracker clicked", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.menuSettings -> {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.menuSignout -> {
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
                finish()
                true
            }
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
}