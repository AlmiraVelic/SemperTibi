package com.example.sempertibi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
This class represents the user table within the database
 */
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val user_id: Int,
    val name: String,
    val passwordHash: ByteArray,
    val salt: ByteArray,
    val gender: String,
    val email: String,
)