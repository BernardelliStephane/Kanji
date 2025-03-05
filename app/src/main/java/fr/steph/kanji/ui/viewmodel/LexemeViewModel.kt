package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.R
import fr.steph.kanji.data.model.Lexeme
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.data.utils.enumeration.SortField
import fr.steph.kanji.data.utils.enumeration.SortOrder
import fr.steph.kanji.ui.uistate.FilterOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val FAILURE = -1L

@OptIn(ExperimentalCoroutinesApi::class)
abstract class LexemeViewModel(private val repo: LexemeRepository) : ViewModel() {

    private val _filterOptions = MutableStateFlow(FilterOptions())

    private val _lexemes = _filterOptions.flatMapLatest { options ->
        when {
            options.filter.isNotEmpty() && options.searchQuery.isNotBlank() ->
                searchInFilteredLexemes(options.searchQuery, options.filter, options.sortField, options.sortOrder)

            options.filter.isNotEmpty() ->
                filterLexemes(options.filter, options.sortField, options.sortOrder)

            options.searchQuery.isNotBlank() ->
                searchLexemes(options.searchQuery, options.sortField, options.sortOrder)

            else -> getAllLexemes(options.sortField, options.sortOrder)
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

    private suspend fun getAllLexemes(sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> repo.lexemesOrderedByMeaning(sortOrder)
            SortField.LESSON_NUMBER -> repo.lexemesOrderedByLessonNumber(sortOrder)
            SortField.ROMAJI -> repo.lexemesOrderedByRomaji(sortOrder)
            SortField.ID -> repo.lexemesOrderedById(sortOrder)
        }
    }

    private suspend fun filterLexemes(filter: List<Long>, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> repo.filterLexemesOrderedByMeaning(filter, sortOrder)
            SortField.LESSON_NUMBER -> repo.filterLexemesOrderedByLessonNumber(filter, sortOrder)
            SortField.ROMAJI -> repo.filterLexemesOrderedByRomaji(filter, sortOrder)
            SortField.ID -> repo.filterLexemesOrderedById(filter, sortOrder)
        }
    }

    private suspend fun searchLexemes(query: String, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> repo.searchLexemesOrderedByMeaning(query, sortOrder)
            SortField.LESSON_NUMBER -> repo.searchLexemesOrderedByLessonNumber(query, sortOrder)
            SortField.ROMAJI -> repo.searchLexemesOrderedByRomaji(query, sortOrder)
            SortField.ID -> repo.searchLexemesOrderedById(query, sortOrder)
        }
    }

    private suspend fun searchInFilteredLexemes(query: String, filter: List<Long>, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> repo.searchInFilteredLexemesOrderedByMeaning(query, filter, sortOrder)
            SortField.LESSON_NUMBER -> repo.searchInFilteredLexemesOrderedByLessonNumber(query, filter, sortOrder)
            SortField.ROMAJI -> repo.searchInFilteredLexemesOrderedByRomaji(query, filter, sortOrder)
            SortField.ID -> repo.searchInFilteredLexemesOrderedById(query, filter, sortOrder)
        }
    }

    fun isFilteringOngoing() = _filterOptions.value.run {
        filter.isNotEmpty() || searchQuery.isNotBlank()
    }

    fun getSortingState() = _filterOptions.value.run { Pair(sortField, sortOrder) }

    fun updateSorting(sortField: SortField, sortOrder: SortOrder) =
        _filterOptions.update { options ->
            options.copy(sortField = sortField, sortOrder = sortOrder)
        }

    fun getFilter() = _filterOptions.value.filter

    fun updateFilter(filter: List<Long>) = _filterOptions.update { options ->
        options.copy(filter = filter)
    }

    fun updateQuery(query: String) = _filterOptions.update { options ->
        options.copy(searchQuery = query)
    }

    sealed class ValidationEvent {
        data class Failure(val failureMessage: Int) : ValidationEvent()
        data object Success : ValidationEvent()
    }
}