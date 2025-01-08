package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.data.model.Kanji
import fr.steph.kanji.data.repository.KanjiRepository
import kotlinx.coroutines.launch

class AddKanjiViewModel(private val repo: KanjiRepository) : ViewModel() {
    var character: String = ""
    var kana: String = ""
    var romaji: String = ""
    var translation: String = ""

    fun upsertKanji(kanji: Kanji) = viewModelScope.launch {
        repo.upsertKanji(kanji)
    }

    fun performValidation() {
        // TODO Verify entries then upsert kanji
    }
}