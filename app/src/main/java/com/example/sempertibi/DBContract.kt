package com.example.sempertibi

import android.provider.BaseColumns

object DBContract {
    /* Inner class that defines the table contents */
    class QuestionsTable : BaseColumns {
        companion object {
            val TABLE_NAME = "quiz_questions"
            val COLUMN_ID = "quid"
            val COLUMN_QUESTION = "question"
            val COLUMN_OPTION1 = "option1"
            val COLUMN_OPTION2 = "option2"
            val COLUMN_OPTION3 = "option3"
            val COLUMN_OPTION4 = "option4"
            val COLUMN_OPTION5 = "option5"
            val COLUMN_ANSWER_NR = "answer_nr"
        }
    }
}