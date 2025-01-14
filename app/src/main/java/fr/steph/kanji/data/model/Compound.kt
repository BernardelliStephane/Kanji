package fr.steph.kanji.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Compound(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    override val characters: String,
    override val meaning: String,
    override val romaji: String,
    override val timestamp: Long,
    // TODO val childrenKanji: List<Kanji>? = null,
) : Lexeme(id, characters, romaji, meaning)