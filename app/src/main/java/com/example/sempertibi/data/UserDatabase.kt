package com.example.sempertibi.data

import android.content.Context
import androidx.room.*
import com.example.sempertibi.data.entities.*

/*
User Database contains the database holder and serves as the main access point
for the underlying connection to the app's persisted, relational data.
 */

@Database(
    entities = [
        User::class,
        MoodJournal::class,
        StressPSS::class,
        StressHRV::class,
        SecurityQuestion::class
    ],
    version = 1,
    exportSchema = true
)


@TypeConverters(DateConverter::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    // this will be visible to other classes
    companion object {
        // Volatile: Rights to this field are immediately made visible to other threads
        // (helps prevent race conditions)
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            // makes sure that only a single thread is being performed, while database is being created
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
