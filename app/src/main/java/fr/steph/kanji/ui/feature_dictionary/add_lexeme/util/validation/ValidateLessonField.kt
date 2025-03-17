package fr.steph.kanji.ui.feature_dictionary.add_lexeme.util.validation

import androidx.core.text.isDigitsOnly
import fr.steph.kanji.R
import fr.steph.kanji.ui.core.util.ValidationResult

class ValidateLessonField {
    companion object {
        fun validateNumber(number: String, lessonNumbers: List<Long>): ValidationResult {
            if (number.isBlank())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.mandatory_field_error
                )

            if (!number.isDigitsOnly())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.digits_only_filling_error
                )

            if (number.toLong() in lessonNumbers)
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.duplicate_lesson_error
                )

            return ValidationResult(successful = true)
        }

        fun validateLabel(label: String): ValidationResult {
            if (label.isBlank())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.mandatory_field_error
                )

            return ValidationResult(successful = true)
        }
    }
}