package fr.steph.kanji.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.steph.kanji.core.data.dao.LessonDao
import fr.steph.kanji.core.data.dao.LexemeDao
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.util.LESSON_NONE_ID

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
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("INSERT INTO Lesson (number, label) VALUES (${LESSON_NONE_ID}, 'None')")
                    }
                }).build()

                INSTANCE = instance
                instance
            }
        }
    }
}