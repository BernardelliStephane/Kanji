package fr.steph.kanji.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lesson(
    @PrimaryKey
    val number: Long,
    val label: String,
)