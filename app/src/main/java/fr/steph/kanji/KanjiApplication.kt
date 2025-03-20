package fr.steph.kanji

import android.app.Application
import fr.steph.kanji.core.data.LexemeDatabase
import fr.steph.kanji.core.data.repository.ApiKanjiRepository
import fr.steph.kanji.core.data.repository.LessonRepository
import fr.steph.kanji.core.data.repository.LexemeRepository

class KanjiApplication: Application() {
    private val database by lazy { LexemeDatabase.getDatabase(this) }
    val lexemeRepository by lazy { LexemeRepository(database.lexemeDao()) }
    val lessonRepository by lazy { LessonRepository(database.lessonDao()) }
    val apiRepository by lazy { ApiKanjiRepository() }
}