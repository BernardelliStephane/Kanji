package fr.steph.kanji.feature_dictionary.ui.util

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import fr.steph.kanji.feature_dictionary.ui.util.StrokeOrderDiagramConfig.Companion.fromContext

/**
 * Configuration utility for stroke order diagrams.
 *
 * This class computes and provides all necessary measurements for laying out
 * stroke order diagrams.
 *
 * Use [fromContext] to instantiate this class with runtime display metrics.
 */
class StrokeOrderDiagramConfig private constructor(
    /** The width available in the parent layout to draw the diagrams in pixels. */
    val availableWidth: Int,
    /** The size of each diagram square in pixels. */
    val diagramSize: Int,
    /** The thickness of the border of each diagram cell, in pixels. */
    val diagramBorderThickness: Int,
    /** The size of each diagram in pixels, excluding the border. */
    val borderlessDiagramSize: Int,
    /** The number of diagram items that fit per row. */
    val itemsPerRow: Int,
    /** Unused screen space in pixels after filling the maximum items per row. */
    val emptySpace: Int,
) {
    companion object {
        private const val STROKE_ORDER_DIAGRAM_SIZE_DP = 100
        private const val STROKE_ORDER_DIAGRAM_BORDER_RATIO = 30F / 800

        /**
         * Creates an instance of [StrokeOrderDiagramConfig] using the provided [context].
         *
         * Performs dp-to-px conversion, calculates border thickness, computes the
         * number of items per row, and determines horizontal padding to center layout.
         *
         * @param context Android context for accessing display metrics.
         * @return A fully initialized [StrokeOrderDiagramConfig] instance.
         */
        fun fromContext(context: Context, parent: ViewGroup): StrokeOrderDiagramConfig {
            val displayMetrics = context.resources.displayMetrics

            val diagramSize = displayMetrics.density.toInt() * STROKE_ORDER_DIAGRAM_SIZE_DP
            val diagramBorderThickness = (diagramSize * STROKE_ORDER_DIAGRAM_BORDER_RATIO).toInt()
            val borderlessDiagramSize = diagramSize - diagramBorderThickness

            val availableWidth = parent.run { width - paddingStart - paddingEnd - marginStart - marginEnd }
            val itemsPerRow = availableWidth.minus(diagramSize) / borderlessDiagramSize + 1
            val emptySpace = (availableWidth - diagramSize) % borderlessDiagramSize

            return StrokeOrderDiagramConfig(
                diagramSize = diagramSize,
                availableWidth = availableWidth,
                diagramBorderThickness = diagramBorderThickness,
                borderlessDiagramSize = borderlessDiagramSize,
                itemsPerRow = itemsPerRow,
                emptySpace = emptySpace
            )
        }
    }
}