package fr.steph.kanji.core.data.model

import java.io.Serializable

data class Variant(
    val priorities: List<String>,
    val pronounced: String,
    val written: String
) : Serializable