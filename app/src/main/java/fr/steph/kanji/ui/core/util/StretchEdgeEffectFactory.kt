package fr.steph.kanji.ui.core.util

import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

class StretchEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {
    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return object : EdgeEffect(view.context) {
            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                val scale = 1f + deltaDistance * 0.5f
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    child.scaleY = scale
                }
            }

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