package fr.steph.kanji.feature_dictionary.ui.add_lexeme.util

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.feature_dictionary.ui.dictionary.adapter.LexemeAdapter

class LexemeDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null)
            return (recyclerView.getChildViewHolder(view) as LexemeAdapter.LexemeViewHolder).getLexemeDetails()
        return null
    }
}