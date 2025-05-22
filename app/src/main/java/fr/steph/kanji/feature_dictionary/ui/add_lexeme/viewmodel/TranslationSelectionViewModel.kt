package fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel

import androidx.lifecycle.ViewModel
import fr.steph.kanji.core.data.model.jisho.Jisho
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TranslationSelectionViewModel : ViewModel() {

    private val _selectedTranslation = MutableStateFlow<Jisho?>(null)
    val selectedTranslation: StateFlow<Jisho?> = _selectedTranslation

    fun setSelectedTranslation(translation: Jisho?) {
        _selectedTranslation.update { translation }
    }
}