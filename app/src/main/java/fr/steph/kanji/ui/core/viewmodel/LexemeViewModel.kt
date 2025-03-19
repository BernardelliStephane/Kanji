package fr.steph.kanji.ui.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.R
import fr.steph.kanji.data.repository.LessonRepository
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.domain.enumeration.SortField
import fr.steph.kanji.domain.enumeration.SortOrder
import fr.steph.kanji.domain.model.Lexeme
import fr.steph.kanji.ui.core.use_case.GetLessonsUseCase
import fr.steph.kanji.ui.feature_dictionary.dictionary.uistate.FilterOptions
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

const val INSERTION_FAILURE = -1L
const val UPDATE_FAILURE = 0

@OptIn(ExperimentalCoroutinesApi::class)
abstract class LexemeViewModel(
    private val lexemeRepo: LexemeRepository,
) : ViewModel() {

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

    protected val lexemeValidationEventChannel = Channel<ValidationEvent>()
    val lexemeValidationEvents = lexemeValidationEventChannel.receiveAsFlow()

    fun insertLexeme(lexeme: Lexeme) = viewModelScope.launch {
        lexemeRepo.insertLexeme(lexeme).let {
            if (it == INSERTION_FAILURE)
                lexemeValidationEventChannel.send(ValidationEvent.Failure(R.string.room_failure))
            else lexemeValidationEventChannel.send(ValidationEvent.Success)
        }
    }

    fun updateLexeme(lexeme: Lexeme) = viewModelScope.launch {
        lexemeRepo.updateLexeme(lexeme).let {
            if (it == UPDATE_FAILURE)
                lexemeValidationEventChannel.send(ValidationEvent.Failure(R.string.room_failure))
            else lexemeValidationEventChannel.send(ValidationEvent.Success)
        }
    }

    fun deleteLexemesFromSelection(selection: List<Long>) = viewModelScope.launch {
        lexemeRepo.deleteLexemesFromSelection(selection).let {
            if (it != selection.size)
                lexemeValidationEventChannel.send(ValidationEvent.Failure(R.string.room_deletion_failure))
            else lexemeValidationEventChannel.send(ValidationEvent.Success)
        }
    }

    suspend fun getLexemeByCharacters(characters: String) =
        lexemeRepo.getLexemeByCharacters(characters)

    private suspend fun getAllLexemes(sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> lexemeRepo.lexemesOrderedByMeaning(sortOrder)
            SortField.LESSON_NUMBER -> lexemeRepo.lexemesOrderedByLessonNumber(sortOrder)
            SortField.ROMAJI -> lexemeRepo.lexemesOrderedByRomaji(sortOrder)
            SortField.ID -> lexemeRepo.lexemesOrderedById(sortOrder)
        }
    }

    private suspend fun filterLexemes(filter: List<Long>, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> lexemeRepo.filterLexemesOrderedByMeaning(filter, sortOrder)
            SortField.LESSON_NUMBER -> lexemeRepo.filterLexemesOrderedByLessonNumber(filter, sortOrder)
            SortField.ROMAJI -> lexemeRepo.filterLexemesOrderedByRomaji(filter, sortOrder)
            SortField.ID -> lexemeRepo.filterLexemesOrderedById(filter, sortOrder)
        }
    }

    private suspend fun searchLexemes(query: String, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> lexemeRepo.searchLexemesOrderedByMeaning(query, sortOrder)
            SortField.LESSON_NUMBER -> lexemeRepo.searchLexemesOrderedByLessonNumber(query, sortOrder)
            SortField.ROMAJI -> lexemeRepo.searchLexemesOrderedByRomaji(query, sortOrder)
            SortField.ID -> lexemeRepo.searchLexemesOrderedById(query, sortOrder)
        }
    }

    private suspend fun searchInFilteredLexemes(query: String, filter: List<Long>, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> lexemeRepo.searchInFilteredLexemesOrderedByMeaning(query, filter, sortOrder)
            SortField.LESSON_NUMBER -> lexemeRepo.searchInFilteredLexemesOrderedByLessonNumber(query, filter, sortOrder)
            SortField.ROMAJI -> lexemeRepo.searchInFilteredLexemesOrderedByRomaji(query, filter, sortOrder)
            SortField.ID -> lexemeRepo.searchInFilteredLexemesOrderedById(query, filter, sortOrder)
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