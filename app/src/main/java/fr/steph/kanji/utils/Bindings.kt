package fr.steph.kanji.utils

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter


@BindingAdapter("android:visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("android:layout_marginTop")
fun setBottomMargin(view: View, topMargin: Float) {
    val layoutParams = view.layoutParams as MarginLayoutParams
    layoutParams.setMargins(
        layoutParams.marginStart, Math.round(topMargin),
        layoutParams.marginEnd, layoutParams.bottomMargin
    )
    view.layoutParams = layoutParams
}