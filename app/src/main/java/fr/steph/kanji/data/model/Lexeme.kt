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
    // TODO val childrenKanji: List<Kanji>? = emptyList(),
    // TODO Ordre des traits
) {
    companion object {
        fun buildLexemeFromFormState(uiState: AddLexemeFormState, id: Int): Lexeme {
            val lexemeType = when {
                uiState.isCharactersFetched -> LexemeType.KANJI
                mojiDetector.hasKanji(uiState.characters) -> LexemeType.COMPOUND
                else -> LexemeType.KANA
            }

            return Lexeme(
                id = id,
                type = lexemeType,
                characters = uiState.characters,
                romaji = if (lexemeType == LexemeType.KANJI)
                    uiState.onyomiRomaji else uiState.romaji,
                meaning = uiState.meaning
            )
        }
    }
}