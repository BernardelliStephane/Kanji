package fr.steph.kanji.ui.form_presentation

data class AddLexemeFormState(
    var characters: String = "",
    var isCharactersLoneKanji: Boolean = false,
    var romaji: String = "",
    var meaning: String = "",
    var onyomi: String = "",
    var onyomiRomaji: String = "",
    var kunyomi: String = "",
    var kunyomiRomaji: String = "",
    var lastFetchedKanji: String? = null,
    var isCharactersFetched: Boolean = false,
)