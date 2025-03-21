package fr.steph.kanji.core.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.steph.kanji.core.domain.enumeration.LexemeType
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLexemeState
import fr.steph.kanji.core.util.extension.isLoneKanji

@Entity
data class Lexeme(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val type: LexemeType,
    val lessonNumber: Long,
    val characters: String,
    val romaji: String,
    val meaning: String,
    val additionDate: Long = System.currentTimeMillis(),
) {
    fun toAddLexemeFormState(): AddLexemeState =
        AddLexemeState(
            lessonNumber = lessonNumber,
            characters = characters,
            isCharactersLoneKanji = characters.isLoneKanji(),
            romaji = romaji,
            meaning = meaning
        )
}
