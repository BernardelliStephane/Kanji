package fr.steph.kanji.data.model

import fr.steph.kanji.data.utils.enumeration.LexemeType.COMPOUND

class Compound (
    override val id: Int = 0,
    override val characters: String,
    override val romaji: String,
    override val meaning: String,
    // TODO val childrenKanji: List<Kanji>? = null,
) : Lexeme(
    id = id,
    type = COMPOUND,
    characters = characters,
    romaji = romaji,
    meaning = meaning,
)