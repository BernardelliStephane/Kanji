package fr.steph.kanji.ui.utils.form_validation

import androidx.core.text.isDigitsOnly
import fr.steph.kanji.R

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
                    errorMessageRes = R.string.already_existing_number_filling_error
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