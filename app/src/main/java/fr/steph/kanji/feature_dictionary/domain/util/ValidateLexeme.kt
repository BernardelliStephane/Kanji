package fr.steph.kanji.feature_dictionary.domain.util

import fr.steph.kanji.R
import fr.steph.kanji.core.util.DEFAULT_LESSON_ID
import fr.steph.kanji.core.util.extension.hasKanji
import fr.steph.kanji.core.util.extension.isOnlyJapaneseCharacters
import fr.steph.kanji.core.util.extension.isOnlyRomanCharacters
import fr.steph.kanji.core.util.extension.isOnlyRomanLetters

object ValidateLexeme {

    fun validateLesson(lessonNumber: Long): ValidationResult {
        return ValidationResult(successful = lessonNumber != DEFAULT_LESSON_ID)
    }

    fun validateCharacters(characters: String, characterFetched: Boolean): ValidationResult {
        if (characters.isBlank())
            return ValidationResult(
                successful = false,
                error = R.string.mandatory_field_error
            )

        if (characters.contains(' '))
            return ValidationResult(
                successful = false,
                error = R.string.no_spaces_allowed_error
            )

        if (!characters.isOnlyJapaneseCharacters())
            return ValidationResult(
                successful = false,
                error = R.string.japanese_characters_only_filling_error
            )

        if (characters.hasKanji() && !characterFetched)
            return ValidationResult(
                successful = false,
                error = R.string.characters_not_fetched_error
            )

        return ValidationResult(successful = true)
    }

    fun validateRomaji(romaji: String): ValidationResult {
        if (romaji.isBlank())
            return ValidationResult(
                successful = false,
                error = R.string.mandatory_field_error
            )

        if (!romaji.isOnlyRomanLetters())
            return ValidationResult(
                successful = false,
                error = R.string.roman_letters_only_filling_error
            )

        return ValidationResult(successful = true)
    }

    fun validateMeaning(meaning: String): ValidationResult {
        if (meaning.isBlank())
            return ValidationResult(
                successful = false,
                error = R.string.mandatory_field_error
            )

        if (!meaning.isOnlyRomanCharacters())
            return ValidationResult(
                successful = false,
                error = R.string.roman_letters_only_filling_error
            )

        return ValidationResult(successful = true)
    }
}