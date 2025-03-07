package fr.steph.kanji.ui.utils.form_validation

import fr.steph.kanji.R
import fr.steph.kanji.utils.extension.isOnlyJapaneseCharacters
import fr.steph.kanji.utils.extension.isOnlyRomanCharacters
import fr.steph.kanji.utils.extension.isOnlyRomanLetters

class ValidateLexemeField {
    companion object {
        fun validateLesson(lessonNumber: Long): ValidationResult {
            return ValidationResult(successful = lessonNumber != -1L)
        }

        fun validateCharacters(characters: String): ValidationResult {
            if (characters.isBlank())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.mandatory_field_error
                )

            if (!characters.isOnlyJapaneseCharacters())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.incorrect_characters_filling_error
                )

            return ValidationResult(successful = true)
        }

        fun validateRomaji(romaji: String): ValidationResult {
            if (romaji.isBlank())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.mandatory_field_error
                )

            if (!romaji.isOnlyRomanLetters())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.incorrect_romaji_filling_error
                )

            return ValidationResult(successful = true)
        }

        fun validateMeaning(meaning: String): ValidationResult {
            if (meaning.isBlank())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.mandatory_field_error
                )

            if (!meaning.isOnlyRomanCharacters())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.incorrect_meaning_filling_error
                )

            return ValidationResult(successful = true)
        }
    }

    class ValidationResult(val successful: Boolean, val errorMessageRes: Int? = null)
}