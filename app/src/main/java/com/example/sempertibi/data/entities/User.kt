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
    var name: String,
    var passwordHash: String,
    var salt: String,
    var gender: String,
    var email: String,
    var notification: Boolean,
)/* {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (user_id != other.user_id) return false
        if (name != other.name) return false
        if (!salt.contentEquals(other.salt)) return false
        if (!passwordHash.contentEquals(other.passwordHash)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user_id
        result = 31 * result + name.hashCode()
        result = 31 * result + salt.contentHashCode()
        result = 31 * result + passwordHash.contentHashCode()
        return result
    }

}*/