package fr.steph.kanji.feature_dictionary.domain.model

import fr.steph.kanji.core.ui.util.LexemeResource

data class LexemeUpsertResult(
    val lessonError: Boolean = false,
    val charactersErrorRes: Int? = null,
    val romajiErrorRes: Int? = null,
    val meaningErrorRes: Int? = null,
    val upsertionResult: LexemeResource? = null
)