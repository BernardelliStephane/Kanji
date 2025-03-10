package fr.steph.kanji.ui.uistate

sealed class AddLessonFormEvent {
    data class NumberChanged(val number: String) : AddLessonFormEvent()
    data class LabelChanged(val label: String) : AddLessonFormEvent()
    data class InsertionFailure(val failureMessageRes: Int) : AddLessonFormEvent()
}