package fr.steph.kanji.core.data.repository

import android.content.Context
import org.json.JSONObject

object StrokeOrderRepository {
    private lateinit var strokeMap: Map<String, String>

    fun load(context: Context) {
        val json = context.assets.open("kanji/kvg_index.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)

        strokeMap = mutableMapOf<String, String>().apply {
            jsonObject.keys().forEach { key ->
                val array = jsonObject.getJSONArray(key)
                if (array.length() > 0)
                    put(key, array.getString(array.length() - 1))
            }
        }
    }

    fun getStrokeSvg(character: Char): String? {
        if (!this::strokeMap.isInitialized) return null
        return strokeMap[character.toString()]
    }
}