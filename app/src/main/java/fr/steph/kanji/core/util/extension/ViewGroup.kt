package fr.steph.kanji.core.util.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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

/**
 * Executes the given [function] after the [ViewGroup] has been laid out and measured.
 *
 * This is useful for retrieving view dimensions or performing operations that depend on the
 * view's final size. The listener is automatically removed after the first invocation to
 * avoid multiple triggers or infinite loops caused by layout changes.
 *
 * @param function The lambda to execute after the view has been laid out.
 */
fun ViewGroup.onLayoutReady(function: () -> Unit) {
    val view = this
    view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            function.invoke()
        }
    })
}