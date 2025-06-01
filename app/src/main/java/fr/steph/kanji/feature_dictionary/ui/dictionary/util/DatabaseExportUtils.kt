package fr.steph.kanji.feature_dictionary.ui.dictionary.util

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import fr.steph.kanji.core.domain.enumeration.LexemeType
import fr.steph.kanji.core.domain.model.DatabaseModel
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.domain.model.Lexeme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object DatabaseExportUtils {

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

    private const val A4_PAGE_WIDTH = 595
    private const val A4_PAGE_HEIGHT = 842

    fun exportDictionaryToPdf(context: Context, dictionary: List<Lexeme>) = with(PdfConfig()){
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()

        var currentPage = document.startPage(pageInfo)
        var y = 80

        currentPage.canvas.drawText("Dictionary", (pageWidth / 2).toFloat(), pageStartingHeight.toFloat(), titlePaint)

        val partWidth = effectiveWidth / 6
        val columnWidths = listOf(partWidth, partWidth * 2, partWidth * 3)
        val columnStarts = mutableListOf(documentMargin)
        for (i in 1 until columnWidths.size) {
            columnStarts.add(columnStarts[i - 1] + columnWidths[i - 1])
        }

        for (translation in dictionary.sortedBy { it.romaji }) {
            val columnTexts = listOf(translation.characters, translation.romaji, translation.meaning)

            val wrappedColumns = columnTexts.mapIndexed { index, text ->
                breakTextIntoLines(text, contentPaint, columnWidths[index])
            }

            val maxLines = wrappedColumns.maxOfOrNull { it.size } ?: 1

            if (y + maxLines * lineHeight > maxStartingHeight) {
                document.finishPage(currentPage)
                currentPage = document.startPage(pageInfo)
                y = pageStartingHeight
            }

            for ((columnIndex, lines) in wrappedColumns.withIndex()) {
                val x = columnStarts[columnIndex].toFloat()
                lines.forEachIndexed { i, line ->
                    currentPage.canvas.drawText(line, x, y + i * lineHeight.toFloat(), contentPaint)
                }
            }

            y += maxLines * lineHeight + lineMargin
        }

        document.finishPage(currentPage)
        sharePdf(context, document)
    }

    private fun breakTextIntoLines(text: String, paint: Paint, maxWidth: Int): List<String> {
        if (text.isBlank()) return listOf("")

        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in text.split(" ")) {
            val candidate = if (currentLine.isEmpty()) word else "$currentLine $word"

            when {
                paint.measureText(candidate) <= maxWidth -> currentLine = candidate
                paint.measureText(word) <= maxWidth -> {
                    lines.add(currentLine)
                    currentLine = word
                }
                else -> {
                    if (currentLine.isNotEmpty()) lines.add(currentLine)
                    val brokenWord = breakLongWord(word, paint, maxWidth)
                    lines.addAll(brokenWord.dropLast(1))
                    currentLine = brokenWord.last()
                }
            }
        }

        if (currentLine.isNotEmpty())
            lines.add(currentLine)

        return lines
    }

    private fun breakLongWord(word: String, paint: Paint, maxWidth: Int): List<String> {
        val parts = mutableListOf<String>()
        var start = 0

        while (start < word.length) {
            var end = word.length
            while (end > start + 1 && paint.measureText(word.substring(start, end) + "-") > maxWidth) {
                end--
            }

            val isLast = end == word.length
            val segment = word.substring(start, end) + if (!isLast) "-" else ""
            parts.add(segment)
            start = end
        }

        return parts
    }

    private fun sharePdf(context: Context, document: PdfDocument) {
        val downloads = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val pdfFile = File(downloads, "Dictionary.pdf")

        try {
            FileOutputStream(pdfFile).use { stream ->
                document.writeTo(stream)
            }

            val pdfUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                setDataAndType(pdfUri, "application/pdf")
                putExtra(Intent.EXTRA_STREAM, pdfUri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            context.startActivity(Intent.createChooser(intent, "Share dictionary"))
        } catch (e: IOException) {
            Toast.makeText(context, "Error creating PDF", Toast.LENGTH_LONG).show()
        }
    }

    private data class PdfConfig(
        val pageWidth: Int = A4_PAGE_WIDTH,
        val pageHeight: Int = A4_PAGE_HEIGHT,
        val documentMargin: Int = 20,
        val pageStartingHeight: Int = 40,
        val lineHeight: Int = 18,
        val lineMargin: Int = 10,
        val columnCount: Int = 3,
        val maxStartingHeight: Int = 800,
    ) {
        val effectiveWidth = pageWidth - 2 * documentMargin

        val titlePaint = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val contentPaint = Paint().apply {
            textSize = 12f
            isAntiAlias = true
        }
    }
}