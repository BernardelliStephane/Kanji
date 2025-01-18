package fr.steph.kanji.ui.form_presentation

sealed class AddLexemeFormEvent {
    data class CharactersChanged(val characters: String) : AddLexemeFormEvent()
    data class RomajiChanged(val romaji: String) : AddLexemeFormEvent()
    data class MeaningChanged(val meaning: String) : AddLexemeFormEvent()
}