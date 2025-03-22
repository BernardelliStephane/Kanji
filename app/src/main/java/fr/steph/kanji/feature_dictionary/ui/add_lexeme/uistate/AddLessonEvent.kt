package fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate

sealed class AddLessonEvent {
    data class NumberChanged(val number: String) : AddLessonEvent()
    data class LabelChanged(val label: String) : AddLessonEvent()
    data object Submit : AddLessonEvent()
}