package fr.steph.kanji.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lesson(
    @PrimaryKey(autoGenerate = false)
    val number: Long,
    val label: String,
)