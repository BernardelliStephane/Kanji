package fr.steph.kanji.core.util.extension

/**
 * Returns `true` if this character is a kana character (hiragana or katakana).
 *
 * A character is considered to be Kana if it belongs to one of the following Unicode blocks:
 * - [Hiragana](https://unicode.org/charts/PDF/U3040.pdf)
 * - [Katakana](https://unicode.org/charts/PDF/U30A0.pdf)
 * - [Katakana Phonetic Extensions](https://unicode.org/charts/PDF/U31F0.pdf)
 */
fun Char.isKana(): Boolean {
    val unicodeBlock = Character.UnicodeBlock.of(this)

    return  unicodeBlock == Character.UnicodeBlock.HIRAGANA ||
            unicodeBlock == Character.UnicodeBlock.KATAKANA ||
            unicodeBlock == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS
}

/**
 * Returns `true` if this character is a Kanji character.
 *
 * A character is considered to be a Kanji if it belongs to the
 * [CJK Unified Ideographs](https://unicode.org/charts/PDF/U4E00.pdf) Unicode block.
 */
fun Char.isKanji(): Boolean {
    return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
}