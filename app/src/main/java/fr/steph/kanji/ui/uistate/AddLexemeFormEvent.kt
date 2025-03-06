package fr.steph.kanji.ui.uistate

import fr.steph.kanji.network.model.ApiKanji

sealed class AddLexemeFormEvent {
    data class LessonChanged(val lessonNumber: Long) : AddLexemeFormEvent()
    data class CharactersChanged(val characters: String) : AddLexemeFormEvent()
    data class RomajiChanged(val romaji: String) : AddLexemeFormEvent()
    data class MeaningChanged(val meaning: String) : AddLexemeFormEvent()
    data class KanjiFetched(val kanji: ApiKanji) : AddLexemeFormEvent()
}