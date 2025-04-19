package fr.steph.kanji.core.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PathMeasure
import android.graphics.RectF
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.core.graphics.PathParser
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import fr.steph.kanji.R
import fr.steph.kanji.core.util.extension.extractPaths
import fr.steph.kanji.feature_dictionary.domain.exception.MissingStrokeOrderDiagramException
import fr.steph.kanji.feature_dictionary.ui.util.StrokeOrderDiagramConfig
import java.io.BufferedReader

object StrokeOrderDiagramHelper {

    private val pathPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 7f
    }

    private val startDotPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    fun createStrokeOrderDiagram(context: Context, filename: String?, config: StrokeOrderDiagramConfig): FlexboxLayout {
        val paths = extractPathsFromSVG(context, filename)
        val bounds = computeBounds(paths)
        val matrix = calculateMatrixFromBounds(bounds, config.diagramSize)
        val bitmaps = generateStrokeBitmaps(paths, config.diagramSize, matrix)

        return generateStrokeOrderDiagram(context, bitmaps, config)
    }

    private fun extractPathsFromSVG(context: Context, filename: String?): List<String> {
        if (filename == null) throw MissingStrokeOrderDiagramException()
        val file = context.assets.open("kanji/$filename")
        val fileContent = file.bufferedReader().use(BufferedReader::readText)
        val paths = fileContent.extractPaths()

        if (paths.isEmpty()) throw MissingStrokeOrderDiagramException()

        return paths
    }

    private fun calculateMatrixFromBounds(bounds: RectF, size: Int): Matrix {
        val scaleX = size / bounds.width()
        val scaleY = size / bounds.height()

        val scale = minOf(scaleX, scaleY) * 0.65f

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
                pathPaint.color = if (j == i) Color.BLACK else Color.LTGRAY

                if (j == i) {
                    val pathMeasure = PathMeasure(path, false)
                    val pos = FloatArray(2)

                    if (pathMeasure.getPosTan(0f, pos, null))
                        canvas.drawCircle(pos[0], pos[1], 10f, startDotPaint)
                }

                canvas.drawPath(path, pathPaint)
            }

            bitmap
        }
    }

    private fun generateStrokeOrderDiagram(context: Context, diagram: List<Bitmap>, config: StrokeOrderDiagramConfig): FlexboxLayout {
        val flexboxLayout = FlexboxLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            flexWrap = FlexWrap.WRAP
        }

        val inflater = LayoutInflater.from(context)

        diagram.forEachIndexed { index, bitmap ->
            val itemView = inflater.inflate(R.layout.item_stroke, flexboxLayout, false)
            val imageView = itemView.findViewById<ImageView>(R.id.stroke_image)
            imageView.setImageBitmap(bitmap)

            val layoutParams = itemView.layoutParams as MarginLayoutParams

            val row = index / config.itemsPerRow
            val column = index % config.itemsPerRow

            if (row > 0) layoutParams.topMargin = -config.diagramBorderThickness
            if (column > 0) layoutParams.marginStart = -config.diagramBorderThickness

            flexboxLayout.addView(itemView)
        }

        return flexboxLayout
    }
}