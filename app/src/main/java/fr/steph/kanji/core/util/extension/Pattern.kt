package fr.steph.kanji.core.util.extension

import java.util.regex.Pattern

/**
 * Collects all non-blank matches of a specified capturing group from the given input string.
 *
 * This method applies the regex pattern to the input string, extracts values from the specified
 * capturing group index, and returns them as a list. Blank matches are ignored.
 *
 * @receiver The compiled regex pattern.
 * @param input The input string to search for matches.
 * @param groupIndex The index of the capturing group to extract (default is `0` for full match).
 * @return A list of extracted non-blank matches.
 */
fun Pattern.collect(input: String, groupIndex: Int = 0): List<String> {
    val matcher = matcher(input)
    val list = ArrayList<String>()

    while (matcher.find()) {
        val match = matcher.group(groupIndex)
        if (!match.isNullOrBlank())
            list.add(match)
    }

    return list
}