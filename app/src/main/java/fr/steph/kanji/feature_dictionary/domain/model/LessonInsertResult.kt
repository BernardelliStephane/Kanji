package fr.steph.kanji.feature_dictionary.domain.model

import fr.steph.kanji.core.ui.util.LessonResource

data class LessonInsertResult(
    val numberErrorRes: Int? = null,
    val labelErrorRes: Int? = null,
    val insertionResult: LessonResource? = null
)