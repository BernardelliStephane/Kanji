package fr.steph.kanji.data.repository

import androidx.annotation.WorkerThread
import fr.steph.kanji.data.dao.LexemeDao
import fr.steph.kanji.data.model.Lexeme
import fr.steph.kanji.data.utils.enumeration.SortOrder

class LexemeRepository(private val lexemeDao: LexemeDao) {

    suspend fun upsertLexeme(lexeme: Lexeme) =
        lexemeDao.upsertLexeme(lexeme)

    suspend fun deleteLexeme(lexeme: Lexeme) =
        lexemeDao.deleteLexeme(lexeme)

    suspend fun deleteLexemesFromSelection(selection: List<Long>) =
        lexemeDao.deleteLexemesFromSelection(selection)


    /********* Lexemes matching search query **********/


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun searchLexemesOrderedByMeaning(query: String, sortOrder: SortOrder) =
        lexemeDao.searchLexemesOrderedByMeaning(query, sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun searchLexemesOrderedByLessonNumber(query: String, sortOrder: SortOrder) =
        lexemeDao.searchLexemesOrderedByLessonNumber(query, sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun searchLexemesOrderedByRomaji(query: String, sortOrder: SortOrder) =
        lexemeDao.searchLexemesOrderedByRomaji(query, sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun searchLexemesOrderedById(query: String, sortOrder: SortOrder) =
        lexemeDao.searchLexemesOrderedById(query, sortOrder.index)


    /******************** All lexemes ********************/


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun lexemesOrderedByMeaning(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedByMeaning(sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun lexemesOrderedByLessonNumber(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedByLessonNumber(sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun lexemesOrderedByRomaji(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedByRomaji(sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun lexemesOrderedById(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedById(sortOrder.index)
}