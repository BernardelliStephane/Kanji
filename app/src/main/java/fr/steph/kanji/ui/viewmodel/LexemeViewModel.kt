package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.R
import fr.steph.kanji.data.model.Lexeme
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.data.utils.enumeration.SortField
import fr.steph.kanji.ui.uistate.FilterOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val FAILURE = -1L

@OptIn(ExperimentalCoroutinesApi::class)
abstract class LexemeViewModel(private val repo: LexemeRepository) : ViewModel() {

    private val _filterOptions = MutableStateFlow(FilterOptions())

    private val _lexemes = _filterOptions.flatMapLatest { options ->
        if (options.searchQuery.isNotBlank())
            return@flatMapLatest repo.searchLexemes(options.searchQuery)

        when (options.sortField) {
            SortField.ID -> repo.lexemesOrderedById(options.sortOrder)
            SortField.ROMAJI -> repo.lexemesOrderedByRomaji(options.sortOrder)
            SortField.MEANING -> repo.lexemesOrderedByTranslation(options.sortOrder)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val lexemes = _lexemes.asLiveData()

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun upsertLexeme(lexeme: Lexeme) = viewModelScope.launch {
        repo.upsertLexeme(lexeme).let {
            if (it == FAILURE)
                validationEventChannel.send(ValidationEvent.Failure(R.string.room_insertion_failure))
            else validationEventChannel.send(ValidationEvent.Success)
        }
    }

    fun deleteLexemesFromSelection(selection: List<Long>) = viewModelScope.launch {
        repo.deleteLexemesFromSelection(selection).let {
            if (it != selection.size)
                validationEventChannel.send(ValidationEvent.Failure(R.string.room_deletion_failure))
            else validationEventChannel.send(ValidationEvent.Success)
        }
    }

    sealed class ValidationEvent {
        data class Failure(val failureMessage: Int) : ValidationEvent()
        data object Success : ValidationEvent()
    }
}