package fr.steph.kanji.ui.viewmodel

import fr.steph.kanji.data.model.Kanji
import fr.steph.kanji.data.repository.LexemeRepository

class AddKanjiViewModel(repo: LexemeRepository) : KanjiViewModel(repo) {

    var id = 0
    var character: String = ""
    var kana: String = ""
    var romaji: String = ""
    var translation: String = ""

    fun performValidation() {
        // TODO Verify entries before upserting kanji
        val kanji = Kanji(id, character, kana, romaji, translation)
        upsertKanji(kanji)
    }
}