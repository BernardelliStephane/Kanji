package fr.steph.kanji.utils.extension

import android.widget.Spinner

fun Spinner.hideSpinnerDropDown() {
    try {
        val method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
        method.isAccessible = true
        method.invoke(this)
    }
    catch (e: Exception) {
        e.printStackTrace()
    }
}