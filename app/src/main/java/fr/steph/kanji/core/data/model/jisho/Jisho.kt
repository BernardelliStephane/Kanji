package fr.steph.kanji.core.data.model.jisho

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Jisho(
    @SerializedName("is_common")
    val isCommon: Boolean,
    @SerializedName("japanese")
    val writings: List<Writing>,
    val jlpt: List<String>,
    val senses: List<Sense>,
) : Serializable {
    fun getJlptLevels(): String {
        if (jlpt.isEmpty()) return "No JLPT level associated"
        val jlptLevels = jlpt.map { it.substringAfter('-').uppercase() }

        return if (jlptLevels.size == 1) "JLPT level ${jlptLevels[0]}"
            else "JLPT levels ${jlptLevels.joinToString(", ")}"
    }
}