package fr.steph.kanji.data.repository

import androidx.annotation.WorkerThread
import fr.steph.kanji.data.dao.LessonDao
import fr.steph.kanji.data.model.Lesson

class LessonRepository(private val lessonDao: LessonDao) {

    suspend fun upsertLesson(lesson: Lesson) =
        lessonDao.upsertLesson(lesson)

    suspend fun deleteLesson(lesson: Lesson) =
        lessonDao.deleteLesson(lesson)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllLessons() =
        lessonDao.getAllLessons()
}