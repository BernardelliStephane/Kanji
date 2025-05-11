package fr.steph.kanji.core.ui.util

import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("android:visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("android:rawInputType")
fun setRawInputType(view: EditText, inputType: Int) {
    view.setRawInputType(inputType)
}

@BindingAdapter("errorRes")
fun setErrorMessage(view: TextInputLayout, resError: Int?) {
    view.error = if (resError == null) null
    else view.context.resources.getString(resError)
}
