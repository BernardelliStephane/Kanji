package fr.steph.kanji.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import fr.steph.kanji.data.model.Kanji

@Dao
interface KanjiDao {

    @Upsert
    suspend fun upsertKanji(kanji: Kanji)

    @Delete
    suspend fun deleteKanji(kanji: Kanji)
}