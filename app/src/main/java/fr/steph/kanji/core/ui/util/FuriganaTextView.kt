package fr.steph.kanji.core.ui.util

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.max

class FuriganaTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ViewGroup(context, attrs) {

    private var furiganas: List<FuriganaPair> = emptyList()

    fun setFurigana(furiganas: List<FuriganaPair>) {
        this.furiganas = furiganas
        removeAllViews()
        buildViews()
        requestLayout()
    }

    private fun buildViews() {
        for (furigana in furiganas) {
            val verticalLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }

            val topText = TextView(context).apply {
                text = furigana.top
                textSize = 12.5f
                setTextColor(Color.GRAY)
                gravity = Gravity.CENTER_HORIZONTAL
            }

            val bottomText = TextView(context).apply {
                text = furigana.bottom
                textSize = 25f
                setTextColor(Color.BLACK)
            }

            verticalLayout.addView(topText)
            verticalLayout.addView(bottomText)
            addView(verticalLayout)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var totalWidth = 0
        var maxHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            totalWidth += child.measuredWidth
            maxHeight = max(maxHeight, child.measuredHeight)
        }

        setMeasuredDimension(
            resolveSize(totalWidth, widthMeasureSpec),
            resolveSize(maxHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(p0: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var currentLeft = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val width = child.measuredWidth
            val height = child.measuredHeight
            child.layout(currentLeft, 0, currentLeft + width, height)
            currentLeft += width
        }
    }

    data class FuriganaPair(
        val bottom: String,
        val top: String,
    )
}
