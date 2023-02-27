package com.example.sempertibi.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sempertibi.data.entities.MoodJournal
import com.example.sempertibi.data.entities.StressHRV
import com.example.sempertibi.data.entities.StressPSS
import com.example.sempertibi.data.entities.User
import com.example.sempertibi.data.relations.UserWithMoodjournal
import java.util.Date

/*
Dao = Data Access Object
It contains the methods used for accessing the database
 */

@Dao
interface UserDao {
    // If there is same user we would ignore that now
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMood(moodJournal: MoodJournal)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStressPSS(testPSS: StressPSS)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStressHRV(testHRV: StressHRV)

    @Query("SELECT * FROM user ORDER BY user_id ASC")
    fun readAllUserData(): LiveData<List<User>>

    // Thread safe manner
    @Transaction
    // Query the situation, that is inserted in the Moodjournal for a specific day
    @Query(value = "SELECT * FROM moodJournal WHERE user_id = :user_id AND entry_date = :entry_date")
    fun readMoodOfUserData(user_id: Int, entry_date: String): List<MoodJournal>

    @Query("SELECT EXISTS(SELECT * FROM moodJournal Where user_id = :user_id AND entry_date = :entry_date)")
    fun isRowIsExists(user_id: Int, entry_date: String): Boolean

}