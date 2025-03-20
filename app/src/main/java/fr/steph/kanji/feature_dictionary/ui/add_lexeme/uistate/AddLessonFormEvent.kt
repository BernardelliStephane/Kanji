package fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate

sealed class AddLessonFormEvent {
    data class NumberChanged(val number: String) : AddLessonFormEvent()
    data class LabelChanged(val label: String) : AddLessonFormEvent()
}