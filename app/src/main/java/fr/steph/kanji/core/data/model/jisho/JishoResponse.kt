package fr.steph.kanji.core.data.model.jisho

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class JishoResponse(
    @SerializedName("data")
    val translations: List<Jisho>,
) : Serializable