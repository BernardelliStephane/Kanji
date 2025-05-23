package fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate

import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.domain.enumeration.LexemeType
import fr.steph.kanji.core.util.extension.capitalized
import fr.steph.kanji.core.util.extension.hasKanji
import fr.steph.kanji.core.util.extension.isLoneKanji

data class AddLexemeState(
    val lessonNumber: Long = 0,
    val lessonError: Boolean = false,
    val characters: String = "",
    val charactersErrorRes: Int? = null,
    val alternativeWritings: String = "",
    val romaji: String = "",
    val romajiErrorRes: Int? = null,
    val meaning: String = "",
    val meaningErrorRes: Int? = null,
    val onyomi: String = "",
    val onyomiRomaji: String = "",
    val kunyomi: String = "",
    val kunyomiRomaji: String = "",
    val nameReadings: String = "",
    val nameReadingsRomaji: String = "",
    val gradeTaught: String = "",
    val jlptLevel: String = "",
    val useFrequencyIndicator: String = "",
    val lastFetch: String? = null,
    val lastFetchMeaning: String = "",
    val lastFetchRomaji: String = "",
    val isFetching: Boolean = false,
    val isUpdating: Boolean = false,
    val isSubmitting: Boolean = false,
) {
    val isCharactersContainingKanji: Boolean
        get() = characters.hasKanji()
    val isCharactersLoneKanji: Boolean
        get() = characters.isLoneKanji()
    val isCharactersFetched: Boolean
        get() = characters == lastFetch

    fun toLexeme(id: Long = 0, creationDate: Long = System.currentTimeMillis()): Lexeme {
        val lexemeType = when {
            isCharactersLoneKanji -> LexemeType.KANJI
            isCharactersContainingKanji -> LexemeType.COMPOUND
            else -> LexemeType.KANA
        }

        val alternativeWritings =
            if (lexemeType == LexemeType.COMPOUND)
                alternativeWritings else ""

        val romaji =
            if (lexemeType == LexemeType.KANJI)
                "$onyomiRomaji, $kunyomiRomaji".split(", ").distinct().joinToString(", ")
            else romaji

        return Lexeme(
            id = id,
            type = lexemeType,
            lessonNumber = lessonNumber,
            characters = characters,
            alternativeWritings = alternativeWritings,
            romaji = romaji,
            meaning = meaning.capitalized(),
            creationDate = creationDate
        )
    }
}