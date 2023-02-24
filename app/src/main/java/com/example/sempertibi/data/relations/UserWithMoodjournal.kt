package com.example.sempertibi.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.sempertibi.data.entities.MoodJournal
import com.example.sempertibi.data.entities.User

data class UserWithMoodjournal(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "user_id"
    )
    val moodJournal: List<MoodJournal>,
)