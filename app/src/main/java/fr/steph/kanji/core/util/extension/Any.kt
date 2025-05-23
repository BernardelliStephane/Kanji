package fr.steph.kanji.core.util.extension

import android.util.Log

val Any.name: String
    get() = with(this) { javaClass.simpleName }

fun Any.log(content: String) {
    Log.d("fr.steph.kanji","${this.name} - $content")
}