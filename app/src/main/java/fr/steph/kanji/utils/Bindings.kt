package fr.steph.kanji.utils

import android.view.View
import android.view.View.FOCUSABLE
import android.view.View.NOT_FOCUSABLE
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("android:visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("focusable")
fun setFocusability(view: View, isFocusable: Boolean) {
    view.focusable = if(isFocusable) FOCUSABLE else NOT_FOCUSABLE
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

@BindingAdapter("errorRes")
fun setErrorMessage(view: TextInputLayout, resError: Int?) {
    view.error = if (resError == null) null
    else view.context.resources.getString(resError)
}