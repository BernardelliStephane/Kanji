package fr.steph.kanji.data.model

data class Kana(
    override val id: Int = 0,
    override val characters: String,
    override val meaning: String,
    val romaji: String,
) : Lexeme(id, characters, meaning)
