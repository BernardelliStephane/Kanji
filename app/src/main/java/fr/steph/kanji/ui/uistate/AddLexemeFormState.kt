package fr.steph.kanji.ui.uistate

data class AddLexemeFormState(
    var characters: String = "",
    var charactersErrorRes: Int? = null,
    var isCharactersLoneKanji: Boolean = false,
    var romaji: String = "",
    var romajiErrorRes: Int? = null,
    var meaning: String = "",
    var meaningErrorRes: Int? = null,
    var lastFetchedKanjiMeaning: String = "",
    var onyomi: String = "",
    var onyomiRomaji: String = "",
    var kunyomi: String = "",
    var kunyomiRomaji: String = "",
    var nameReadings: String = "",
    var nameReadingsRomaji: String = "",
    var gradeTaught: String = "",
    var jlptLevel: String = "",
    var useFrequencyIndicator: String = "",
    var lastFetchedKanji: String? = null,
    var isCharactersFetched: Boolean = false,
    var isSubmitting: Boolean = false,
)