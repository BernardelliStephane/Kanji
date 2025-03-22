package fr.steph.kanji.core.util.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.marginBottom
import androidx.core.view.marginTop

/**
 * Measures the height of a layout item based on the provided layout resource.
 *
 * This function inflates a layout resource and measures the height of the resulting item view,
 * taking both paddings and margins into account.
 *
 * @param layoutInflater The [LayoutInflater] used to inflate the layout resource.
 * @param layoutRes The resource ID of the layout to measure.
 */
fun ViewGroup.measureLayoutHeight(layoutInflater: LayoutInflater, @LayoutRes layoutRes: Int): Int {
    val itemView = layoutInflater.inflate(layoutRes, this, false)

    itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

    val itemHeight = itemView.measuredHeight
        .plus(paddingTop).plus(paddingBottom)
        .plus(marginTop).plus(marginBottom)

    return itemHeight
}