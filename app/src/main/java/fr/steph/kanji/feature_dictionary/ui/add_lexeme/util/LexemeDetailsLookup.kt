package fr.steph.kanji.feature_dictionary.ui.add_lexeme.util

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.feature_dictionary.ui.dictionary.adapter.LexemeAdapter

/**
 * Custom [ItemDetailsLookup] implementation for retrieving details of a lexeme item in a [RecyclerView].
 *
 * This class is used to provide item details for a specific lexeme when interacting with the
 * [RecyclerView] during touch events, such as when selecting or highlighting a lexeme.
 *
 * It extends [ItemDetailsLookup] and overrides the [getItemDetails] method to retrieve the item
 * details of the lexeme under a given motion event (e.g., a touch event).
 */
class LexemeDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null)
            return (recyclerView.getChildViewHolder(view) as LexemeAdapter.LexemeViewHolder).getLexemeDetails()
        return null
    }
}