package fr.steph.kanji.utils.extension

import fr.steph.kanji.utils.Moji.mojiConverter
import fr.steph.kanji.utils.Moji.mojiDetector

fun String.isLoneKanji(): Boolean {
    return length == 1 && mojiDetector.hasKanji(this)
}

fun String.kanaToRomaji(): String {
    return mojiConverter.convertKanaToRomaji(this)
}