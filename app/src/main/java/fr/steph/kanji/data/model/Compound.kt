package fr.steph.kanji.data.model

data class Compound(
    override val id: Int,
    override val characters: String,
    override val meaning: String,
    val romaji: String,
    val childrenKanji: List<Kanji>? = null,
) : Lexeme(id, characters, meaning)
