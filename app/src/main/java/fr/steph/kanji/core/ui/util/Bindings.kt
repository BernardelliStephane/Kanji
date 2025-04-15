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
    (view.layoutParams as MarginLayoutParams).topMargin = Math.round(topMargin)
}

@BindingAdapter("errorRes")
fun setErrorMessage(view: TextInputLayout, resError: Int?) {
    view.error = if (resError == null) null
    else view.context.resources.getString(resError)
}