package com.example.sempertibi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "stressPSS")
data class StressPSS (
    @PrimaryKey(autoGenerate = true)
    val testPSS_id: Int,
    val user_id: Int,
    val testPSS_date: String,
    val PSS_score: Int
    )