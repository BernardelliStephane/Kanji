package fr.steph.kanji.core.ui.util

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("android:visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("android:layout_marginTop")
fun setTopMargin(view: View, topMargin: Float) {
    val layoutParams = view.layoutParams as MarginLayoutParams
    layoutParams.setMargins(
        layoutParams.marginStart, Math.round(topMargin),
        layoutParams.marginEnd, layoutParams.bottomMargin
    )
    view.layoutParams = layoutParams
}

@BindingAdapter("errorRes")
fun setErrorMessage(view: TextInputLayout, resError: Int?) {
    view.error = if (resError == null) null
    else view.context.resources.getString(resError)
}