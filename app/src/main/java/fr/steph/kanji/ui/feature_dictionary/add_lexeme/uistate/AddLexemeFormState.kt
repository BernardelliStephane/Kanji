package fr.steph.kanji.ui.feature_dictionary.add_lexeme.uistate

import fr.steph.kanji.domain.model.Lexeme
import fr.steph.kanji.domain.enumeration.LexemeType
import fr.steph.kanji.util.Moji.mojiDetector
import fr.steph.kanji.util.extension.capitalized

data class AddLexemeFormState(
    var lessonNumber: Long = 0,
    var lessonError: Boolean = false,
    var characters: String = "",
    var charactersErrorRes: Int? = null,
    var isCharactersLoneKanji: Boolean = false,
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
    var lastFetchedKanji: String? = null,
    var isCharactersFetched: Boolean = false,
    var isUpdating: Boolean = false,
    var isSubmitting: Boolean = false,
) {
    fun toLexeme(id: Long): Lexeme {
        val lexemeType = when {
            isCharactersFetched -> LexemeType.KANJI
            mojiDetector.hasKanji(characters) -> LexemeType.COMPOUND
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
        )
    }
}