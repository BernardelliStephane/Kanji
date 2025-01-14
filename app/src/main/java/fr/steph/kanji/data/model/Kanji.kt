package fr.steph.kanji.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Kanji(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    override val characters: String,
    override val meaning: String,
    val onyomi: String,
    val onyomiRomaji: String,
    val kunyomi: String,
    val kunyomiRomaji: String,
    override val romaji: String = onyomiRomaji,
    override val timestamp: Long,
    //TODO Ordre des traits
) : Lexeme(id, characters, romaji, meaning)