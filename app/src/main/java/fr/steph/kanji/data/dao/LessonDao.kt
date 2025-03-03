package fr.steph.kanji.data.dao

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import fr.steph.kanji.data.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonDao {

    @Upsert
    suspend fun upsertLesson(lesson: Lesson): Long

    @Delete
    suspend fun deleteLesson(lesson: Lesson): Int

    @Query("SELECT * FROM lesson ORDER BY number ASC ")
    fun getAllLessons(): Flow<List<Lesson>>
}