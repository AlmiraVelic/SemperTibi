package com.example.sempertibi.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.sempertibi.data.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*
The ViewModel's role is to provide data to the UI and survive configuration changes.
A ViewModel acts as a communication center between the Repository and the UI.
 */

class UserViewModel(application: Application): AndroidViewModel(application) {

    private val readAllData: LiveData<List<User>>
    private val repository: UserRepository

    init{
        val userDao = UserDatabase.getInstance(application).userDao()
        repository = UserRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addUser(user: User){
        // Code should be run in a background thread (Dispatchers.IO)
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }



}