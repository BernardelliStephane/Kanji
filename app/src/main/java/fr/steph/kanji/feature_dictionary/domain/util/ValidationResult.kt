package fr.steph.kanji.feature_dictionary.domain.util

import androidx.annotation.StringRes

data class ValidationResult(
    val successful: Boolean,
    @StringRes val error: Int? = null
)