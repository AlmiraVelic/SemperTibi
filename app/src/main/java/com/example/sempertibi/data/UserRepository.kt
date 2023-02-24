package com.example.sempertibi.data

import androidx.lifecycle.LiveData
import com.example.sempertibi.data.entities.User


/*
A Repository class abstracts access to multiple data sources.
The repository is not part of the Architecture Components libraries,
but is a suggested best practice for code separation and architecture
 */

class UserRepository(private val userDao: UserDao) {

    val readAllData: LiveData<List<User>> = userDao.readAllUserData()

    suspend fun addUser(user: User){
        userDao.addUser(user)
    }
}

