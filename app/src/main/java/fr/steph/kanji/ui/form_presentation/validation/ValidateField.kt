package fr.steph.kanji.ui.form_presentation.validation

import fr.steph.kanji.R

class ValidateField {
    companion object {
        fun execute(characters: String): ValidationResult {
            if (characters.isBlank()) {
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.mandatory_field_error
                )
            }
            return ValidationResult(successful = true)
        }
    }
}