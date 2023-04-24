package com.example.sempertibi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
This class represents the mood journal table within the database
 */
@Entity(tableName = "moodJournal")
//@Entity(tableName = "moodJournal", primaryKeys = ["entry_id", "user_id"])
data class MoodJournal(
    @PrimaryKey(autoGenerate = true)
    val entry_id: Int,
    val user_id: Int,
    val entry_date: String,
    val situation: String,
    val emotion: String,
    val achievement: String,
    val mood_score: Int
)