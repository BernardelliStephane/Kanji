package fr.steph.kanji.core.util.extension

import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment

fun DialogFragment.setupDialogWindow(alignBottom: Boolean = true) {
    dialog?.window?.apply {
        setLayout(MATCH_PARENT, WRAP_CONTENT)
        decorView.background.alpha = 0
        if (alignBottom) setGravity(Gravity.BOTTOM)
    }
}