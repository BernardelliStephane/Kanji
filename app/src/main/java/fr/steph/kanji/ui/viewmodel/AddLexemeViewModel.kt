package fr.steph.kanji.ui.viewmodel

import fr.steph.kanji.data.model.Lexeme
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.data.utils.enumeration.LexemeType

class AddLexemeViewModel(repo: LexemeRepository) : LexemeViewModel(repo) {

    var id = 0
    var characters: String = ""
    var romaji: String = ""
    var meaning: String = ""

    fun performValidation() {
        // TODO Verify entries before upserting lexeme
        val lexeme = Lexeme(id, LexemeType.KANA, characters, romaji, meaning)
        upsertLexeme(lexeme)
    }
}