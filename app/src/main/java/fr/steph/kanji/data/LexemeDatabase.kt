package fr.steph.kanji.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.steph.kanji.data.dao.LexemeDao
import fr.steph.kanji.data.model.Compound
import fr.steph.kanji.data.model.Kana
import fr.steph.kanji.data.model.Kanji
import fr.steph.kanji.data.utils.RoomTypeConverter

@Database(entities = [Kana::class, Kanji::class, Compound::class], version = 1)
@TypeConverters(value = [RoomTypeConverter::class])
abstract class LexemeDatabase: RoomDatabase() {
    abstract fun dao(): LexemeDao

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