package fr.steph.kanji.ui.core.model

import fr.steph.kanji.ui.core.viewmodel.LessonViewModel.ValidationEvent

data class LessonInsertResult(
    val numberErrorRes: Int? = null,
    val labelErrorRes: Int? = null,
    val insertionResult: ValidationEvent? = null
)