package fr.steph.kanji.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.steph.kanji.data.dao.LessonDao
import fr.steph.kanji.data.dao.LexemeDao
import fr.steph.kanji.data.model.Lesson
import fr.steph.kanji.data.model.Lexeme

@Database(entities = [Lexeme::class, Lesson::class], version = 1)
abstract class LexemeDatabase: RoomDatabase() {
    abstract fun lexemeDao(): LexemeDao
    abstract fun lessonDao(): LessonDao

    companion object {
        @Volatile
        private var INSTANCE: LexemeDatabase? = null

        fun getDatabase(context: Context): LexemeDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LexemeDatabase::class.java,
                    "lexeme_db"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}