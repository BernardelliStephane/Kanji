package fr.steph.kanji

import android.app.Application
import fr.steph.kanji.data.LexemeDatabase
import fr.steph.kanji.data.repository.ApiKanjiRepository
import fr.steph.kanji.data.repository.LexemeRepository

class KanjiApplication: Application() {
    private val database by lazy { LexemeDatabase.getDatabase(this) }
    val repository by lazy { LexemeRepository(database.dao()) }
    val apiRepository by lazy { ApiKanjiRepository() }
}