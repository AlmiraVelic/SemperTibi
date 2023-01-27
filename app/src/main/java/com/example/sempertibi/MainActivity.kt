package com.example.sempertibi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val enterButton = findViewById<Button>(R.id.btnStart)

        enterButton.setOnClickListener{
            var intent = Intent(this,SigninActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuDashboard -> {
                // Handle menu item 1 click
                return true
            }
            R.id.menuSignout -> {
                // Handle menu item 2 click
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}