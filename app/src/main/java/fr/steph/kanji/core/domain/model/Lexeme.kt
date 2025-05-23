package fr.steph.kanji.core.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import fr.steph.kanji.core.domain.enumeration.LexemeType
import fr.steph.kanji.core.util.extension.isLoneKanji
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLexemeState
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Lesson::class,
            parentColumns = arrayOf("number"),
            childColumns = arrayOf("lessonNumber")
        )
    ]
)
data class Lexeme(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val type: LexemeType,
    val lessonNumber: Long,
    val characters: String,
    val alternativeWritings: String,
    val romaji: String,
    val meaning: String,
    val creationDate: Long,
) : Parcelable {
    fun toAddLexemeFormState(): AddLexemeState =
        AddLexemeState(
            lessonNumber = lessonNumber,
            characters = characters,
            alternativeWritings = alternativeWritings,
            romaji = romaji,
            meaning = meaning
        )
}
