package fr.steph.kanji.data.model

abstract class Lexeme(
    open val id: Int,
    open val characters: String,
    open val romaji: String,
    open val meaning: String,
    open val timestamp: Long = 0,
)