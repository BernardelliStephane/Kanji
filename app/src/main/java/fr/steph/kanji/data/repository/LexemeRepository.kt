package fr.steph.kanji.data.repository

import androidx.annotation.WorkerThread
import fr.steph.kanji.data.dao.LexemeDao
import fr.steph.kanji.data.model.Lexeme
import fr.steph.kanji.data.utils.enumeration.SortOrder

class LexemeRepository(private val lexemeDao: LexemeDao) {

    suspend fun insertLexeme(lexeme: Lexeme) =
        lexemeDao.insertLexeme(lexeme)

    suspend fun updateLexeme(lexeme: Lexeme) =
        lexemeDao.updateLexeme(lexeme)

    suspend fun deleteLexeme(lexeme: Lexeme) =
        lexemeDao.deleteLexeme(lexeme)

    suspend fun deleteLexemesFromSelection(selection: List<Long>) =
        lexemeDao.deleteLexemesFromSelection(selection)


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


    /***************** Filtered lexemes ******************/


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun filterLexemesOrderedByMeaning(filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.filterLexemesOrderedByMeaning(filter, sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun filterLexemesOrderedByLessonNumber(filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.filterLexemesOrderedByLessonNumber(filter, sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun filterLexemesOrderedByRomaji(filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.filterLexemesOrderedByRomaji(filter, sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun filterLexemesOrderedById(filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.filterLexemesOrderedById(filter, sortOrder.index)


    /********** Lexemes matching search query ***********/


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


    /****** Filtered lexemes matching search query *******/


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun searchInFilteredLexemesOrderedByMeaning(query: String, filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.searchInFilteredLexemesOrderedByMeaning(query, filter, sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun searchInFilteredLexemesOrderedByLessonNumber(query: String, filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.searchInFilteredLexemesOrderedByLessonNumber(query, filter, sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun searchInFilteredLexemesOrderedByRomaji(query: String, filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.searchInFilteredLexemesOrderedByRomaji(query, filter, sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun searchInFilteredLexemesOrderedById(query: String, filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.searchInFilteredLexemesOrderedById(query, filter, sortOrder.index)
}