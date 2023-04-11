package com.example.sempertibi.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sempertibi.data.entities.*
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

    @Query("SELECT * FROM user WHERE user_id = :user_id")
    fun getUserByID(user_id: Int): User?

    @Query("SELECT * FROM user WHERE email = :email")
    fun getUserByMail(email: String): User?

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // Mood Journal database interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMood(moodJournal: MoodJournal)

    @Transaction    // Thread safe manner
    @Query("SELECT * FROM moodJournal WHERE user_id = :user_id AND entry_date = :entry_date")// Query the situation, that is inserted in the Moodjournal for a specific day
    fun readMoodOfUser(user_id: Int, entry_date: String): MoodJournal?

    @Update
    suspend fun updateMood(moodJournal: MoodJournal)

    @Delete
    suspend fun deleteMood(moodJournal: MoodJournal)

    // Stress Test PSS database interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStressPSS(testPSS: StressPSS)

    @Query("SELECT * FROM stressPSS ORDER BY user_id ASC")
    fun readStressTestPSS(): LiveData<List<StressPSS>>

    @Query("SELECT * FROM stressPSS WHERE user_id = :user_id ORDER BY testPSS_date DESC LIMIT 7")
    fun getPSSLast7Entries(user_id: Int): List<StressPSS>

    @Query("SELECT COUNT(*) FROM stressPSS WHERE user_id = :user_id")
    fun getPSSNumEntries(user_id: Int): Int

    @Query("SELECT * FROM stressPSS WHERE user_id = :user_id AND testPSS_date = :date")
    fun getStressPSSByUserIdAndDate(user_id: Int, date: String): StressPSS?

    @Update
    suspend fun updateStressTestPSS(testPSS: StressPSS)

    @Delete
    suspend fun deleteStressTestPSS(testPSS: StressPSS)

    // Stress Test HRV database interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStressHRV(testHRV: StressHRV)

    @Query("SELECT * FROM stressHRV ORDER BY user_id ASC")
    fun readStressTestHRV(): LiveData<List<StressHRV>>

    @Query("SELECT * FROM stressHRV WHERE user_id = :user_id ORDER BY testHRV_date DESC LIMIT 7")
    fun getHRVLast7Entries(user_id: Int): List<StressHRV>

    @Update
    suspend fun updateStressTestHRV(testHRV: StressHRV)

    @Delete
    suspend fun deleteStressTestHRV(testHRV: StressHRV)

    @Query("SELECT * FROM securityQuestion WHERE user_id = :user_id")
    fun getSecurityQuestions(user_id: Int): List<SecurityQuestion>

    @Query("SELECT * FROM securityQuestion WHERE user_id = :user_id ORDER BY RANDOM() LIMIT 2")
    fun getTwoRandomSecurityQuestions(user_id: Int): List<SecurityQuestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityQuestions(question: SecurityQuestion)

    @Update
    suspend fun updateSecurityQuestions(question: SecurityQuestion)

    @Query("SELECT * FROM toDoItems WHERE user_id = :user_id")
    fun getAllTodoItems(user_id: Int): LiveData<List<ToDoItem>>

    @Query("SELECT * FROM toDoItems WHERE todo_id = :todo_id")
    fun getTodoItemByID(todo_id: Int): ToDoItem

    @Insert
    suspend fun insertTodoItem(todoItem: ToDoItem)

    @Update
    suspend fun updateTodoItem(todoItem: ToDoItem)

    @Delete
    suspend fun deleteTodoItem(todoItem: ToDoItem)
}