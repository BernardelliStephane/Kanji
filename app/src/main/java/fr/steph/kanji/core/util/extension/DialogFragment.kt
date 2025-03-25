package fr.steph.kanji.core.util.extension

import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment

/**
 * Configures the dialog window appearance by allowing custom background usage and setting the
 * dialog's width to MATCH_PARENT.
 *
 * @param alignBottom If `true`, the dialog will be aligned to the bottom of the screen. Default is `true`.
 */
fun DialogFragment.setupDialogWindow(alignBottom: Boolean = true) {
    dialog?.window?.apply {
        setLayout(MATCH_PARENT, WRAP_CONTENT)
        decorView.background.alpha = 0
        if (alignBottom) setGravity(Gravity.BOTTOM)
    }
}