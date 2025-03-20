package fr.steph.kanji.feature_dictionary.domain.model

import fr.steph.kanji.feature_dictionary.ui.dictionary.viewmodel.FilterLexemesViewModel.ValidationEvent

data class LessonInsertResult(
    val numberErrorRes: Int? = null,
    val labelErrorRes: Int? = null,
    val insertionResult: ValidationEvent? = null
)