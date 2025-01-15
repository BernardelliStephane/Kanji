package fr.steph.kanji.data.model

import fr.steph.kanji.data.utils.enumeration.LexemeType.KANA

data class Kana(
    override val id: Int = 0,
    override val characters: String,
    override val romaji: String,
    override val meaning: String,
) : Lexeme(
    id = id,
    type = KANA,
    characters = characters,
    romaji = romaji,
    meaning = meaning,
)