package fr.steph.kanji.feature_dictionary.domain.util

import androidx.core.text.isDigitsOnly
import fr.steph.kanji.R

object ValidateLesson {

    fun validateNumber(number: String, lessonNumbers: List<Long>): ValidationResult {
        if (number.isBlank())
            return ValidationResult(
                successful = false,
                error = R.string.mandatory_field_error
            )

        if (!number.isDigitsOnly())
            return ValidationResult(
                successful = false,
                error = R.string.digits_only_filling_error
            )

        if (number.toLong() in lessonNumbers)
            return ValidationResult(
                successful = false,
                error = R.string.duplicate_lesson_error
            )

        return ValidationResult(successful = true)
    }

    fun validateLabel(label: String): ValidationResult {
        if (label.isBlank())
            return ValidationResult(
                successful = false,
                error = R.string.mandatory_field_error
            )

        return ValidationResult(successful = true)
    }
}