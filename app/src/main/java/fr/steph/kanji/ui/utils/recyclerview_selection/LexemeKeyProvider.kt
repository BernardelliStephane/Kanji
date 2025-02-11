package fr.steph.kanji.ui.utils.recyclerview_selection

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class LexemeKeyProvider(private val recyclerView: RecyclerView) : ItemKeyProvider<Long>(SCOPE_CACHED) {
    override fun getKey(position: Int) = recyclerView.adapter?.getItemId(position)

    override fun getPosition(key: Long): Int {
        val viewHolder = recyclerView.findViewHolderForItemId(key)
        return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}