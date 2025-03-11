package fr.steph.kanji.utils.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.marginBottom
import androidx.core.view.marginTop

fun ViewGroup.measureLayoutHeight(layoutInflater: LayoutInflater, @LayoutRes layoutRes: Int): Int {
    val itemView = layoutInflater.inflate(layoutRes, this, false)

    itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

    val itemHeight = itemView.measuredHeight
        .plus(paddingTop).plus(paddingBottom)
        .plus(marginTop).plus(marginBottom)

    return itemHeight
}