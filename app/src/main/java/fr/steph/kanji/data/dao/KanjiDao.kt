package fr.steph.kanji.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import fr.steph.kanji.data.model.Kanji
import kotlinx.coroutines.flow.Flow

@Dao
interface KanjiDao {
    @Upsert
    suspend fun upsertKanji(kanji: Kanji)

    @Delete
    suspend fun deleteKanji(kanji: Kanji)

    // TODO rajouter les filtres (par type de kanji etc)
    @Query("SELECT * FROM kanji ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN id END ASC, " +
            "CASE WHEN :isAsc = 1 THEN id END DESC ")
    fun getKanjisOrderedById(isAsc: Int): Flow<List<Kanji>>

    @Query("SELECT * FROM kanji ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN romaji END ASC, " +
            "CASE WHEN :isAsc = 1 THEN romaji END DESC ")
    fun getKanjisOrderedByRomaji(isAsc: Int): Flow<List<Kanji>>

    @Query("SELECT * FROM kanji ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN translation END ASC, " +
            "CASE WHEN :isAsc = 1 THEN translation END DESC ")
    fun getKanjisOrderedByTranslation(isAsc: Int): Flow<List<Kanji>>
}