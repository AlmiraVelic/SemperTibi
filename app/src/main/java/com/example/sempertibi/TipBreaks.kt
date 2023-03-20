package com.example.sempertibi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class TipBreaks : AppCompatActivity() {
    private lateinit var playerView: YouTubePlayerView
    private lateinit var linkTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip_breaks)

        // https://github.com/PierfrancescoSoffritti/android-youtube-player
        // Library to embed Youtube videos
        playerView = findViewById(R.id.VideoBreaks)
        lifecycle.addObserver(playerView)

        // link in Text should be clickable
        linkTextView = findViewById(R.id.tvBreak)

        linkTextView.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Notification")
                .setMessage("You are leaving the app now to a 3rd party website")
                .setPositiveButton("Ok"){_,_->
                    linkTextView.movementMethod = LinkMovementMethod.getInstance()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}