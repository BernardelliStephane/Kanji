package fr.steph.kanji.data.model

import fr.steph.kanji.data.utils.enumeration.LexemeType.KANJI

data class Kanji(
    override val id: Int = 0,
    override val characters: String,
    override val onyomi: String,
    override val onyomiRomaji: String,
    override val kunyomi: String,
    override val kunyomiRomaji: String,
    override val meaning: String,
    //TODO Ordre des traits
) : Lexeme(
    id = id,
    type = KANJI,
    characters = characters,
    romaji = onyomiRomaji,
    onyomi = onyomi,
    onyomiRomaji = onyomiRomaji,
    kunyomi = kunyomi,
    kunyomiRomaji = kunyomiRomaji,
    meaning = meaning,
)