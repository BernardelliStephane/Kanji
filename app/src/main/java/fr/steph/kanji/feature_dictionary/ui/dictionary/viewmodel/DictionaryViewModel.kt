package fr.steph.kanji.feature_dictionary.ui.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.core.domain.enumeration.SortField
import fr.steph.kanji.core.domain.enumeration.SortOrder
import fr.steph.kanji.feature_dictionary.domain.use_case.DeleteLexemesFromSelectionUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.FilterLexemesUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLexemesUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.SearchInFilteredLexemesUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.SearchLexemesUseCase
import fr.steph.kanji.feature_dictionary.ui.dictionary.uistate.FilterOptions
import fr.steph.kanji.core.ui.viewmodel.LexemeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryViewModel(
    getLexemes: GetLexemesUseCase,
    searchLexemes: SearchLexemesUseCase,
    filterLexemes: FilterLexemesUseCase,
    searchInFilteredLexemes: SearchInFilteredLexemesUseCase,
    private val deleteLexemeFromSelection: DeleteLexemesFromSelectionUseCase,
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

            else -> getLexemes(options.sortField, options.sortOrder)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val lexemes = _lexemes.asLiveData()

    private val _selectionSize = MutableStateFlow(0)
    val selectionSize = _selectionSize.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    val allSelected = _selectionSize
        .map { it == (lexemes.value?.size ?: 0) }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val lexemeValidationEventChannel = Channel<LexemeViewModel.ValidationEvent>()
    val lexemeValidationEvents = lexemeValidationEventChannel.receiveAsFlow()

    fun deleteLexemesFromSelection(selection: List<Long>) = viewModelScope.launch {
        val result = deleteLexemeFromSelection(selection)
        lexemeValidationEventChannel.send(result)
    }

    fun onSelectionChanged(selectionSize: Int) {
        _isSelectionMode.update { it || selectionSize > 0 }
        _selectionSize.update { selectionSize }
    }

    fun disableSelectionMode() {
        _isSelectionMode.update { false }
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
}