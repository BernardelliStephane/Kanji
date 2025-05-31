package fr.steph.kanji.core.domain.model

data class DatabaseModel(
    val lessons: List<Lesson>,
    val lexemes: List<Lexeme>
)