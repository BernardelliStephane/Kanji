package fr.steph.kanji.utils.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.R

fun RecyclerView.setMaxVisibleItems(layoutInflater: LayoutInflater, itemCount: Int) {
    val itemView = layoutInflater.inflate(R.layout.item_lesson, this, false)

    itemView.measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.UNSPECIFIED
    )

    val itemHeight = itemView.measuredHeight
    val maxHeight = itemHeight * itemCount

    layoutParams.height = maxHeight
    requestLayout()

    itemView.parent?.let {
        (it as? ViewGroup)?.removeView(itemView)
    }
}