package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class TipPlans : AppCompatActivity() {
    private lateinit var playerView: YouTubePlayerView
    private lateinit var linkTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip_plans)

        // https://github.com/PierfrancescoSoffritti/android-youtube-player
        // Library to embed Youtube videos
        playerView = findViewById(R.id.VideoPlans)
        lifecycle.addObserver(playerView)

        // link in Text should be clickable
        linkTextView = findViewById(R.id.tvPlans)
        linkTextView.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Notification")
                .setMessage("You are leaving the app now to a 3rd party website. Please click again on the text to get there.")
                .setPositiveButton("Ok"){_,_->
                    linkTextView.movementMethod = LinkMovementMethod.getInstance()
                    linkTextView.requestFocus()
                }
                .setNegativeButton("Cancel", null)
                .show()
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
                    android.app.AlertDialog.Builder(this).setTitle("Sign Out")
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