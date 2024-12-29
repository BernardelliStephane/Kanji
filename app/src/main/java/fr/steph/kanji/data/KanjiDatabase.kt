package fr.steph.kanji.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.steph.kanji.data.dao.KanjiDao
import fr.steph.kanji.data.model.Kanji

@Database(
    entities = [Kanji::class],
    version = 1
)
abstract class KanjiDatabase: RoomDatabase() {
    abstract val dao: KanjiDao

    companion object {
        @Volatile
        private var INSTANCE: KanjiDatabase? = null

        fun getDatabase(context: Context): KanjiDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KanjiDatabase::class.java,
                    "kanji_db"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}