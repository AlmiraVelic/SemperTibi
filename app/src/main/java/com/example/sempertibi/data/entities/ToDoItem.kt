package com.example.sempertibi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "toDoItems")
data class ToDoItem(
    @PrimaryKey(autoGenerate = true)
    val todo_id: Int = 0,
    val user_id: Int,
    var title: String,
    var description: String,
    val isCompleted: Boolean = false
)
