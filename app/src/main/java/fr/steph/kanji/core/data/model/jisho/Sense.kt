package fr.steph.kanji.core.data.model.jisho

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Sense(
    @SerializedName("english_definitions")
    val meaning: List<String>,
    @SerializedName("parts_of_speech")
    val grammaticalRole: List<String>,
) : Serializable