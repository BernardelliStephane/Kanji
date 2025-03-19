package fr.steph.kanji.ui.core.model

import fr.steph.kanji.ui.feature_dictionary.dictionary.viewmodel.FilterLexemesViewModel.ValidationEvent

data class LessonInsertResult(
    val numberErrorRes: Int? = null,
    val labelErrorRes: Int? = null,
    val insertionResult: ValidationEvent? = null
)