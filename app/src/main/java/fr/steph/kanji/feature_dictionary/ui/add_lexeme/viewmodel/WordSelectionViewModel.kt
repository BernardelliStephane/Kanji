package fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel

import androidx.lifecycle.ViewModel
import fr.steph.kanji.core.data.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class WordSelectionViewModel : ViewModel() {

    private val _selectedWord = MutableStateFlow<Word?>(null)
    val selectedWord: StateFlow<Word?> = _selectedWord

    fun setSelectedWord(word: Word?) {
        _selectedWord.update { word }
    }
}