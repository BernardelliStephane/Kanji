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

    // TODO rajouter les filtres (par type de lexeme etc)
    @Query("SELECT * FROM lexeme ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN id END ASC, " +
            "CASE WHEN :isAsc = 1 THEN id END DESC ")
    fun lexemesOrderedById(isAsc: Int): Flow<List<Lexeme>>

    @Query("SELECT * FROM lexeme ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN romaji END ASC, " +
            "CASE WHEN :isAsc = 1 THEN romaji END DESC ")
    fun lexemesOrderedByRomaji(isAsc: Int): Flow<List<Lexeme>>

    @Query("SELECT * FROM lexeme ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN meaning END ASC, " +
            "CASE WHEN :isAsc = 1 THEN meaning END DESC ")
    fun lexemesOrderedByMeaning(isAsc: Int): Flow<List<Lexeme>>
}