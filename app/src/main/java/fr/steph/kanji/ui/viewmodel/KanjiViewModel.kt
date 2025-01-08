package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.data.repository.KanjiRepository
import fr.steph.kanji.data.utils.enumeration.SortField
import fr.steph.kanji.data.utils.enumeration.SortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
abstract class KanjiViewModel(private val repo: KanjiRepository): ViewModel() {

    private val _sortType = MutableStateFlow(Pair(SortField.ID, SortOrder.ASCENDING))
    private val _kanjis = _sortType.flatMapLatest {
        when(it.first) {
            SortField.ID -> repo.getKanjisOrderedById(it.second)
            SortField.ROMAJI -> repo.getKanjisOrderedByRomaji(it.second)
            SortField.TRANSLATION -> repo.getKanjisOrderedByTranslation(it.second)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val kanjis = _kanjis.asLiveData()
}