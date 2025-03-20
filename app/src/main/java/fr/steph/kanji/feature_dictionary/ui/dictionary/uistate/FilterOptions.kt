package fr.steph.kanji.feature_dictionary.ui.dictionary.uistate

import fr.steph.kanji.core.domain.enumeration.SortField
import fr.steph.kanji.core.domain.enumeration.SortOrder

data class FilterOptions(
    var sortField: SortField = SortField.MEANING,
    var sortOrder: SortOrder = SortOrder.ASCENDING,
    val filter: List<Long> = listOf(),
    var searchQuery: String = "",
)