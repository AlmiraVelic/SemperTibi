package com.example.sempertibi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "securityQuestion")
data class SecurityQuestion(
    @PrimaryKey(autoGenerate = true)
    val question_id: Int,
    val user_id: Int,
    val question_text: String,
    val answer: String
)
