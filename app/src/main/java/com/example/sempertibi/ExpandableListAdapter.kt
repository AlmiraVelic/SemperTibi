package com.example.sempertibi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class ExpandableListAdapter(private val context: Context, private val faqList: List<FAQ>) :
    BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return faqList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getGroup(groupPosition: Int): Any {
        return faqList[groupPosition].question
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return faqList[groupPosition].answer
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }
/*
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {

        var convertView = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.list_questions_stresstracker, null)
        }
        val title = convertView!!.findViewById<TextView>(R.id.listTitle)
        title.text = faqList[groupPosition].question
        return convertView
    }

 */

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_questions_stresstracker, parent, false)

        val groupText = view.findViewById<TextView>(R.id.listTitle)
        groupText.text = faqList[groupPosition].question

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        val childView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_answers_stresstracker, parent, false)

        val answerTextView = childView.findViewById<TextView>(R.id.expandedListItem)

        // Set the text of the answer TextView
        val answer = faqList[groupPosition].answer
        answerTextView.text = answer

        return childView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}
