package fr.steph.kanji.ui.utils.recyclerview_selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.ui.adapter.LessonAdapter

class LessonDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null)
            return (recyclerView.getChildViewHolder(view) as LessonAdapter.LessonViewHolder).getLessonDetails()
        return null
    }
}