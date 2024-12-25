package fr.steph.kanji.data.repository

import androidx.annotation.WorkerThread
import fr.steph.kanji.data.dao.KanjiDao
import fr.steph.kanji.data.model.Kanji
import fr.steph.kanji.data.utils.enum.SortType

class KanjiRepository(private val kanjiDao: KanjiDao) {

    suspend fun upsertKanji(kanji: Kanji) =
        kanjiDao.upsertKanji(kanji)

    suspend fun deleteKanji(kanji: Kanji) =
        kanjiDao.deleteKanji(kanji)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getKanjisOrderedByRomaji(sortType: SortType) =
        kanjiDao.getKanjisOrderedByRomaji(sortType.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getKanjisOrderedById(sortType: SortType) =
        kanjiDao.getKanjisOrderedById(sortType.index)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getKanjisOrderedByTranslation(sortType: SortType) =
        kanjiDao.getKanjisOrderedByTranslation(sortType.index)
}