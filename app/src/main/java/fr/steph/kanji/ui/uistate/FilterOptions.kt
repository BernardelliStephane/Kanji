package fr.steph.kanji.ui.uistate

import fr.steph.kanji.data.utils.enumeration.SortField
import fr.steph.kanji.data.utils.enumeration.SortOrder

data class FilterOptions(
    var sortField: SortField = SortField.MEANING,
    var sortOrder: SortOrder = SortOrder.ASCENDING,
    val filter: ArrayList<Int> = ArrayList(),
    var searchQuery: String = "",
)