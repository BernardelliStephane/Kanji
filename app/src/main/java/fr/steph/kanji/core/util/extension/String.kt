package fr.steph.kanji.core.util.extension

import fr.steph.kanji.core.util.Moji.mojiConverter
import fr.steph.kanji.core.util.Moji.mojiDetector
import java.util.Locale

fun String.isLoneKanji(): Boolean {
    return length == 1 && mojiDetector.hasKanji(this)
}

fun String.isOnlyRomanLetters(): Boolean {
    val regex = Regex("[a-zA-Z\\s]*")
    return regex.matches(this)
}

fun String.isOnlyRomanCharacters(): Boolean {
    val regex = Regex("[a-zA-ZÀ-ÿ0-9\\s,.!_\"'–-]*")
    return regex.matches(this)
}

fun String.isOnlyJapaneseCharacters(): Boolean {
    return all { it.isKana() || it.isKanji() }
}

fun String.kanaToRomaji(): String {
    return mojiConverter.convertKanaToRomaji(this)
}

fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}