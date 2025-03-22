package fr.steph.kanji.core.util.extension

import android.view.LayoutInflater
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.ListPopupWindow

/**
 * Extension function for [Spinner] to hide its dropdown menu by invoking its
 * `onDetachedFromWindow` method using reflection.
 */
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

/**
 * Extension function for [Spinner] that sets a maximum number of items to be visible without
 * requiring scrolling. The dropdown height is adjusted to display exactly `itemCount` items.
 *
 * This function uses reflection to access private members of the spinner's dropdown
 * and modify the height of the popup window.
 *
 * @param layoutInflater The [LayoutInflater] to measure the height of a single item in the spinner.
 * @param itemCount The maximum number of items to show in the dropdown without needing to scroll.
 */
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