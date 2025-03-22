package fr.steph.kanji.core.ui.util

import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView


/**
 * A custom [RecyclerView.EdgeEffectFactory] that applies a stretch effect
 * to the RecyclerView's child views when overscrolling.
 *
 * This effect scales the child views vertically when the user pulls on the edges
 * of the RecyclerView and smoothly animates them back to their original size when released.
 */
class StretchEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {
    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return object : EdgeEffect(view.context) {

            /**
             * Called when the user pulls on the edge of the RecyclerView.
             * Increases the scale of child views based on the pull distance.
             *
             * @param deltaDistance The distance pulled, as a fraction of the view height.
             * @param displacement The displacement of the pull as a fraction of the view width.
             */
            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                val scale = 1f + deltaDistance * 0.5f
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    child.scaleY = scale
                }
            }

            /**
             * Called when the user releases the edge.
             * Animates child views back to their original scale.
             */
            override fun onRelease() {
                super.onRelease()
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    child.animate().scaleY(1f).setDuration(200).start()
                }
            }
        }
    }
}