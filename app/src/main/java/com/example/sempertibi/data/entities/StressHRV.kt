package com.example.sempertibi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class StressHRV (
    @PrimaryKey(autoGenerate = true)
    val testHRV_id: Int,
    val user_id: Int,
    val testHRV_date: String,
    val HRV_score: Int
)