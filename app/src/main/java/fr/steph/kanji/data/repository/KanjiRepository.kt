package fr.steph.kanji.data.repository

import androidx.annotation.WorkerThread
import fr.steph.kanji.data.dao.KanjiDao
import fr.steph.kanji.data.model.Kanji
import fr.steph.kanji.data.utils.enum.SortOrder

class KanjiRepository(private val kanjiDao: KanjiDao) {

    suspend fun upsertKanji(kanji: Kanji) =
        kanjiDao.upsertKanji(kanji)

    suspend fun deleteKanji(kanji: Kanji) =
        kanjiDao.deleteKanji(kanji)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getKanjisOrderedByRomaji(sortOrder: SortOrder) =
        kanjiDao.getKanjisOrderedByRomaji(sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getKanjisOrderedById(sortOrder: SortOrder) =
        kanjiDao.getKanjisOrderedById(sortOrder.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getKanjisOrderedByTranslation(sortOrder: SortOrder) =
        kanjiDao.getKanjisOrderedByTranslation(sortOrder.index)
}