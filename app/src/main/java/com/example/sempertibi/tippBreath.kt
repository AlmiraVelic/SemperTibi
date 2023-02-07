package com.example.sempertibi


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class tippBreath : AppCompatActivity() {
    private lateinit var playerView: YouTubePlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tipp_breath)

        // https://github.com/PierfrancescoSoffritti/android-youtube-player
        // Library to embed Youtube videos

        playerView = findViewById(R.id.VideoBreath)
        lifecycle.addObserver(playerView)
    }
}