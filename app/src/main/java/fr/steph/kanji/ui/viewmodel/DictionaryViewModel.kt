package fr.steph.kanji.ui.viewmodel

import fr.steph.kanji.data.repository.LexemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DictionaryViewModel(repo: LexemeRepository) : LexemeViewModel(repo) {
    private val _selectionSize = MutableStateFlow(0)
    val selectionSize = _selectionSize.asStateFlow()

    fun onSelectionChanged(selectionSize: Int) {
        _selectionSize.update { selectionSize }
    }
}