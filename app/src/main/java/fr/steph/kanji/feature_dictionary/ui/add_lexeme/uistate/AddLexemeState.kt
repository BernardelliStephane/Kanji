package fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate

import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.domain.enumeration.LexemeType
import fr.steph.kanji.core.util.extension.capitalized

data class AddLexemeState(
    var lessonNumber: Long = 0,
    var lessonError: Boolean = false,
    var characters: String = "",
    var charactersErrorRes: Int? = null,
    var isCharactersContainingKanji: Boolean = false,
    var isCharactersLoneKanji: Boolean = false,
    var alternativeWritings: String = "",
    var romaji: String = "",
    var romajiErrorRes: Int? = null,
    var meaning: String = "",
    var meaningErrorRes: Int? = null,
    var lastFetchedKanjiMeaning: String = "",
    var onyomi: String = "",
    var onyomiRomaji: String = "",
    var kunyomi: String = "",
    var kunyomiRomaji: String = "",
    var nameReadings: String = "",
    var nameReadingsRomaji: String = "",
    var gradeTaught: String = "",
    var jlptLevel: String = "",
    var useFrequencyIndicator: String = "",
    var lastFetch: String? = null,
    var isFetching: Boolean = false,
    var isCharactersFetched: Boolean = false,
    var isUpdating: Boolean = false,
    var isSubmitting: Boolean = false,
) {
    fun toLexeme(id: Long = 0, creationDate: Long = System.currentTimeMillis()): Lexeme {
        val lexemeType = when {
            isCharactersLoneKanji -> LexemeType.KANJI
            isCharactersContainingKanji -> LexemeType.COMPOUND
            else -> LexemeType.KANA
        }

        val romaji = if (lexemeType == LexemeType.KANJI)
            onyomiRomaji else romaji

        return Lexeme(
            id = id,
            type = lexemeType,
            lessonNumber = lessonNumber,
            characters = characters,
            romaji = romaji,
            meaning = meaning.capitalized(),
            creationDate = creationDate
        )
    }
}