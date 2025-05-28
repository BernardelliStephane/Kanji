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

    fun downloadDatabase(context: Context) {
        val dbFile = context.getDatabasePath("lexeme_db")
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val destFile = File(downloadsDir, "dictionary.db")
        dbFile.copyTo(destFile, overwrite = true)

        MediaScannerConnection.scanFile(context, arrayOf(destFile.absolutePath), null, null)

        Toast.makeText(context, R.string.dictionary_saved, Toast.LENGTH_SHORT).show()
    }

    fun shareDatabase(context: Context) {
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