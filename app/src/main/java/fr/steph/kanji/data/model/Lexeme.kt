package fr.steph.kanji.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.steph.kanji.data.utils.enumeration.LexemeType
import fr.steph.kanji.network.model.ApiKanji
import fr.steph.kanji.ui.uistate.AddLexemeFormState
import fr.steph.kanji.utils.extension.kanaToRomaji

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
)
