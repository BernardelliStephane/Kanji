package fr.steph.kanji.core.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PathMeasure
import android.graphics.RectF
import androidx.core.graphics.PathParser
import fr.steph.kanji.core.util.extension.extractPaths
import java.io.BufferedReader

object StrokeOrderDiagramHelper {

    private val pathPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 8f
    }

    private val startDotPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    fun createStrokeOrderDiagram(context: Context, filename: String, size: Int): List<Bitmap> {
        val paths = extractPathsFromSVG(context, filename)
        val bounds = computeBounds(paths)
        val matrix = calculateMatrixFromBounds(bounds, size)

        return generateStrokeBitmaps(paths, size, matrix)
    }

    private fun extractPathsFromSVG(context: Context, filename: String): List<String> {
        val file = context.assets.open("kanji/$filename")
        val fileContent = file.bufferedReader().use(BufferedReader::readText)
        val paths = fileContent.extractPaths()

        if (paths.isEmpty()) throw NoPathFoundException()

        return paths
    }

    private fun calculateMatrixFromBounds(bounds: RectF, size: Int): Matrix {
        val scaleX = size / bounds.width()
        val scaleY = size / bounds.height()

        val scale = minOf(scaleX, scaleY) * 0.75f

        return Matrix().apply {
            setScale(scale, scale)
            postTranslate(
                (size - bounds.width() * scale) / 2 - bounds.left * scale,
                (size - bounds.height() * scale) / 2 - bounds.top * scale
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

    private fun generateStrokeBitmaps(paths: List<String>, size: Int, matrix: Matrix): List<Bitmap> {
        return List(paths.size) { i ->
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            for (j in 0..i) {
                val path = PathParser.createPathFromPathData(paths[j]).apply { transform(matrix) }
                pathPaint.color = if (j==i) Color.BLACK else Color.LTGRAY

                if (j==i) {
                    val pathMeasure = PathMeasure(path, false)
                    val pos = FloatArray(2)

                    if (pathMeasure.getPosTan(0f, pos, null))
                        canvas.drawCircle(pos[0], pos[1], 16f, startDotPaint)
                }

                canvas.drawPath(path, pathPaint)
            }

            bitmap
        }
    }

    class NoPathFoundException(message: String = "No valid paths found") : Exception(message)
}