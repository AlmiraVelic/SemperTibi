package com.example.sempertibi

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class TipsTricksOverview : AppCompatActivity() {
    private lateinit var playerView: YouTubePlayerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tips_tricks_overview)

        // https://github.com/PierfrancescoSoffritti/android-youtube-player
        // Library to embed Youtube videos
        playerView = findViewById(R.id.VideoOverview)
        lifecycle.addObserver(playerView)
        playerView = findViewById(R.id.VideoOverview2)
        lifecycle.addObserver(playerView)

        val icon = findViewById<ImageView>(R.id.logo)
        icon.bringToFront()

        val scrollView = findViewById<NestedScrollView>(R.id.nestedScrollView)
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY >= 0) {
                icon.visibility = View.GONE
            } else {
                icon.visibility = View.VISIBLE
            }
        }

        val breath = findViewById<Button>(R.id.btnBreath)
        breath.setOnClickListener{
            val intent = Intent(this, TipBreath::class.java)
            startActivity(intent)
        }

        val breaks = findViewById<Button>(R.id.btnBreaks)
        breaks.setOnClickListener{
            val intent = Intent(this, TipBreaks::class.java)
            startActivity(intent)
        }

        val sports = findViewById<Button>(R.id.btnSports)
        sports.setOnClickListener{
            val intent = Intent(this, TipSports::class.java)
            startActivity(intent)
        }

        val plans = findViewById<Button>(R.id.btnPlans)
        plans.setOnClickListener{
            val intent = Intent(this, TipPlans::class.java)
            startActivity(intent)
        }

        val nutrition = findViewById<Button>(R.id.btnNutrition)
        nutrition.setOnClickListener{
            val intent = Intent(this, TipNutrition::class.java)
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
                R.id.menuSettings -> {
                    val intent = Intent(this, Settings::class.java)
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
                        .setNegativeButton("No"){_,_->
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
}