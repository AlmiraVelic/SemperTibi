package com.example.sempertibi

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.ToDoItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToDoList : AppCompatActivity() {

    private lateinit var adapter: TodoListAdapter
    private lateinit var buttonAddTodo: Button
    private lateinit var todoTitleInput: TextInputEditText
    private lateinit var todoDescriptionInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)

        todoTitleInput = findViewById(R.id.todoTitleInput)
        todoDescriptionInput = findViewById(R.id.todoDescriptionInput)
        buttonAddTodo = findViewById(R.id.button_add_todo)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_todo_items)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TodoListAdapter(emptyList())
        recyclerView.adapter = adapter

        // initializing the objects
        val dao = UserDatabase.getInstance(this).userDao()

        lifecycleScope.launch {
            val userToDoList =
                withContext(Dispatchers.IO) { dao.getAllTodoItems(GlobalData.userID!!) }
            userToDoList.observe(this@ToDoList) { todoItems ->
                adapter = TodoListAdapter(todoItems)
                recyclerView.adapter = adapter
            }

            // Add swipe-to-delete functionality
            val itemTouchHelperCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder,
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val item = adapter.getItemAt(position)

                    when (direction) {
                        ItemTouchHelper.LEFT -> { // Swiping to the left (delete)
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) { dao.deleteTodoItem(item) }
                            }
                        }
                        ItemTouchHelper.RIGHT -> { // Swiping to the right (edit)
                            val intent = Intent(this@ToDoList, EditTodo::class.java)
                            intent.putExtra("todoItem", item.todo_id)
                            startActivity(intent)
                        }
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean,
                ) {
                    val itemView = viewHolder.itemView
                    val backgroundCornerOffset =
                        20 // So background is behind the rounded corners of itemView
                    val iconDelete =
                        ContextCompat.getDrawable(this@ToDoList, R.drawable.ic_delete)
                    val iconEdit = ContextCompat.getDrawable(this@ToDoList, R.drawable.ic_edit)
                    val iconMargin = (itemView.height - iconDelete!!.intrinsicHeight) / 2
                    val iconTop = itemView.top + (itemView.height - iconDelete.intrinsicHeight) / 2
                    val iconBottom = iconTop + iconDelete.intrinsicHeight

                    when {
                        dX > 0 -> { // Swiping to the right
                            val iconLeft = itemView.left + iconMargin
                            val iconRight = itemView.left + iconMargin + iconEdit!!.intrinsicWidth
                            iconEdit.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                            val background = ColorDrawable(Color.parseColor("#FF4CAF50"))
                            background.setBounds(
                                itemView.left,
                                itemView.top,
                                itemView.left + dX.toInt() + backgroundCornerOffset,
                                itemView.bottom
                            )

                            background.draw(c)
                            iconEdit.draw(c)

                        }
                        dX < 0 -> { // Swiping to the left
                            val iconLeft = itemView.right - iconMargin - iconDelete.intrinsicWidth
                            val iconRight = itemView.right - iconMargin
                            iconDelete.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                            val background = ColorDrawable(Color.parseColor("#FFF1776E"))
                            background.setBounds(
                                itemView.right + dX.toInt() - backgroundCornerOffset,
                                itemView.top,
                                itemView.right,
                                itemView.bottom
                            )

                            background.draw(c)
                            iconDelete.draw(c)
                        }
                        else -> { // View is unswiped
                            val background = ColorDrawable(Color.parseColor("#FFF1776E"))
                            background.setBounds(0, 0, 0, 0)
                            background.draw(c)
                        }
                    }

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)

            buttonAddTodo.setOnClickListener {
                if (validateInput()) {
                    val title = todoTitleInput.text.toString()
                    val description = todoDescriptionInput.text.toString()
                    val todoItem = ToDoItem(
                        user_id = GlobalData.userID!!,
                        title = title,
                        description = description
                    )
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) { dao.insertTodoItem(todoItem) }
                    }
                    todoTitleInput.text = null
                    todoDescriptionInput.text = null
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
            Toast.makeText(this,"Please enter at least a title",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}