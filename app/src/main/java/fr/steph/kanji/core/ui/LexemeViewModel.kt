package fr.steph.kanji.core.ui

class LexemeViewModel {
    sealed class ValidationEvent {
        data class Failure(val failureMessage: Int) : ValidationEvent()
        data object Success : ValidationEvent()
    }
}