package fr.steph.kanji

import android.app.Application
import fr.steph.kanji.data.KanjiDatabase
import fr.steph.kanji.data.repository.KanjiRepository

class KanjiApplication: Application() {
    val database by lazy { KanjiDatabase.getDatabase(this) }
    val repository by lazy { KanjiRepository(database.dao()) }
}