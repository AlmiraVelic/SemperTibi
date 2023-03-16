package com.example.sempertibi.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sempertibi.data.entities.MoodJournal
import com.example.sempertibi.data.entities.StressHRV
import com.example.sempertibi.data.entities.StressPSS
import com.example.sempertibi.data.entities.User
import java.util.*

/*
Dao = Data Access Object
It contains the methods used for accessing the database
 */

@Dao
interface UserDao {
    // If there is same user we would ignore that now

    // User database interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM user ORDER BY user_id ASC")
    fun readAllUserData(): LiveData<List<User>>

    @Query("SELECT * FROM user WHERE name = :name")
    fun getUserByUsername(name: String): User?

    @Query("SELECT * FROM user WHERE user_id = :id")
    fun getUserByID(id: Int): User?

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // Mood Journal database interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMood(moodJournal: MoodJournal)

    @Transaction    // Thread safe manner
    @Query("SELECT * FROM moodJournal WHERE user_id = :user_id AND entry_date = :entry_date")// Query the situation, that is inserted in the Moodjournal for a specific day
    fun readMoodOfUser(user_id: Int, entry_date: String): List<MoodJournal>

    @Update
    suspend fun updateMood(user: User)

    @Delete
    suspend fun deleteMood(user: User)

    // Stress Test PSS database interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStressPSS(testPSS: StressPSS)

    @Query("SELECT * FROM stressPSS ORDER BY user_id ASC")
    fun readStressTestPSS(): LiveData<List<StressPSS>>

    @Query("SELECT * FROM stressPSS WHERE user_id = :userId ORDER BY testPSS_date ASC LIMIT 7")
    fun getPSSLast7Entries(userId: Int): List<StressPSS>

    @Query("SELECT COUNT(*) FROM stressPSS WHERE user_id = :userId")
    fun getPSSNumEntries(userId: Int): Int

    @Query("SELECT * FROM stressPSS WHERE user_id = :userId AND testPSS_date = :date")
    fun getStressPSSByUserIdAndDate(userId: Int, date: String): StressPSS?

    @Update
    suspend fun updateStressTestPSS(testPSS: StressPSS)

    @Delete
    suspend fun deleteStressTestPSS(testPSS: StressPSS)

    // Stress Test HRV database interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStressHRV(testHRV: StressHRV)

    @Query("SELECT * FROM stressHRV ORDER BY user_id ASC")
    fun readStressTestHRV(): LiveData<List<StressHRV>>

    @Query("SELECT * FROM stressHRV WHERE user_id = :userId ORDER BY testHRV_date DESC LIMIT 7")
    fun getHRVLast7Entries(userId: Int): List<StressHRV>

    @Update
    suspend fun updateStressTestHRV(testHRV: StressHRV)

    @Delete
    suspend fun deleteStressTestHRV(testHRV: StressHRV)
}