package fr.steph.kanji.core.data.model.oracle

import com.google.gson.annotations.SerializedName

data class FuriganaResult(
    @SerializedName("result")
    val furigana: String
)