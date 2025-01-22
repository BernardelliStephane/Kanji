package fr.steph.kanji.ui.form_presentation.validation

import fr.steph.kanji.R

class ValidateField {
    companion object {
        fun execute(value: String): ValidationResult {
            if (value.isBlank()) {
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.mandatory_field_error
                )
            }
            return ValidationResult(successful = true)
        }
    }
}