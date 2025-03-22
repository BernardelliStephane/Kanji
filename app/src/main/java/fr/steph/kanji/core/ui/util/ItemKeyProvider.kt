package fr.steph.kanji.core.ui.util

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

/**
 * A custom [ItemKeyProvider] implementation that provides stable item keys for a [RecyclerView] using item IDs.
 *
 * This class is used to map a position in the [RecyclerView] to a unique key (item ID) and vice versa,
 * which is useful for handling stable item identifiers, especially in scenarios like pagination or restoring
 * the state of a [RecyclerView].
 */
class ItemKeyProvider(private val recyclerView: RecyclerView) : ItemKeyProvider<Long>(SCOPE_CACHED) {
    override fun getKey(position: Int) = recyclerView.adapter?.getItemId(position)

    override fun getPosition(key: Long): Int {
        val viewHolder = recyclerView.findViewHolderForItemId(key)
        return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}