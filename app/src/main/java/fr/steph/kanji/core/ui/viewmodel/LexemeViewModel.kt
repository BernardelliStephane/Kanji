package fr.steph.kanji.core.ui.viewmodel

const val INSERTION_FAILURE = -1L
const val UPDATE_FAILURE = 0

class LexemeViewModel {
    sealed class ValidationEvent {
        data class Failure(val failureMessage: Int) : ValidationEvent()
        data object Success : ValidationEvent()
    }
}