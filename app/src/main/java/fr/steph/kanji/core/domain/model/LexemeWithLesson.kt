package fr.steph.kanji.core.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class LexemeWithLesson(
    @Embedded val lexeme: Lexeme,

    @Relation(
        parentColumn = "lessonNumber",
        entityColumn = "number"
    )
    val lesson: Lesson
)