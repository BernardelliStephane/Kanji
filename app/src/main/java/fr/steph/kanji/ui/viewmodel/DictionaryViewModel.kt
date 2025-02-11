package fr.steph.kanji.ui.viewmodel

import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.ui.uistate.DictionaryFragmentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DictionaryViewModel(repo: LexemeRepository) : LexemeViewModel(repo) {

    private val _uiState = MutableStateFlow(DictionaryFragmentState())
    val uiState = _uiState.asStateFlow()

    fun onSelectionChanged(selectionSize: Int) {
        if(selectionSize == uiState.value.itemsSelected) return
        _uiState.update { currentUiState ->
            val selectionMode = uiState.value.isSelectionMode || selectionSize != 0
            currentUiState.copy(itemsSelected = selectionSize, isSelectionMode = selectionMode)
        }
    }

    fun onSelectionCleared() {
        _uiState.update { currentUiState ->
            currentUiState.copy(itemsSelected = 0, isSelectionMode = false)
        }
    }
}