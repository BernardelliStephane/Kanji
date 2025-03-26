package fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate

import fr.steph.kanji.core.data.model.ApiKanji
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.domain.model.LexemeWithLesson

sealed class AddLexemeEvent {
    data class LessonAdded(val lesson: Lesson) : AddLexemeEvent()
    data class LessonChanged(val lessonNumber: Long) : AddLexemeEvent()
    data class CharactersChanged(val characters: String) : AddLexemeEvent()
    data class RomajiChanged(val romaji: String) : AddLexemeEvent()
    data class MeaningChanged(val meaning: String) : AddLexemeEvent()
    data class KanjiFetched(val kanji: ApiKanji) : AddLexemeEvent()
    data class Submit(val duplicateTranslationCallback: (LexemeWithLesson) -> Unit) : AddLexemeEvent()
    data object Fetch : AddLexemeEvent()
}