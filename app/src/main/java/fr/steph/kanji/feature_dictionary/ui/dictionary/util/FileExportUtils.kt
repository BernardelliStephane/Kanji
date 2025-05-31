package fr.steph.kanji.feature_dictionary.ui.dictionary.util

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import fr.steph.kanji.core.domain.enumeration.LexemeType
import fr.steph.kanji.core.domain.model.DatabaseModel
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.domain.model.Lexeme
import java.io.File

object FileExportUtils {

    fun downloadDatabase(context: Context): Boolean {
        try {
            val dbPath = context.getDatabasePath("lexeme_db").absolutePath
            val cacheDir = context.cacheDir

            val dbFile = File(dbPath)
            val walFile = File("$dbPath-wal")
            val shmFile = File("$dbPath-shm")

            val tempDb = File(cacheDir, "temp_dictionary.db")
            val tempWal = File(cacheDir, "temp_dictionary.db-wal")
            val tempShm = File(cacheDir, "temp_dictionary.db-shm")

            dbFile.copyTo(tempDb, overwrite = true)
            if (walFile.exists()) walFile.copyTo(tempWal, overwrite = true)
            if (shmFile.exists()) shmFile.copyTo(tempShm, overwrite = true)

            SQLiteDatabase.openDatabase(tempDb.path, null, SQLiteDatabase.OPEN_READWRITE).use { tempDbHandle ->
                tempDbHandle.rawQuery("PRAGMA wal_checkpoint(FULL);", null).close()
            }

            tempWal.delete()
            tempShm.delete()

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destFile = File(downloadsDir, "dictionary.db")
            tempDb.copyTo(destFile, overwrite = true)

            tempDb.delete()

            MediaScannerConnection.scanFile(context, arrayOf(destFile.absolutePath), null, null)
            return true
        }
        catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun shareDatabaseFile(context: Context) {
        val dbFile = context.getDatabasePath("lexeme_db")
        val cachedDbFile = File(context.cacheDir, "dictionary.db")
        dbFile.copyTo(cachedDbFile, overwrite = true)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            cachedDbFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share database file"))
    }

    fun importDatabaseFromFile(context: Context, uri: Uri): DatabaseModel? {
        try {
            val dbFile = File.createTempFile("dictionary", ".db", context.cacheDir)
            context.contentResolver.openInputStream(uri)?.use { input ->
                dbFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: throw IllegalArgumentException("Cannot open input stream from URI")

            val db = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READONLY)

            val lessons = mutableListOf<Lesson>()
            db.rawQuery("SELECT * FROM Lesson", null).use { cursor ->
                while (cursor.moveToNext()) {
                    val number = cursor.getLong(cursor.getColumnIndexOrThrow("number"))
                    val label = cursor.getString(cursor.getColumnIndexOrThrow("label"))
                    lessons.add(Lesson(number, label))
                }
            }

            val lexemes = mutableListOf<Lexeme>()
            db.rawQuery("SELECT * FROM Lexeme", null).use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                    val type = LexemeType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("type")))
                    val lessonNumber = cursor.getLong(cursor.getColumnIndexOrThrow("lessonNumber"))
                    val characters = cursor.getString(cursor.getColumnIndexOrThrow("characters"))
                    val alternativeWritings = cursor.getString(cursor.getColumnIndexOrThrow("alternativeWritings"))
                    val romaji = cursor.getString(cursor.getColumnIndexOrThrow("romaji"))
                    val meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"))
                    val creationDate = cursor.getLong(cursor.getColumnIndexOrThrow("creationDate"))
                    lexemes.add(Lexeme(id, type, lessonNumber, characters, alternativeWritings, romaji, meaning, creationDate))
                }
            }

            db.close()
            dbFile.delete()

            return DatabaseModel(lessons, lexemes)
        }
        catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}