package fr.steph.kanji.core.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.PathParser
import fr.steph.kanji.core.util.extension.extractPaths
import java.io.BufferedReader

object StrokeOrderDiagramHelper {

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10f
    }

    fun createStrokeDiagrams(context: Context, filename: String, width: Int, height: Int): List<Bitmap> {
        val paths = extractPathsFromSVG(context, filename)
        val bounds = computeBounds(paths)
        val matrix = calculateMatrixFromBounds(bounds, width, height)

        return generateStrokeBitmaps(paths, width, height, matrix)
    }

    private fun extractPathsFromSVG(context: Context, filename: String): List<String> {
        val file = context.assets.open("kanji/$filename")
        val fileContent = file.bufferedReader().use(BufferedReader::readText)
        val paths = fileContent.extractPaths()

        if (paths.isEmpty()) throw NoPathFoundException()

        return paths
    }

    private fun calculateMatrixFromBounds(bounds: RectF, width: Int, height: Int): Matrix {
        val scaleX = width / bounds.width()
        val scaleY = height / bounds.height()

        val scale = minOf(scaleX, scaleY) * 0.8f

        return Matrix().apply {
            setScale(scale, scale)
            postTranslate(
                (width - bounds.width() * scale) / 2 - bounds.left * scale,
                (height - bounds.height() * scale) / 2 - bounds.top * scale
            )
        }
    }

    private fun computeBounds(paths: List<String>): RectF {
        val bounds = RectF()
        val tempBounds = RectF()

        for (pathData in paths) {
            val path = PathParser.createPathFromPathData(pathData)
            path.computeBounds(tempBounds, true)
            bounds.union(tempBounds)
        }

        return bounds
    }

    private fun generateStrokeBitmaps(paths: List<String>, width: Int, height: Int, matrix: Matrix): List<Bitmap> {
        return List(paths.size) { i ->
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            for (j in 0..i) {
                paint.color = if (j == i) Color.BLACK else Color.GRAY
                PathParser.createPathFromPathData(paths[j]).apply {
                    transform(matrix)
                    canvas.drawPath(this, paint)
                }
            }

            bitmap
        }
    }

    class NoPathFoundException(message: String = "No valid paths found") : Exception(message)
}