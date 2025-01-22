package fr.steph.kanji.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.steph.kanji.data.model.Lexeme

class RoomTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun convertChildrenKanjisToJSONString(childrenKanjis: List<Lexeme>): String {
        val itemType = object : TypeToken<List<Lexeme>>() {}.type
        return gson.toJson(childrenKanjis, itemType)
    }

    @TypeConverter
    fun convertJSONStringToChildrenKanjis(jsonString: String): List<Lexeme> {
        val itemType = object : TypeToken<List<Lexeme>>() {}.type
        return gson.fromJson(jsonString, itemType)
    }
}