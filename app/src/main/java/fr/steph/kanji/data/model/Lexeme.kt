package fr.steph.kanji.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.steph.kanji.data.utils.enumeration.LexemeType
import fr.steph.kanji.ui.uistate.AddLexemeFormState
import fr.steph.kanji.utils.Moji.mojiDetector

@Entity
data class Lexeme(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val type: LexemeType,
    val lessonNumber: Long = 0,
    val characters: String,
    val romaji: String,
    val meaning: String,
    val unicode: String?,
) {
    companion object {
        fun buildLexemeFromFormState(id: Long, uiState: AddLexemeFormState, unicode: String?): Lexeme {
            val lexemeType = when {
                uiState.isCharactersFetched -> LexemeType.KANJI
                mojiDetector.hasKanji(uiState.characters) -> LexemeType.COMPOUND
                else -> LexemeType.KANA
            }

            val romaji = if (lexemeType == LexemeType.KANJI)
                uiState.onyomiRomaji else uiState.romaji

            return Lexeme(
                id = id,
                type = lexemeType,
                characters = uiState.characters,
                romaji = romaji,
                meaning = uiState.meaning,
                unicode = unicode
            )
        }
    }
}