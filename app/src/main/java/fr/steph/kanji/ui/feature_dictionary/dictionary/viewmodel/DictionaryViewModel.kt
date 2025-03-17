package fr.steph.kanji.ui.feature_dictionary.dictionary.viewmodel

import androidx.lifecycle.viewModelScope
import fr.steph.kanji.data.repository.LessonRepository
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.ui.core.viewmodel.LexemeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DictionaryViewModel(
    lessonRepo: LessonRepository,
    lexemeRepo: LexemeRepository,
) : LexemeViewModel(lessonRepo, lexemeRepo) {

    private val _selectionSize = MutableStateFlow(0)
    val selectionSize = _selectionSize.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    val allSelected = _selectionSize
        .map { it == (lexemes.value?.size ?: 0) }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun onSelectionChanged(selectionSize: Int) {
        _isSelectionMode.update { it || selectionSize > 0 }
        _selectionSize.update { selectionSize }
    }

    fun disableSelectionMode() {
        _isSelectionMode.update { false }
    }
}