package fr.steph.kanji.feature_dictionary.ui.add_lexeme.util.validation

import androidx.annotation.StringRes

class ValidationResult(
    val successful: Boolean,
    @StringRes val error: Int? = null
)