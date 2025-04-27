package fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate

import android.content.Context
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.domain.model.LexemeWithLesson

sealed class AddLexemeEvent {
    data class LessonAdded(val lesson: Lesson) : AddLexemeEvent()
    data class LessonChanged(val lessonNumber: Long) : AddLexemeEvent()
    data class CharactersChanged(val characters: String) : AddLexemeEvent()
    data class RomajiChanged(val romaji: String) : AddLexemeEvent()
    data class MeaningChanged(val meaning: String) : AddLexemeEvent()
    data class DataFetched(val data: Any) : AddLexemeEvent()
    data class Submit(val duplicateTranslationCallback: (LexemeWithLesson) -> Unit) : AddLexemeEvent()
    data class Fetch(val context: Context) : AddLexemeEvent()
}