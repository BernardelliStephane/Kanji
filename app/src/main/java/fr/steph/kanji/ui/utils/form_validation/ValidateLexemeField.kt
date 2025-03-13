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

        fun validateCharacters(characters: String, loneKanji: Boolean, characterFetched: Boolean): ValidationResult {
            if (loneKanji)
                return validateLoneKanjiCharacters(characterFetched)

            if (characters.isBlank())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.mandatory_field_error
                )

            if (!characters.isOnlyJapaneseCharacters())
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.japanese_characters_only_filling_error
                )

            return ValidationResult(successful = true)
        }

        private fun validateLoneKanjiCharacters(characterFetched: Boolean): ValidationResult {
            if (!characterFetched)
                return ValidationResult(
                    successful = false,
                    errorMessageRes = R.string.kanji_not_fetched_error
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
                    errorMessageRes = R.string.roman_letters_only_filling_error
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
                    errorMessageRes = R.string.roman_letters_only_filling_error
                )

            return ValidationResult(successful = true)
        }
    }
}