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

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun lexemesOrderedById(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedById(sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun lexemesOrderedByRomaji(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedByRomaji(sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun lexemesOrderedByTranslation(sortOrder: SortOrder) =
        lexemeDao.lexemesOrderedByMeaning(sortOrder.index)
}