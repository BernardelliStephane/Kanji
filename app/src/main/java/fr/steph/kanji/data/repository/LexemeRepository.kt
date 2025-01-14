package fr.steph.kanji.data.repository

import androidx.annotation.WorkerThread
import fr.steph.kanji.data.dao.LexemeDao
import fr.steph.kanji.data.model.Kanji
import fr.steph.kanji.data.utils.enumeration.SortOrder

class LexemeRepository(private val lexemeDao: LexemeDao) {

    suspend fun upsertKanji(kanji: Kanji) =
        lexemeDao.upsertLexeme(kanji)

    suspend fun deleteKanji(kanji: Kanji) =
        lexemeDao.deleteLexeme(kanji)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLexemesOrderedByTimestamp(sortOrder: SortOrder) =
        lexemeDao.getLexemesOrderedByTimestamp(sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLexemesOrderedByRomaji(sortOrder: SortOrder) =
        lexemeDao.getLexemesOrderedByRomaji(sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLexemesOrderedByTranslation(sortOrder: SortOrder) =
        lexemeDao.getLexemesOrderedByTranslation(sortOrder.index)
}