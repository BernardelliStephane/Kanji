package fr.steph.kanji.core.data.model.jisho

import java.io.Serializable

data class Writing(
    val reading: String,
    val word: String
) : Serializable