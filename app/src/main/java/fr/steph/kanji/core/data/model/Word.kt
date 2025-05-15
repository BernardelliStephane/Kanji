package fr.steph.kanji.core.data.model

import java.io.Serializable

data class Word(
    val meanings: List<Meaning>,
    val variants: List<Variant>
) : Serializable