package com.example.sempertibi.data

import androidx.room.TypeConverter
import java.util.*

/*
We use in the entities Date type for some attributes.
The database does not support this type, so we have to convert it to a timestamp, like 1647375406045,
and when we read the value from the database, we will convert the timestamp back to Date type.
Every time we interact with the database, the converter will do the conversion automatically.
The magic of Room ✨
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}