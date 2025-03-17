package fr.steph.kanji.ui.feature_dictionary.dictionary.uistate

import fr.steph.kanji.domain.enumeration.SortField
import fr.steph.kanji.domain.enumeration.SortOrder

data class FilterOptions(
    var sortField: SortField = SortField.MEANING,
    var sortOrder: SortOrder = SortOrder.ASCENDING,
    val filter: List<Long> = listOf(),
    var searchQuery: String = "",
)