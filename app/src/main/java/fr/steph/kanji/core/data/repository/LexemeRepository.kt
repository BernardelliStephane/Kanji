package fr.steph.kanji.core.data.repository

import fr.steph.kanji.core.data.dao.LexemeDao
import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.domain.enumeration.SortOrder

class LexemeRepository(private val lexemeDao: LexemeDao) {

    suspend fun insertLexeme(lexeme: Lexeme) =
        lexemeDao.insertLexeme(lexeme)

    suspend fun updateLexeme(lexeme: Lexeme) =
        lexemeDao.updateLexeme(lexeme)

    suspend fun deleteLexemesFromSelection(selection: List<Long>) =
        lexemeDao.deleteLexemesFromSelection(selection)

    fun getLexemeByCharacters(characters: String) =
        lexemeDao.getLexemeByCharacters(characters)

    fun getLexemeByCharactersAndMeaning(characters: String, meaning: String) =
        lexemeDao.getLexemeByCharactersAndMeaning(characters, meaning)


    /******************** All lexemes ********************/


    fun lexemesOrderedByMeaning(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedByMeaning(sortOrder.index)

    fun lexemesOrderedByLessonNumber(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedByLessonNumber(sortOrder.index)

    fun lexemesOrderedByRomaji(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedByRomaji(sortOrder.index)

    fun lexemesOrderedById(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedById(sortOrder.index)


    /***************** Filtered lexemes ******************/


    fun filterLexemesOrderedByMeaning(filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.filterLexemesOrderedByMeaning(filter, sortOrder.index)

    fun filterLexemesOrderedByLessonNumber(filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.filterLexemesOrderedByLessonNumber(filter, sortOrder.index)

    fun filterLexemesOrderedByRomaji(filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.filterLexemesOrderedByRomaji(filter, sortOrder.index)

    fun filterLexemesOrderedById(filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.filterLexemesOrderedById(filter, sortOrder.index)


    /********** Lexemes matching search query ***********/


    fun searchLexemesOrderedByMeaning(query: String, sortOrder: SortOrder) =
        lexemeDao.searchLexemesOrderedByMeaning(query, sortOrder.index)

    fun searchLexemesOrderedByLessonNumber(query: String, sortOrder: SortOrder) =
        lexemeDao.searchLexemesOrderedByLessonNumber(query, sortOrder.index)

    fun searchLexemesOrderedByRomaji(query: String, sortOrder: SortOrder) =
        lexemeDao.searchLexemesOrderedByRomaji(query, sortOrder.index)

    fun searchLexemesOrderedById(query: String, sortOrder: SortOrder) =
        lexemeDao.searchLexemesOrderedById(query, sortOrder.index)


    /****** Filtered lexemes matching search query *******/


    fun searchInFilteredLexemesOrderedByMeaning(query: String, filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.searchInFilteredLexemesOrderedByMeaning(query, filter, sortOrder.index)

    fun searchInFilteredLexemesOrderedByLessonNumber(query: String, filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.searchInFilteredLexemesOrderedByLessonNumber(query, filter, sortOrder.index)

    fun searchInFilteredLexemesOrderedByRomaji(query: String, filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.searchInFilteredLexemesOrderedByRomaji(query, filter, sortOrder.index)

    fun searchInFilteredLexemesOrderedById(query: String, filter: List<Long>, sortOrder: SortOrder) =
        lexemeDao.searchInFilteredLexemesOrderedById(query, filter, sortOrder.index)
}