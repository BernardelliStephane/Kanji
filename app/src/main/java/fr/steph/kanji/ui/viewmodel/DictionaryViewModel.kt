package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.viewModelScope
import fr.steph.kanji.data.repository.LexemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DictionaryViewModel(repo: LexemeRepository) : LexemeViewModel(repo) {
    private val _selectionSize = MutableStateFlow(0)
    val selectionSize = _selectionSize.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    val allSelected = _selectionSize
        .map { it == (lexemes.value?.size ?: 0) }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun onSelectionChanged(selectionSize: Int) {
        _selectionSize.update { selectionSize }
        _isSelectionMode.update { selectionSize > 0 }
    }
}