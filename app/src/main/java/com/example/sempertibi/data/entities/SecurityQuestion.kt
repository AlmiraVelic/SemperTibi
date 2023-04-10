package com.example.sempertibi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "security_questions")
data class SecurityQuestion(
    @PrimaryKey(autoGenerate = true)
    val question_id: Int = 0,
    val user_id: Int,
    val question_text: String,
    val answer: String
)
