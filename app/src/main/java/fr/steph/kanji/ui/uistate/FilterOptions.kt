package fr.steph.kanji.ui.uistate

import fr.steph.kanji.data.utils.enumeration.SortField
import fr.steph.kanji.data.utils.enumeration.SortOrder

data class FilterOptions(
    var searchQuery: String = "",
    var sortField: SortField = SortField.ID,
    var sortOrder: SortOrder = SortOrder.ASCENDING,
)