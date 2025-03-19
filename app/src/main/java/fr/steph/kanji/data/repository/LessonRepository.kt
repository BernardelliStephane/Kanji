package fr.steph.kanji.data.repository

import fr.steph.kanji.data.dao.LessonDao
import fr.steph.kanji.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

class LessonRepository(private val lessonDao: LessonDao) {

    suspend fun insertLesson(lesson: Lesson) =
        lessonDao.insertLesson(lesson)


    fun allLessons() = lessonDao.allLessons()
    val lessonNumbers: Flow<List<Long>>
        get() = lessonDao.lessonNumbers()
}