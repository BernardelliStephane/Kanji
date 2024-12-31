package fr.steph.kanji.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.steph.kanji.data.model.Kanji

class RoomTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun convertChildrenKanjisToJSONString(childrenKanjis: List<Kanji>): String {
        val itemType = object : TypeToken<List<Kanji>>() {}.type
        return gson.toJson(childrenKanjis, itemType)
    }

    @TypeConverter
    fun convertJSONStringToChildrenKanjis(jsonString: String): List<Kanji> {
        val itemType = object : TypeToken<List<Kanji>>() {}.type
        return gson.fromJson(jsonString, itemType)
    }
}