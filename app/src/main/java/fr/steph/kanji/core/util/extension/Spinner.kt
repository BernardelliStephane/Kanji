package fr.steph.kanji.core.util.extension

import android.view.LayoutInflater
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.ListPopupWindow

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

fun Spinner.setMaxVisibleItems(layoutInflater: LayoutInflater, itemCount: Int) {
    val itemHeight = measureLayoutHeight(layoutInflater, android.R.layout.simple_spinner_dropdown_item)

    val maxHeight = itemHeight * itemCount

    try {
        val popup = AppCompatSpinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val popupWindow = popup.get(this) as ListPopupWindow
        popupWindow.height = maxHeight
    } catch (e: Exception) {
        e.printStackTrace()
    }
}