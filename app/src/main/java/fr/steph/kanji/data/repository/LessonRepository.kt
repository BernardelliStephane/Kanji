package fr.steph.kanji.data.repository

import fr.steph.kanji.data.dao.LessonDao
import fr.steph.kanji.domain.model.Lesson

class LessonRepository(private val lessonDao: LessonDao) {

    suspend fun insertLesson(lesson: Lesson) =
        lessonDao.insertLesson(lesson)

    suspend fun deleteLesson(lesson: Lesson) =
        lessonDao.deleteLesson(lesson)

    fun allLessons() = lessonDao.allLessons()
}