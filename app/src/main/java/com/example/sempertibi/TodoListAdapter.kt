package com.example.sempertibi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sempertibi.data.entities.ToDoItem

class TodoListAdapter(private val todoList: List<ToDoItem>) :
    RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.text_view_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.text_view_description)

        fun bind(todoItem: ToDoItem) {
            titleTextView.text = todoItem.title
            descriptionTextView.text = todoItem.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(todoList[position])
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun getItemAt(position: Int): ToDoItem {
        return todoList[position]
    }
}
