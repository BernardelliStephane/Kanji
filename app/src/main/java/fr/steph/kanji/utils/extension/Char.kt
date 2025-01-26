package fr.steph.kanji.utils.extension

fun Char.isKana(): Boolean {
    val unicodeBlock = Character.UnicodeBlock.of(this)

    return  unicodeBlock == Character.UnicodeBlock.HIRAGANA ||
            unicodeBlock == Character.UnicodeBlock.KATAKANA ||
            unicodeBlock == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS
}

fun Char.isKanji(): Boolean {
    return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
}