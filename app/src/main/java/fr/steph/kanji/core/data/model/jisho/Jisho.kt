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
) : Serializable