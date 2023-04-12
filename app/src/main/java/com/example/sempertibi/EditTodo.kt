package com.example.sempertibi

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditTodo : AppCompatActivity() {

    private lateinit var buttonSaveTodo: Button
    private lateinit var todoTitleInput: TextInputEditText
    private lateinit var todoDescriptionInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_todo)

        todoTitleInput = findViewById(R.id.todoTitleInput)
        todoDescriptionInput = findViewById(R.id.todoDescriptionInput)
        buttonSaveTodo = findViewById(R.id.button_save_todo)

        // initializing the objects
        val dao = UserDatabase.getInstance(this).userDao()

        lifecycleScope.launch {
            val extras = intent.extras
            val itemId = extras!!.getInt("todoItem")

            val todoItem = withContext(Dispatchers.IO) {
                dao.getTodoItemByID(itemId)
            }
            todoTitleInput.setText(todoItem.title)
            todoDescriptionInput.setText(todoItem.description)

            buttonSaveTodo.setOnClickListener {
                if (validateInput()) {
                    val title = todoTitleInput.text.toString()
                    val description = todoDescriptionInput.text.toString()
                    todoItem.title = title
                    todoItem.description = description
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) { dao.updateTodoItem(todoItem) }
                        finish()
                    }
                    Toast.makeText(this@EditTodo, "Changes saved", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@EditTodo, ToDoList::class.java))
                }
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuDashboard -> {
                    val intent = Intent(this, Dashboard::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuMoodJournal -> {
                    val intent = Intent(this, MoodJournalOverview::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuStressTracker -> {
                    val intent = Intent(this, StressTrackerOverview::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuToDoList -> {
                    val intent = Intent(this, ToDoList::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuSignOut -> {
                    AlertDialog.Builder(this).setTitle("Sign Out")
                        .setMessage("Do you really want to sign out?")
                        .setPositiveButton("Yes") { _, _ ->
                            // Handle sign out here
                            val myApplication = applicationContext as MyApplication
                            myApplication.clearGlobalData()
                            val packageManager = applicationContext.packageManager
                            val intent =
                                packageManager.getLaunchIntentForPackage(applicationContext.packageName)
                            val componentName = intent!!.component
                            val mainIntent = Intent.makeRestartActivityTask(componentName)
                            applicationContext.startActivity(mainIntent)
                        }
                        .setNegativeButton("No") { _, _ ->
                            val intent = Intent(this, Dashboard::class.java)
                            startActivity(intent)
                        }
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun validateInput(): Boolean {
        if (todoTitleInput.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter at least a title", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}