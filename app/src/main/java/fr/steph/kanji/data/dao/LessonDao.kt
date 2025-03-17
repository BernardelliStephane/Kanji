package fr.steph.kanji.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.steph.kanji.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLesson(lesson: Lesson): Long

    @Delete
    suspend fun deleteLesson(lesson: Lesson): Int

    @Query("SELECT * FROM lesson ORDER BY number ASC ")
    fun allLessons(): Flow<List<Lesson>>
}