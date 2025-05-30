package fr.steph.kanji.feature_dictionary.ui.dictionary.util

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import fr.steph.kanji.R
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
}