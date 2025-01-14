package fr.steph.kanji.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import fr.steph.kanji.data.model.Lexeme
import kotlinx.coroutines.flow.Flow

@Dao
interface LexemeDao {

    @Upsert
    suspend fun upsertLexeme(lexeme: Lexeme): Long

    @Delete
    suspend fun deleteLexeme(lexeme: Lexeme): Int

    @Query("SELECT * FROM kana, kanji, compound ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN timestamp END ASC, " +
            "CASE WHEN :isAsc = 1 THEN timestamp END DESC")
    fun getLexemesOrderedByTimestamp(isAsc: Int): Flow<List<Lexeme>>

    @Query("SELECT * FROM kana, kanji, compound ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN romaji END ASC, " +
            "CASE WHEN :isAsc = 1 THEN romaji END DESC ")
    fun getLexemesOrderedByRomaji(isAsc: Int): Flow<List<Lexeme>>

    @Query("SELECT * FROM kana, kanji, compound ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN meaning END ASC, " +
            "CASE WHEN :isAsc = 1 THEN meaning END DESC ")
    fun getLexemesOrderedByTranslation(isAsc: Int): Flow<List<Lexeme>>

    /*@Upsert
    suspend fun upsertKanji(kanji: Kanji): Long

    @Delete
    suspend fun deleteKanji(kanji: Kanji): Int

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
    fun getKanjisOrderedByTranslation(isAsc: Int): Flow<List<Kanji>>*/
}