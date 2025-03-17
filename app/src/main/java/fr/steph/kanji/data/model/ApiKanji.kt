package fr.steph.kanji.data.model

import com.google.gson.annotations.SerializedName

data class ApiKanji(
    val kanji: String,
    @SerializedName("on_readings")
    val onReadings: List<String>,
    @SerializedName("kun_readings")
    val kunReadings: List<String>,
    @SerializedName("name_readings")
    val nameReadings: List<String>,
    val meanings: List<String>,
    @SerializedName("grade")
    val gradeTaught: Int?,
    @SerializedName("jlpt")
    val jlptLevel: Int?,
    @SerializedName("freq_mainichi_shinbun")
    val useFrequency: Int?,
    val unicode: String,
)