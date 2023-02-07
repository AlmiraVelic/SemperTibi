package com.example.sempertibi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class TipNutrition : AppCompatActivity() {
    private lateinit var playerView: YouTubePlayerView
    private lateinit var linkTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip_nutrition)

        // https://github.com/PierfrancescoSoffritti/android-youtube-player
        // Library to embed Youtube videos
        playerView = findViewById(R.id.VideoNutrition)
        lifecycle.addObserver(playerView)

        // link in Text should be clickable
        linkTextView = findViewById(R.id.tvNutrition)
        linkTextView.movementMethod = LinkMovementMethod.getInstance()
    }

}