package fr.steph.kanji.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.steph.kanji.data.utils.enumeration.LexemeType
import fr.steph.kanji.ui.form_presentation.AddLexemeFormState
import fr.steph.kanji.utils.Moji.mojiDetector

@Entity
data class Lexeme(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: LexemeType,
    val characters: String,
    val romaji: String,
    val meaning: String,
    val unicode: String?,
) {
    companion object {
        fun buildLexemeFromFormState(id: Int, uiState: AddLexemeFormState, unicode: String?): Lexeme {
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