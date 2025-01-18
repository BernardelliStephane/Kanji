package fr.steph.kanji.utils

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}