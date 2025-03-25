package fr.steph.kanji.feature_dictionary.ui.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.core.domain.enumeration.SortField
import fr.steph.kanji.core.domain.enumeration.SortOrder
import fr.steph.kanji.core.ui.util.LexemeResource
import fr.steph.kanji.feature_dictionary.domain.use_case.DictionaryUseCases
import fr.steph.kanji.feature_dictionary.ui.dictionary.util.FilterOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryViewModel(private val dictionaryUseCases: DictionaryUseCases) : ViewModel() {

    private val _filterOptions = MutableStateFlow(FilterOptions())

    private val _lexemes = _filterOptions.flatMapLatest { options ->
        with(options) {
            when {
                filter.isNotEmpty() && searchQuery.isNotBlank() ->
                    dictionaryUseCases.searchInFilteredLexemes(
                        searchQuery, filter, sortField, sortOrder
                    )

                filter.isNotEmpty() ->
                    dictionaryUseCases.filterLexemes(filter, sortField, sortOrder)

                searchQuery.isNotBlank() ->
                    dictionaryUseCases.searchLexemes(searchQuery, sortField, sortOrder)

                else -> dictionaryUseCases.getLexemes(sortField, sortOrder)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val lexemes = _lexemes.asLiveData()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    private val validationEventChannel = Channel<LexemeResource>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun deleteLexemesFromSelection(selection: List<Long>) = viewModelScope.launch {
        val result = dictionaryUseCases.deleteLexemesFromSelection(selection)
        validationEventChannel.send(result)
    }

    fun onSelectionChanged(selectionSize: Int) {
        _isSelectionMode.update { it || selectionSize > 0 }
    }

    fun isSelectionActive() = isSelectionMode.value

    fun disableSelectionMode() = _isSelectionMode.update { false }

    fun isFilteringOngoing() = _filterOptions.value.run {
        filter.isNotEmpty() || searchQuery.isNotBlank()
    }

    fun getSortingState() = _filterOptions.value.run { Pair(sortField, sortOrder) }

    fun updateSorting(sortField: SortField, sortOrder: SortOrder) =
        _filterOptions.update { it.copy(sortField = sortField, sortOrder = sortOrder) }

    fun getFilter() = _filterOptions.value.filter

    fun updateFilter(filter: List<Long>) = _filterOptions.update { it.copy(filter = filter) }

    fun updateQuery(query: String) = _filterOptions.update { it.copy(searchQuery = query) }
}