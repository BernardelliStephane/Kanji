package fr.steph.kanji.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Kanji (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val character: Char,
    val kana: String,
    val romaji: String,
    val translation: String,
    val childrenKanjis: ArrayList<Kanji>
)