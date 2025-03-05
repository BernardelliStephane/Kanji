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

    @Query("DELETE FROM lexeme WHERE id in (:selection)")
    suspend fun deleteLexemesFromSelection(selection: List<Long>): Int


    /********* Lexemes matching search query **********/


    @Query("SELECT * FROM lexeme WHERE " +
            "lower(meaning) LIKE '%' || :query || '%' OR " +
            "romaji LIKE '%' || :query || '%' OR " +
            "characters LIKE '%' || :query || '%' ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN meaning END ASC, " +
            "CASE WHEN :isAsc = 1 THEN meaning END DESC ")
    fun searchLexemesOrderedByMeaning(query: String, isAsc: Int): Flow<List<Lexeme>>

    @Query("SELECT * FROM lexeme WHERE " +
            "lower(meaning) LIKE '%' || :query || '%' OR " +
            "romaji LIKE '%' || :query || '%' OR " +
            "characters LIKE '%' || :query || '%' ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN lessonNumber END ASC, " +
            "CASE WHEN :isAsc = 1 THEN lessonNumber END DESC ")
    fun searchLexemesOrderedByLessonNumber(query: String, isAsc: Int): Flow<List<Lexeme>>

    @Query("SELECT * FROM lexeme WHERE " +
            "lower(meaning) LIKE '%' || :query || '%' OR " +
            "romaji LIKE '%' || :query || '%' OR " +
            "characters LIKE '%' || :query || '%' ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN romaji END ASC, " +
            "CASE WHEN :isAsc = 1 THEN romaji END DESC ")
    fun searchLexemesOrderedByRomaji(query: String, isAsc: Int): Flow<List<Lexeme>>

    @Query("SELECT * FROM lexeme WHERE " +
            "lower(meaning) LIKE '%' || :query || '%' OR " +
            "romaji LIKE '%' || :query || '%' OR " +
            "characters LIKE '%' || :query || '%' ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN id END ASC, " +
            "CASE WHEN :isAsc = 1 THEN id END DESC ")
    fun searchLexemesOrderedById(query: String, isAsc: Int): Flow<List<Lexeme>>


    /******************** All lexemes ********************/


    @Query("SELECT * FROM lexeme ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN meaning END ASC, " +
            "CASE WHEN :isAsc = 1 THEN meaning END DESC ")
    fun lexemesOrderedByMeaning(isAsc: Int): Flow<List<Lexeme>>

    @Query("SELECT * FROM lexeme ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN lessonNumber END ASC, " +
            "CASE WHEN :isAsc = 1 THEN lessonNumber END DESC ")
    fun lexemesOrderedByLessonNumber(isAsc: Int): Flow<List<Lexeme>>

    @Query("SELECT * FROM lexeme ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN romaji END ASC, " +
            "CASE WHEN :isAsc = 1 THEN romaji END DESC ")
    fun lexemesOrderedByRomaji(isAsc: Int): Flow<List<Lexeme>>

    @Query("SELECT * FROM lexeme ORDER BY " +
            "CASE WHEN :isAsc = 0 THEN id END ASC, " +
            "CASE WHEN :isAsc = 1 THEN id END DESC ")
    fun lexemesOrderedById(isAsc: Int): Flow<List<Lexeme>>
}