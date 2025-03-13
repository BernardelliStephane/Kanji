package fr.steph.kanji.ui.uistate

import fr.steph.kanji.data.model.Lexeme
import fr.steph.kanji.data.utils.enumeration.LexemeType
import fr.steph.kanji.utils.Moji.mojiDetector

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
            meaning = meaning,
        )
    }
}