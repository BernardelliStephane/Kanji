package fr.steph.kanji.ui.form_presentation

data class AddLexemeFormState(
    var characters: String = "",
    var isCharactersLoneKanji: Boolean = false,
    var romaji: String = "",
    var meaning: String = "",
)