package fr.steph.kanji.core.data.model

data class Word(
    val meanings: List<Meaning>,
    val variants: List<Variant>
)