package com.example.sempertibi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "stressHRV")
data class StressHRV (
    @PrimaryKey(autoGenerate = true)
    val testHRV_id: Int,
    val user_id: Int,
    val testHRV_date: Date,
    val HRV_score: Int
)