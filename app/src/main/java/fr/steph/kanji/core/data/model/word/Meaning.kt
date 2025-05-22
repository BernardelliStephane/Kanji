package fr.steph.kanji.core.data.model.word

import java.io.Serializable

data class Meaning(
    val glosses: List<String>
) : Serializable