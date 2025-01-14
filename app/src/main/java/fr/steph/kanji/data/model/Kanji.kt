package fr.steph.kanji.data.model

data class Kanji(
    override val id: Int = 0,
    override val characters: String,
    override val meaning: String,
    val onyomi: String,
    val onyomiRomaji: String,
    val kunyomi: String,
    val kunyomiRomaji: String,
    //TODO Ordre des traits
) : Lexeme(id, characters, meaning)