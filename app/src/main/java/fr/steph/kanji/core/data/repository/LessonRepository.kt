package fr.steph.kanji.core.data.repository

import fr.steph.kanji.core.data.dao.LessonDao
import fr.steph.kanji.core.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

class LessonRepository(private val lessonDao: LessonDao) {

    suspend fun insertLesson(lesson: Lesson) =
        lessonDao.insertLesson(lesson)

    val allLessons: Flow<List<Lesson>>
        get() = lessonDao.allLessons()

    val lessonNumbers: Flow<List<Long>>
        get() = lessonDao.lessonNumbers()
}