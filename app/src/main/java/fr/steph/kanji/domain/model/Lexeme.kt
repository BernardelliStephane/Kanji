package fr.steph.kanji.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.steph.kanji.domain.enumeration.LexemeType
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.uistate.AddLexemeFormState
import fr.steph.kanji.util.extension.isLoneKanji

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
    fun toAddLexemeFormState(): AddLexemeFormState =
        AddLexemeFormState(
            lessonNumber = lessonNumber,
            characters = characters,
            isCharactersLoneKanji = characters.isLoneKanji(),
            romaji = romaji,
            meaning = meaning
        )
}
