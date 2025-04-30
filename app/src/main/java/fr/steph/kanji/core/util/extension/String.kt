package fr.steph.kanji.core.util.extension

import com.moji4j.MojiConverter
import java.util.Locale
import java.util.regex.Pattern

val mojiConverter = MojiConverter()

/**
 * Returns `true` if this string contains one or several Kanji character(s).
 *
 * See [isKanji] for the definition of a Kanji character.
 */
fun String.hasKanji(): Boolean {
    for (c in this)
        if (c.isKanji()) return true
    return false
}

/**
 * Returns `true` if this string contains only a single Kanji character.
 *
 * See [isKanji] for the definition of a Kanji character.
 */
fun String.isLoneKanji(): Boolean {
    return length == 1 && hasKanji()
}

/**
 * Returns `true` if this string contains only roman letters and spaces.
 *
 * Roman letters are defined as letters from the Latin alphabet (a-z), both uppercase and lowercase.
 */
fun String.isOnlyRomanLetters(): Boolean {
    val regex = Regex("[a-zA-Z\\s,]*")
    return regex.matches(this)
}

/**
 * Returns `true` if this string contains only roman characters.
 *
 * Roman characters include roman letters (uppercase and lowercase, including accented characters),
 * numbers, spaces and punctuation marks.
 */
fun String.isOnlyRomanCharacters(): Boolean {
    val regex = Regex("[a-zA-ZÀ-ÿ0-9\\s,.!_\"'()–-]*")
    return regex.matches(this)
}

/**
 * Returns `true` if this string contains only Japanese characters (Hiragana, Katakana, or Kanji).
 *
 * See [isKana] and [isKanji] for the individual character checks.
 */
fun String.isOnlyJapaneseCharacters(): Boolean {
    return all { it.isKana() || it.isKanji() }
}

/**
 * Converts a string of Kana characters to Romaji.
 */
fun String.kanaToRomaji(): String {
    return mojiConverter.convertKanaToRomaji(this)
}

/**
 * Capitalizes the first character of the given string.
 */
fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

/**
 * Extracts all SVG path data (`d` attributes) from the string.
 *
 * This method scans the string for `<path>` elements and retrieves the values of their `d` attributes,
 * which define the drawing instructions for an SVG path.
 *
 * @return A list of extracted path data strings from `d="..."` attributes.
 */
fun String.extractPaths(): List<String> {
    val svgPathPattern = Pattern.compile("""<path .*(?<= )d="([^"]+)".*/>""")
    return svgPathPattern.collect(this, 1)
}