package com.example.sempertibi

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class QuizDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    companion object {
        // below is the variable for database version
        // If you change the database schema, you must increment the database version.
        private val DATABASE_VERSION = 1

        // here we have defined variables for our database
        // below is variable for database name
        private val DATABASE_NAME = "test.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.QuestionsTable.TABLE_NAME + " (" +
                    DBContract.QuestionsTable.COLUMN_ID + " TEXT PRIMARY KEY," +
                    DBContract.QuestionsTable.COLUMN_QUESTION + " TEXT," +
                    DBContract.QuestionsTable.COLUMN_OPTION1 + " TEXT," +
                    DBContract.QuestionsTable.COLUMN_OPTION2 + " TEXT," +
                    DBContract.QuestionsTable.COLUMN_OPTION3 + " TEXT," +
                    DBContract.QuestionsTable.COLUMN_OPTION4 + " TEXT," +
                    DBContract.QuestionsTable.COLUMN_OPTION5 + " TEXT," +
                    DBContract.QuestionsTable.COLUMN_ANSWER_NR + " TEXT)"

        private val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.QuestionsTable.TABLE_NAME
    }

    fun open(): SQLiteDatabase {
        return writableDatabase
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    // This method is for adding data in our database
    @Throws(SQLiteConstraintException::class)
    fun addQuestion(question: QuestionModel): Boolean {
        // Gets the data repository in write mode
        // here we are creating a  writable variable of  our database as we want to
        // insert value in our database
        val db = writableDatabase

        // below we are creating a content values variable
        // Create a new map of values, where column names are the keys
        val values = ContentValues()

        // we are inserting our values in the form of key-value pair
        values.put(DBContract.QuestionsTable.COLUMN_QUESTION, question.question)
        values.put(DBContract.QuestionsTable.COLUMN_ID, question.id)
        values.put(DBContract.QuestionsTable.COLUMN_OPTION1, question.option1)
        values.put(DBContract.QuestionsTable.COLUMN_OPTION2, question.option2)
        values.put(DBContract.QuestionsTable.COLUMN_OPTION3, question.option3)
        values.put(DBContract.QuestionsTable.COLUMN_OPTION4, question.option4)
        values.put(DBContract.QuestionsTable.COLUMN_OPTION5, question.option5)
        values.put(DBContract.QuestionsTable.COLUMN_ANSWER_NR, question.answerNr)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.QuestionsTable.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteQuestion(userid: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.QuestionsTable.COLUMN_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(userid)
        // Issue SQL statement.
        db.delete(DBContract.QuestionsTable.TABLE_NAME, selection, selectionArgs)

        return true
    }

    @SuppressLint("Range")
    fun readQuestion(questionID: String): ArrayList<QuestionModel> {
        val questions = ArrayList<QuestionModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                "SELECT * FROM " + DBContract.QuestionsTable.TABLE_NAME + " WHERE " + DBContract.QuestionsTable.COLUMN_ID + "='" + questionID + "'",
                null
            )
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var question: String
        var answer1: String
        var answer2: String
        var answer3: String
        var answer4: String
        var answer5: String
        var answerNr: String

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                question =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_QUESTION))
                answer1 =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION1))
                answer2 =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION2))
                answer3 =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION3))
                answer4 =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION4))
                answer5 =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION5))
                answerNr =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_ANSWER_NR))

                questions.add(
                    QuestionModel(questionID, question,answer1,answer2,answer3,answer4,answer5,answerNr)
                )
                cursor.moveToNext()
            }
        }
        return questions
    }

    @SuppressLint("Range")
    fun readAllQuestions(): ArrayList<QuestionModel> {
        val questions = ArrayList<QuestionModel>()
        val db = writableDatabase
        val cursor: Cursor? = null
        try {
            db.rawQuery("SELECT * FROM " + DBContract.QuestionsTable.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var id: String
        var question: String
        var answer1: String
        var answer2: String
        var answer3: String
        var answer4: String
        var answer5: String
        var answerNr: String

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_ID))
                question =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_QUESTION))
                answer1 =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION1))
                answer2 =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION2))
                answer3 =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION3))
                answer4 =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION4))
                answer5 =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION5))
                answerNr =
                    cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_ANSWER_NR))

                questions.add(
                    QuestionModel(
                        id,
                        question,
                        answer1,
                        answer2,
                        answer3,
                        answer4,
                        answer5,
                        answerNr
                    )
                )
                cursor.moveToNext()
            }
        }
        return questions
    }

    // below method is to get all data from our database
    fun getName(): Cursor? {
        // here we are creating a readable variable of our database as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to read data from the database
        return db.rawQuery("SELECT * FROM " + DBContract.QuestionsTable.TABLE_NAME, null)
    }
}