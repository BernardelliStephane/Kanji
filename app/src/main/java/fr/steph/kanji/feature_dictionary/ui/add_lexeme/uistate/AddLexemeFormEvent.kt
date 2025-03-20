package fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate

import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.data.model.ApiKanji

sealed class AddLexemeFormEvent {
    data class LessonChanged(val lessonNumber: Long) : AddLexemeFormEvent()
    data class CharactersChanged(val characters: String) : AddLexemeFormEvent()
    data class RomajiChanged(val romaji: String) : AddLexemeFormEvent()
    data class MeaningChanged(val meaning: String) : AddLexemeFormEvent()
    data class KanjiFetched(val kanji: ApiKanji) : AddLexemeFormEvent()
    data class Submit(val duplicateTranslationCallback: (Lexeme) -> Unit) : AddLexemeFormEvent()
    data object Search : AddLexemeFormEvent()
}